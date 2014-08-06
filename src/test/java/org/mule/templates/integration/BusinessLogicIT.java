/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */
package org.mule.templates.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		flow.initialise();

		MuleEvent event = flow.process(getTestEvent("", MessageExchangePattern.REQUEST_RESPONSE));
		Set<String> flowVariables = event.getFlowVariableNames();

		Assert.assertTrue("The variable " + VariableNames.OPPORTUNITIES_FROM_SALESFORCE + " is missing.", flowVariables.contains(VariableNames.OPPORTUNITIES_FROM_SALESFORCE));
		Assert.assertTrue("The variable " + VariableNames.SALES_ORDERS_FROM_SAP + " is missing.", flowVariables.contains(VariableNames.SALES_ORDERS_FROM_SAP));

		Iterator<?> opportunitiesFromSalesforce = event.getFlowVariable(VariableNames.OPPORTUNITIES_FROM_SALESFORCE);
		Collection<?> salesOrdersFromSap = event.getFlowVariable(VariableNames.SALES_ORDERS_FROM_SAP);

		Assert.assertTrue("There should be opportunities in the variable " + VariableNames.OPPORTUNITIES_FROM_SALESFORCE + ".", opportunitiesFromSalesforce.hasNext());
		Assert.assertTrue("There should be sales orders in the variable " + VariableNames.SALES_ORDERS_FROM_SAP + ".", !salesOrdersFromSap.isEmpty());
	}

}
