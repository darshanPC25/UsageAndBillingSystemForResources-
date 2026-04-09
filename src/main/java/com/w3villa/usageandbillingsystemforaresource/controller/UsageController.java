package com.w3villa.usageandbillingsystemforaresource.controller;

import com.w3villa.usageandbillingsystemforaresource.model.UsageSession;
import com.w3villa.usageandbillingsystemforaresource.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class UsageController {

    private final BillingService billingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsageSession startSession(@RequestBody StartSessionRequest request) {
        return billingService.startUsage(request.resourceId(), request.userId(), request.serviceId());
    }

    @PostMapping("/{sessionId}/stop")
    public BillingService.StopResult stopSession(@PathVariable Integer sessionId) {
        return billingService.stopUsage(sessionId);
    }

    @GetMapping("/{sessionId}")
    public UsageSession getSession(@PathVariable Integer sessionId) {
        return billingService.getSession(sessionId);
    }

    @GetMapping
    public List<UsageSession> getAllSessions() {
        return billingService.getAllSessions();
    }

    public record StartSessionRequest(Integer resourceId, Integer userId, Integer serviceId) {}
}
