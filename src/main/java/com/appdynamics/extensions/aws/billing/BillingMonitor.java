/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.billing;

import com.appdynamics.extensions.aws.SingleNamespaceCloudwatchMonitor;
import com.appdynamics.extensions.aws.billing.configuration.BillingConfiguration;
import com.appdynamics.extensions.aws.billing.processors.BillingMetricsProcessor;
import com.appdynamics.extensions.aws.collectors.NamespaceMetricStatisticsCollector;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.google.common.collect.Lists;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public class BillingMonitor extends SingleNamespaceCloudwatchMonitor<BillingConfiguration> {
    private static final Logger LOGGER = ExtensionsLoggerFactory.getLogger(BillingMonitor.class);

    public BillingMonitor() {
        super(BillingConfiguration.class);
    }

    @Override
    protected NamespaceMetricStatisticsCollector getNamespaceMetricsCollector(BillingConfiguration billingConfiguration) {
        MetricsProcessor metricsProcessor = createMetricsProcessor(billingConfiguration);

        return new NamespaceMetricStatisticsCollector
                .Builder(billingConfiguration.getAccounts(),
                billingConfiguration.getConcurrencyConfig(),
                billingConfiguration.getMetricsConfig(),
                metricsProcessor,
                billingConfiguration.getMetricPrefix())
                .withCredentialsDecryptionConfig(billingConfiguration.getCredentialsDecryptionConfig())
                .withProxyConfig(billingConfiguration.getProxyConfig())
                .build();
    }

    private MetricsProcessor createMetricsProcessor(BillingConfiguration billingConfiguration){
        return new BillingMetricsProcessor(billingConfiguration);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected String getDefaultMetricPrefix() {
        return "Custom Metrics|Amazon Billing|";
    }

    @Override
    public String getMonitorName() {
        return "AWS Billing Monitor";
    }

    @Override
    protected List<Map<String, ?>> getServers() {
        return Lists.newArrayList();
    }
}
