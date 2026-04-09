package com.w3villa.usageandbillingsystemforaresource.repository;

import com.w3villa.usageandbillingsystemforaresource.model.Bill;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BillRepository {
    private final Map<Integer, Bill> store = new LinkedHashMap<>();

//    Save a bill
    public Bill save(Bill bill) {
        store.put(bill.getBillId(), bill);
        return bill;
    }

//     Find by ID
    public Optional<Bill> findById(Integer id) {
        return Optional.ofNullable(store.get(id));
    }

//    Return all bills
    public List<Bill> findAll() {
        return new ArrayList<>(store.values());
    }

//     Return all bills for a specific user
    public List<Bill> findByUserId(Integer userId) {
        return store.values().stream()
                .filter(b -> b.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

//     Return all bills for a specific resource
    public List<Bill> findByResourceId(Integer resourceId) {
        return store.values().stream()
                .filter(b -> b.getResourceId().equals(resourceId))
                .collect(Collectors.toList());
    }

//     Get total revenue across all bills
    public double totalRevenue() {
        return store.values().stream()
                .mapToDouble(Bill::getTotalAmountInr)
                .sum();
    }

//     Check if a bill exists or not
    public boolean existsById(Integer id) {
        return store.containsKey(id);
    }

//     clear Data
    public void clear() {
        store.clear();
    }
}
