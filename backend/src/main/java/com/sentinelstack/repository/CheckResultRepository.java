package com.sentinelstack.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sentinelstack.entity.CheckResult;

public interface CheckResultRepository extends JpaRepository<CheckResult, Long> {

    default List<CheckResult> findNewestFirst() {
        return findAll(Sort.by(Sort.Direction.DESC, "checkedAt"));
    }
}
