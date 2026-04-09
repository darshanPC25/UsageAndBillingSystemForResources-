package com.w3villa.usageandbillingsystemforaresource.controller;

import com.w3villa.usageandbillingsystemforaresource.model.Bill;
import com.w3villa.usageandbillingsystemforaresource.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillingService billingService;

    @GetMapping("/{billId}")
    public Bill getBill(@PathVariable Integer billId) {
        return billingService.getBill(billId);
    }

    @GetMapping
    public List<Bill> getAllBills() {
        return billingService.getAllBills();
    }
}
