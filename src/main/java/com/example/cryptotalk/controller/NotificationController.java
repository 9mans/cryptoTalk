package com.example.cryptotalk.controller;

import com.example.cryptotalk.entity.NotificationCondition;
import com.example.cryptotalk.repository.NotificationConditionRepository;
import com.example.cryptotalk.util.AESUtil;
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
    private final AESUtil aesUtil;

    public NotificationController(NotificationConditionRepository conditionRepository, AESUtil aesUtil) {
        this.conditionRepository = conditionRepository;
        this.aesUtil = aesUtil;
    }

    @GetMapping("/notifications/new")
    public String showNotificationForm(Model model) {
        model.addAttribute("notification", new NotificationCondition());
        return "notification-form";
    }


    @PostMapping("/notifications")
    public String addNotification(NotificationCondition condition) {

        try {
            condition.setPhoneNumber(aesUtil.encrypt(condition.getPhoneNumber()));
            condition.setActive(true);
            condition.setCreatedAt(LocalDateTime.now());
            conditionRepository.save(condition);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/notifications/new?error";
        }

        return "redirect:/notifications/new?success";
    }
}
