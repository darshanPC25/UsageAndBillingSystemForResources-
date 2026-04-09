package com.w3villa.usageandbillingsystemforaresource.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Bill {
    private Integer billId;
    private Integer sessionId;
    private Integer userId;
    private Integer resourceId;
    private Integer serviceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double duration;
    private Integer billableHours;
    private Integer firstHoursPrice;
    private Integer additionalHoursPrice;
    private double totalAmount;

    public Bill(Integer sessionId, Integer userId, Integer resourceId, Integer serviceId, LocalDateTime startTime, LocalDateTime endTime, double duration, Integer billableHours, Integer firstHoursPrice, Integer additionalHoursPrice, double totalAmount) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.resourceId = resourceId;
        this.serviceId = serviceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.billableHours = billableHours;
        this.firstHoursPrice = firstHoursPrice;
        this.additionalHoursPrice = additionalHoursPrice;
        this.totalAmount = totalAmount;
    }

    public double getTotalAmountInr() {
        return totalAmount;
    }
}
