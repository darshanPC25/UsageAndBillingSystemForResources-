package com.w3villa.usageandbillingsystemforaresource.controller;

import com.w3villa.usageandbillingsystemforaresource.model.Resource;
import com.w3villa.usageandbillingsystemforaresource.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final BillingService billingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Resource createResource(@RequestBody CreateResourceRequest request) {
        return billingService.createResource(request.name(), request.capacity());
    }

    @PostMapping("/{resourceId}/services")
    @ResponseStatus(HttpStatus.CREATED)
    public Resource addService(@PathVariable Integer resourceId, @RequestBody AddServiceRequest request) {
        return billingService.addService(resourceId, request.serviceName(), request.firstHourCost(), request.additionalHourCost());
    }

    @GetMapping("/{resourceId}")
    public Resource getResource(@PathVariable Integer resourceId) {
        return billingService.getResource(resourceId);
    }

    @GetMapping
    public List<Resource> getAllResources() {
        return billingService.getAllResources();
    }

    public record CreateResourceRequest(String name, Integer capacity) {}
    public record AddServiceRequest(String serviceName, Integer firstHourCost, Integer additionalHourCost) {}
}
