package com.w3villa.usageandbillingsystemforaresource.repository;

import com.w3villa.usageandbillingsystemforaresource.model.UsageSession;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SessionRepository {
    private final Map<Integer, UsageSession> store = new LinkedHashMap<>();

//     Save or update a session
    public UsageSession save(UsageSession session) {
        store.put(session.getSessionId(), session);
        return session;
    }

//    Find by Id
    public Optional<UsageSession> findById(Integer id) {
        return Optional.ofNullable(store.get(id));
    }

//     Return all sessions (active + ended)
    public List<UsageSession> findAll() {
        return new ArrayList<>(store.values());
    }

//     Return only active sessions
    public List<UsageSession> findAllActive() {
        return store.values().stream()
                .filter(UsageSession::isActive)
                .collect(Collectors.toList());
    }

//     Return all sessions for specific user
    public List<UsageSession> findByUserId(Integer userId) {
        return store.values().stream()
                .filter(s -> s.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

//     Return all sessions for specific resource
    public List<UsageSession> findByResourceId(Integer resourceId) {
        return store.values().stream()
                .filter(s -> s.getResourceId().equals(resourceId))
                .collect(Collectors.toList());
    }

//     Check if a session exists or not
    public boolean existsById(Integer id) {
        return store.containsKey(id);
    }

//     Clear all data
    public void clear() {
        store.clear();
    }
}
