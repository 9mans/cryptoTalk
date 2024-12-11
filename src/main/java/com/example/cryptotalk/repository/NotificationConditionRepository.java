package com.example.cryptotalk.repository;

import com.example.cryptotalk.entity.NotificationCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationConditionRepository extends JpaRepository<NotificationCondition, Long> {
    List<NotificationCondition> findByIsActiveTrue();
}
