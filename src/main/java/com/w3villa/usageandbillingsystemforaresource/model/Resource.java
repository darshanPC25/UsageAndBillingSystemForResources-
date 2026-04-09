package com.w3villa.usageandbillingsystemforaresource.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Resource {

    private Integer resourceId;
    private String resourceName;
    private Integer resourceCapacity;
    private List<Services> services;
    private List<UsageSession> activeSession;

    public Resource(String resourceName, Integer capacity) {
        this.resourceName = resourceName;
        this.resourceCapacity = capacity;
    }

    public boolean isFull() {
        return activeSession.size() == resourceCapacity;
    }
}
