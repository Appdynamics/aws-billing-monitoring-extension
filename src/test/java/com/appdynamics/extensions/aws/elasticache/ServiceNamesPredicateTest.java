/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elasticache;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.appdynamics.extensions.aws.billing.BillingMonitor;
import com.appdynamics.extensions.aws.billing.ServiceNamesPredicate;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class ServiceNamesPredicateTest {

	@Mock
	private Metric metric;

	@Mock
	private Dimension dimension;

	@Test
	public void matchedApiNameMetricShouldReturnTrue(){
		List<String> serviceNamesList = Lists.newArrayList("sampleName");
		ServiceNamesPredicate serviceNamesPredicate = new ServiceNamesPredicate(serviceNamesList);
		when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
		when(dimension.getValue()).thenReturn("sampleName");
		Assert.assertTrue(serviceNamesPredicate.apply(metric));

	}

	@Test
	public void unMatchedApiNameMetricShouldReturnFalse(){
		List<String> serviceNamesList = Lists.newArrayList("sampleName1", "sampleName2");
		ServiceNamesPredicate serviceNamesPredicate = new ServiceNamesPredicate(serviceNamesList);
		when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
		when(dimension.getValue()).thenReturn("sampleName");
		Assert.assertFalse(serviceNamesPredicate.apply(metric));

	}

	@Test
	public void emptyPredicateShouldReturnTrue(){
		List<String> serviceNamesList = Lists.newArrayList();
		ServiceNamesPredicate serviceNamesPredicate = new ServiceNamesPredicate(serviceNamesList);
		when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
		when(dimension.getValue()).thenReturn("sampleName");
		Assert.assertTrue(serviceNamesPredicate.apply(metric));

	}

	@Test
	public void nullPredicateShouldReturnTrue(){
		List<String> serviceNamesList = null;
		ServiceNamesPredicate serviceNamesPredicate = new ServiceNamesPredicate(serviceNamesList);
		when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
		when(dimension.getValue()).thenReturn("sampleName");
		Assert.assertTrue(serviceNamesPredicate.apply(metric));

	}

	@Test
	public void emptyApiNamesInListShouldReturnTrue(){
		List<String> serviceNamesList = Lists.newArrayList("", "");
		ServiceNamesPredicate serviceNamesPredicate = new ServiceNamesPredicate(serviceNamesList);
		when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
		when(dimension.getValue()).thenReturn("sampleName");
		Assert.assertTrue(serviceNamesPredicate.apply(metric));

	}

	@Test
	public void emptyApiNamesAndNonEmtyApiNamesInListShouldReturnTrueIfMatched(){
		List<String> serviceNamesList = Lists.newArrayList("sampleName", "");
		ServiceNamesPredicate serviceNamesPredicate = new ServiceNamesPredicate(serviceNamesList);
		when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
		when(dimension.getValue()).thenReturn("sampleName");
		Assert.assertTrue(serviceNamesPredicate.apply(metric));

	}

	@Test
	public void emptyApiNamesAndNonEmtyApiNamesInListShouldReturnFalseIfNotMatched(){
		List<String> serviceNamesList = Lists.newArrayList("sampleName$", "");
		ServiceNamesPredicate serviceNamesPredicate = new ServiceNamesPredicate(serviceNamesList);
		when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
		when(dimension.getValue()).thenReturn("sampleName1");
		Assert.assertFalse(serviceNamesPredicate.apply(metric));

	}
}

