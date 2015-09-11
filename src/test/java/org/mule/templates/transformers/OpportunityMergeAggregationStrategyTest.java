/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.routing.AggregationContext;
import org.mule.templates.integration.AbstractTemplateTestCase;

import com.google.common.collect.Lists;

@SuppressWarnings({ "unchecked" })
@RunWith(MockitoJUnitRunner.class)
public class OpportunityMergeAggregationStrategyTest extends AbstractTemplateTestCase {
	
	@Mock
	private MuleContext muleContext;
  
	
	@Test
	public void testAggregate() throws Exception {
		List<Map<String, String>> sfdcList = OpportunityMergeTest.prepareSFDCopportunities();
		Map<String, List> sapList = prepareSAPopportunities();
		
		MuleEvent testEventA = getTestEvent("");
		MuleEvent testEventB = getTestEvent("");
		
		testEventA.getMessage().setPayload(sfdcList.iterator());
		testEventB.getMessage().setPayload(sapList);
		
		List<MuleEvent> testEvents = new ArrayList<MuleEvent>();
		testEvents.add(testEventA);
		testEvents.add(testEventB);
		
		AggregationContext aggregationContext = new AggregationContext(getTestEvent(""), testEvents);
		
		OpportunityMergeAggregationStrategy oppMerge = new OpportunityMergeAggregationStrategy();
		Iterator<Map<String, String>> iterator = (Iterator<Map<String, String>>) oppMerge.aggregate(aggregationContext).getMessage().getPayload();
		List<Map<String, String>> mergedList = Lists.newArrayList(iterator);

		//Assert.assertEquals("The merged list obtained is not as expected", OpportunityMergeTest.createExpectedList(), mergedList);
		OpportunityMergeTest.assertTwoLists("The merged list obtained is not as expected", OpportunityMergeTest.createExpectedList(), mergedList);

	}
	
	private Map<String, List> prepareSAPopportunities() {
		
		List<List<Map<String, String>>> sapList = OpportunityMergeTest.prepareSAPopportunities();
		
		Map sapMap = new HashMap<String, List<List<Map<String, String>>>>();
		
		sapMap.put("StatusHeaders", sapList.get(0));
		sapMap.put("TextLines", sapList.get(1));
		sapMap.put("TextHeaders", sapList.get(2));
		
		return sapMap;
	}

}
