package com.w3villa.usageandbillingsystemforaresource.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UsageSession {
    private Integer sessionId;
    private Integer resourceId;
    private Integer userId;
    private Integer serviceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isActive;

    public UsageSession(Integer resourceId, Integer userId, Integer serviceId) {
        this.resourceId = resourceId;
        this.userId = userId;
        this.serviceId = serviceId;
    }

    public boolean isActive() {
        return isActive;
    }
}
