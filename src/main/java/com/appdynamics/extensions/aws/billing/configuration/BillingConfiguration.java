package com.appdynamics.extensions.aws.billing.configuration;

import com.appdynamics.extensions.aws.config.Configuration;

import java.util.List;

public class BillingConfiguration extends Configuration {

    private List<String> serviceNames;

    public List<String> getServiceNames() {
        return serviceNames;
    }

    public void setServiceNames(List<String> serviceNames) {
        this.serviceNames = serviceNames;
    }
}
