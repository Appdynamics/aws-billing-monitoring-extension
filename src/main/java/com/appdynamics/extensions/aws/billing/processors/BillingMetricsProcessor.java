/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.billing.processors;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.DimensionFilter;
import com.appdynamics.extensions.aws.billing.ServiceNamesPredicate;
import com.appdynamics.extensions.aws.billing.configuration.BillingConfiguration;
import com.appdynamics.extensions.aws.config.IncludeMetric;
import com.appdynamics.extensions.aws.dto.AWSMetric;
import com.appdynamics.extensions.aws.metric.*;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessorHelper;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.metrics.Metric;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Pattern;

/**
 * @author Satish Muddam
 */
public class BillingMetricsProcessor implements MetricsProcessor {

    private static final Logger logger = ExtensionsLoggerFactory.getLogger(BillingMetricsProcessor.class);
    private static final String NAMESPACE = "AWS/Billing";
    private static final String SERVICENAME = "ServiceName";
    //private static final String[] DIMENSIONS = {"Currency", "ServiceName"};
    private BillingConfiguration billingConfiguration;
    private List<IncludeMetric> includeMetrics;
    private List<String> serviceNamesList;

    public BillingMetricsProcessor(BillingConfiguration billingConfiguration){
        this.billingConfiguration = billingConfiguration;
        this.includeMetrics = billingConfiguration.getMetricsConfig().getIncludeMetrics();
        this.serviceNamesList = billingConfiguration.getServiceNames();
    }

    public List<AWSMetric> getMetrics(AmazonCloudWatch awsCloudWatch, String accountName, LongAdder awsRequestCounter) {
        List<DimensionFilter> dimensionFilters = getDimensionFilters();
        ServiceNamesPredicate serviceNamesPredicate = new ServiceNamesPredicate(serviceNamesList);
        return MetricsProcessorHelper.getFilteredMetrics(awsCloudWatch, awsRequestCounter, NAMESPACE, includeMetrics, dimensionFilters, serviceNamesPredicate);
    }

    private List<DimensionFilter> getDimensionFilters() {
        List<DimensionFilter> dimensionFilters = Lists.newArrayList();

        DimensionFilter serviceNameDimensionFilter = new DimensionFilter();
        serviceNameDimensionFilter.withName(SERVICENAME);
        dimensionFilters.add(serviceNameDimensionFilter);

        return dimensionFilters;
    }

    @Override
    public StatisticType getStatisticType(AWSMetric metric) {
        return MetricsProcessorHelper.getStatisticType(metric.getIncludeMetric(), includeMetrics);
    }

    public List<Metric> createMetricStatsMapForUpload(NamespaceMetricStatistics namespaceMetricStats) {
        List<Metric> stats = Lists.newArrayList();
        if(namespaceMetricStats != null) {
            for (AccountMetricStatistics accountMetricStatistics : namespaceMetricStats.getAccountMetricStatisticsList()) {
                for (RegionMetricStatistics regionMetricStatistics : accountMetricStatistics.getRegionMetricStatisticsList()) {
                    for (MetricStatistic metricStatistic : regionMetricStatistics.getMetricStatisticsList()) {
                        String metricPath = createMetricPath(accountMetricStatistics.getAccountName(), regionMetricStatistics.getRegion(), metricStatistic);
                        if (metricStatistic.getValue() != null) {
                            Map<String, Object> metricProperties = Maps.newHashMap();
                            AWSMetric awsMetric = metricStatistic.getMetric();
                            IncludeMetric includeMetric = awsMetric.getIncludeMetric();
                            metricProperties.put("alias", includeMetric.getAlias());
                            metricProperties.put("multiplier", includeMetric.getMultiplier());
                            metricProperties.put("aggregationType", includeMetric.getAggregationType());
                            metricProperties.put("timeRollUpType", includeMetric.getTimeRollUpType());
                            metricProperties.put("clusterRollUpType ", includeMetric.getClusterRollUpType());
                            metricProperties.put("delta", includeMetric.isDelta());
                            Metric metric = new Metric(includeMetric.getName(), Double.toString(metricStatistic.getValue()), metricStatistic.getMetricPrefix() + metricPath, metricProperties);
                            stats.add(metric);
                        } else {
                            logger.debug(String.format("Ignoring metric [ %s ] which has null value", metricPath));
                        }
                    }
                }
            }
        }
        return stats;
    }

    private String createMetricPath(String accountName, String region, MetricStatistic metricStatistic){
        AWSMetric awsMetric = metricStatistic.getMetric();
        IncludeMetric includeMetric = awsMetric.getIncludeMetric();
        com.amazonaws.services.cloudwatch.model.Metric metric = awsMetric.getMetric();
        String serviceName = null;
        String currency = null;

        for(Dimension dimension : metric.getDimensions()) {
            if(dimension.getName().equalsIgnoreCase("ServiceName")) {
                serviceName = dimension.getValue();
            }
            if(dimension.getName().equalsIgnoreCase("Currency")) {
                currency = dimension.getValue();
            }
        }
        //apiName will never be null
        StringBuilder stringBuilder = new StringBuilder(accountName)
                .append("|")
                .append(region)
                .append("|");
        if(serviceName != null) {
            stringBuilder.append(serviceName)
                    .append("|");
        }

        if(currency != null) {
            stringBuilder.append(currency)
                    .append("|");
        }
        stringBuilder.append(includeMetric.getName());
        return stringBuilder.toString();

    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

}
