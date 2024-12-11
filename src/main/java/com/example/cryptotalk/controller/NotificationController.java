package com.example.cryptotalk.controller;

import com.example.cryptotalk.entity.NotificationCondition;
import com.example.cryptotalk.repository.NotificationConditionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationConditionRepository conditionRepository;

    public NotificationController(NotificationConditionRepository conditionRepository) {
        this.conditionRepository = conditionRepository;
    }

    @PostMapping
    public NotificationCondition addNotification(@RequestBody NotificationCondition condition) {
        condition.setActive(true);
        condition.setCreatedAt(LocalDateTime.now());

        return conditionRepository.save(condition);
    }
}
