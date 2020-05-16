/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elasticache;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.appdynamics.extensions.aws.billing.BillingMonitor;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.TaskOutput;

public class BillingMonitorITest {
	
	private BillingMonitor classUnderTest = new BillingMonitor();
	
	@Test
	public void testMetricsCollectionCredentialsEncrypted() throws Exception {
		Map<String, String> args = Maps.newHashMap();
		args.put("config-file","src/test/resources/conf/itest-encrypted-config.yml");
		
		TaskOutput result = classUnderTest.execute(args, null);
		assertTrue(result.getStatusMessage().contains("successfully completed"));
	}
	
	@Test
	public void testMetricsCoyllectionWithProxy() throws Exception {
		Map<String, String> args = Maps.newHashMap();
		args.put("config-file","src/test/resources/conf/itest-proxy-config.yml");
		
		TaskOutput result = classUnderTest.execute(args, null);
		assertTrue(result.getStatusMessage().contains("successfully completed"));
	}	
}
