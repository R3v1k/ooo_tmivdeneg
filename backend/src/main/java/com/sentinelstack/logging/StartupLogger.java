package com.sentinelstack.logging;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);

    private final DataSource dataSource;
    private final String instanceName;

    public StartupLogger(DataSource dataSource, @Value("${sentinel.instance-name:local}") String instanceName) {
        this.dataSource = dataSource;
        this.instanceName = instanceName;
    }

    @Override
    public void run(ApplicationArguments args) throws SQLException {
        log.info("Application startup completed instance={}", instanceName);
        try (Connection connection = dataSource.getConnection()) {
            log.info("Database connection verified url={}", connection.getMetaData().getURL());
        } catch (SQLException ex) {
            log.error("Database connection verification failed", ex);
            throw ex;
        }
    }
}
