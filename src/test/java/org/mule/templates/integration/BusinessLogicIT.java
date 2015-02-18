/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */
package org.mule.templates.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.templates.utils.VariableNames;
import org.mule.util.UUID;

import com.sforce.soap.partner.SaveResult;

/**
 * The objective of this class is to validate the correct behavior of the flows
 * for this Mule Template that make calls to external systems.
 * 
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {

	private List<Map<String, Object>> createdOpportunitiesInSalesforce = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> createdSalesOrdersInSap = new ArrayList<Map<String, Object>>();

	@BeforeClass
	public static void init() {
		System.setProperty("mail.subject", "Opportunities/Sales Orders Report");
		System.setProperty("mail.body", "Please find attached your Opportunities/Sales Orders Report");
		System.setProperty("attachment.name", "OpportunitiesSalesOrdersReport.csv");
	}
	
	@Before
	public void setUp() throws Exception {
		createOpportunities();
	}

	@After
	public void tearDown() throws Exception {
		deleteTestOpportunityFromSandBox(createdOpportunitiesInSalesforce, "deleteOpportunitiesFromSalesforceFlow");
		deleteTestOpportunityFromSandBox(createdSalesOrdersInSap, "deleteSalesOrdersFromSapFlow");
	}

	@Override
	protected String getConfigResources() {
		return super.getConfigResources() + getTestFlows();
	}

	private void createOpportunities() throws Exception {
		SubflowInterceptingChainLifecycleWrapper createOpportunitiesInSalesforceFlow = getSubFlow("createOpportunitiesInSalesforceFlow");
		createOpportunitiesInSalesforceFlow.initialise();

		Map<String, Object> salesforceOpportunity = new HashMap<String, Object>();
		salesforceOpportunity.put("Name", "SFDC_0_" + TEMPLATE_NAME + "_" + UUID.getUUID());
		createdOpportunitiesInSalesforce.add(salesforceOpportunity);

		MuleEvent event = createOpportunitiesInSalesforceFlow.process(getTestEvent(createdOpportunitiesInSalesforce, MessageExchangePattern.REQUEST_RESPONSE));
		List<?> results = (List<?>) event.getMessage().getPayload();
		for (int i = 0; i < results.size(); i++) {
			createdOpportunitiesInSalesforce.get(i).put(VariableNames.ID, ((SaveResult) results.get(i)).getId());
		}

		Map<String, Object> sapSalesOrder = new HashMap<String, Object>();
		sapSalesOrder.put(VariableNames.NAME, "SAP_0_" + TEMPLATE_NAME + "_" + UUID.getUUID());
		sapSalesOrder.put(VariableNames.ID, "sfdc2sap-opp-agg-" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX));
		createdSalesOrdersInSap.add(sapSalesOrder);

		MuleEvent event1 = runFlow("createSalesOrdersInSapFlow", createdSalesOrdersInSap);
		
		List<?> results1 = (List<?>) event1.getMessage().getPayload();
		
		// assign Sap-generated IDs
		for (int i = 0; i < createdSalesOrdersInSap.size(); i++) {
			createdSalesOrdersInSap.get(i).put(VariableNames.ID, results1.get(i));
		}
	}

	protected void deleteTestOpportunityFromSandBox(List<Map<String, Object>> createdOpportunities, String deleteFlow) throws Exception {
		List<String> idList = new ArrayList<String>();

		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow(deleteFlow);
		flow.initialise();
		for (Map<String, Object> c : createdOpportunities) {
			idList.add((String) c.get(VariableNames.ID));
		}
		flow.process(getTestEvent(idList, MessageExchangePattern.REQUEST_RESPONSE));
	}

	@Test
	public void testGatherDataFlow() throws Exception {
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("gatherDataFlow");
		flow.setMuleContext(muleContext);
		flow.initialise();
		flow.start();
		MuleEvent event = flow.process(getTestEvent("", MessageExchangePattern.REQUEST_RESPONSE));
		Iterator<Map<String, String>> mergedList = (Iterator<Map<String, String>>)event.getMessage().getPayload();
		
		Assert.assertTrue("There should be opportunities from source A or source B.", mergedList.hasNext());
	}


}
