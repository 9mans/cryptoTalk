package com.example.cryptotalk.controller;

import com.example.cryptotalk.entity.NotificationCondition;
import com.example.cryptotalk.repository.NotificationConditionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
public class NotificationController {

    private final NotificationConditionRepository conditionRepository;

    public NotificationController(NotificationConditionRepository conditionRepository) {
        this.conditionRepository = conditionRepository;
    }

    @GetMapping("/notifications/new")
    public String showNotificationForm(Model model) {
        model.addAttribute("notification", new NotificationCondition());
        return "notification-form";
    }


    @PostMapping("/notifications")
    public String addNotification(NotificationCondition condition) {
        condition.setActive(true);
        condition.setCreatedAt(LocalDateTime.now());
        conditionRepository.save(condition);

        return "redirect:/notifications/new?success";
    }
}
