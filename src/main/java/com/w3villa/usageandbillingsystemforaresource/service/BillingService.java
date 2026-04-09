package com.w3villa.usageandbillingsystemforaresource.service;

import com.w3villa.usageandbillingsystemforaresource.exception.ResourceConflictException;
import com.w3villa.usageandbillingsystemforaresource.exception.ResourceNotFoundException;
import com.w3villa.usageandbillingsystemforaresource.model.Bill;
import com.w3villa.usageandbillingsystemforaresource.model.Resource;
import com.w3villa.usageandbillingsystemforaresource.model.Services;
import com.w3villa.usageandbillingsystemforaresource.model.UsageSession;
import com.w3villa.usageandbillingsystemforaresource.exception.ResourceConflictException;
import com.w3villa.usageandbillingsystemforaresource.exception.ResourceNotFoundException;
import com.w3villa.usageandbillingsystemforaresource.repository.BillRepository;
import com.w3villa.usageandbillingsystemforaresource.repository.ResourceRepository;
import com.w3villa.usageandbillingsystemforaresource.repository.SessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class BillingService {
    private final ResourceRepository resourceRepo;
    private final SessionRepository sessionRepo;
    private final BillRepository billRepo;

    public Resource createResource(String name, Integer capacity) {
        if (capacity < 1)
            throw new IllegalArgumentException("Capacity must be at least 1.");
        Resource resource = new Resource(name.trim(), capacity);
        return resourceRepo.save(resource);
    }

    public Resource addService(Integer resourceId, String serviceName,
                               Integer firstHourCost, Integer additionalHourCost) {
        Resource resource = getResource(resourceId);
        if (firstHourCost < 0 || additionalHourCost < 0)
            throw new IllegalArgumentException("Costs cannot be negative.");
        Services service = new Services(serviceName.trim(), firstHourCost, additionalHourCost);
        resource.getServices().add(service);
        return resource;
    }

    public Resource getResource(Integer resourceId) {
        return resourceRepo.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource not found: " + resourceId));
    }

    public List<Resource> getAllResources() {
        return resourceRepo.findAll();
    }

    public UsageSession startUsage(Integer resourceId, Integer userId, Integer serviceId) {
        Resource resource = getResource(resourceId);
        getServiceFromResource(resource, serviceId); // validates service exists

        if (resource.isFull())
            throw new ResourceConflictException(
                    "Resource '" + resource.getResourceName() + "' is fully occupied ("
                            + resource.getServices() + "/" + resource.getResourceCapacity()
                            + " slots in use). Request rejected.");

        boolean alreadyActive = resource.getActiveSession().stream()
                .anyMatch(s -> s.getUserId().equals(userId));
        if (alreadyActive)
            throw new ResourceConflictException(
                    "User '" + userId + "' already has an active session on this resource.");

        UsageSession session = new UsageSession(resourceId, userId, serviceId);
        resource.getActiveSession().add(session);
        sessionRepo.save(session);
        return session;
    }

    public record StopResult(UsageSession session, Bill bill) {}

    public StopResult stopUsage(Integer sessionId) {
        UsageSession session = getSession(sessionId);

        if (!session.isActive())
            throw new ResourceConflictException(
                    "Session '" + sessionId + "' has already ended.");

        session.setEndTime(LocalDateTime.now());
        session.setIsActive(false);

        Resource resource = getResource(session.getResourceId());
        resource.getActiveSession()
                .removeIf(s -> s.getSessionId().equals(sessionId));

        Bill bill = calculateBill(session, resource);
        billRepo.save(bill);

        return new StopResult(session, bill);
    }

    public UsageSession getSession(Integer sessionId) {
        return sessionRepo.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found: " + sessionId));
    }

    public List<UsageSession> getAllSessions() {
        return sessionRepo.findAll();
    }

    // ── Bill operations ──────────────────────────────────────────────────────

    public Bill getBill(Integer billId) {
        return billRepo.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bill not found: " + billId));
    }

    public List<Bill> getAllBills() {
        return billRepo.findAll();
    }

    private Bill calculateBill(UsageSession session, Resource resource) {
        Services service = getServiceFromResource(resource, session.getServiceId());

        long seconds = ChronoUnit.SECONDS.between(
                session.getStartTime(), session.getEndTime());
        double durationMinutes = seconds / 60.0;
        int billableHours = Math.max(1, (int) Math.ceil(durationMinutes / 60.0));

        double total;
        if (billableHours == 1) {
            total = service.getFirstHoursPrice();
        } else {
            total = service.getFirstHoursPrice()
                    + (billableHours - 1) * service.getAdditionalHoursPrice();
        }

        return new Bill(
                session.getSessionId(),
                session.getUserId(),
                session.getResourceId(),
                session.getServiceId(),
                session.getStartTime(),
                session.getEndTime(),
                durationMinutes,
                billableHours,
                service.getFirstHoursPrice(),
                service.getAdditionalHoursPrice(),
                total
        );
    }

    private Services getServiceFromResource(
            Resource resource, Integer serviceId) {
        return resource.getServices().stream()
                .filter(s -> s.getServiceId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service '" + serviceId + "' not found on resource '"
                                + resource.getResourceId() + "'."));
    }
}
