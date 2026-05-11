package com.sentinelstack;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class SentinelStackApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void healthReturnsUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void metricsAreAccessible() throws Exception {
        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("jvm_memory")));
    }

    @Test
    void targetCanBeCreatedAndRetrieved() throws Exception {
        MvcResult created = mockMvc.perform(post("/targets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Example",
                                "url", "https://example.com"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Example"))
                .andReturn();

        long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/targets/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://example.com"));
    }

    @Test
    void invalidUrlIsRejected() throws Exception {
        mockMvc.perform(post("/targets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Bad",
                                "url", "ftp://example.com"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void manualCheckIsStoredAndReturnedAsLatest() throws Exception {
        HttpServer server = startLocalServer();
        try {
            String url = "http://localhost:%d/ok".formatted(server.getAddress().getPort());
            MvcResult created = mockMvc.perform(post("/targets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "name", "Local",
                                    "url", url))))
                    .andExpect(status().isCreated())
                    .andReturn();
            JsonNode target = objectMapper.readTree(created.getResponse().getContentAsString());

            mockMvc.perform(post("/checks/run/{targetId}", target.get("id").asLong()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.available").value(true));

            mockMvc.perform(get("/checks/latest"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].targetName").value("Local"))
                    .andExpect(jsonPath("$[0].available").value(true));
        } finally {
            server.stop(0);
        }
    }

    private static HttpServer startLocalServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/ok", SentinelStackApplicationTests::handleOk);
        server.start();
        return server;
    }

    private static void handleOk(HttpExchange exchange) throws IOException {
        byte[] response = "ok".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, response.length);
        try (var body = exchange.getResponseBody()) {
            body.write(response);
        }
    }
}
