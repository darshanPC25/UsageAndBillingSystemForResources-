package com.w3villa.usageandbillingsystemforaresource.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Services {
    private Integer serviceId;
    private String serviceName;
    private Integer firstHoursPrice;
    private Integer additionalHoursPrice;

    public Services(String serviceName, Integer firstHoursPrice, Integer additionalHoursPrice) {
        this.serviceName = serviceName;
        this.firstHoursPrice = firstHoursPrice;
        this.additionalHoursPrice = additionalHoursPrice;
    }


}
