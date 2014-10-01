/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */
package org.mule.templates.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.templates.utils.Utils;
import org.mule.templates.utils.VariableNames;

@RunWith(MockitoJUnitRunner.class)
public class ProductMergerTransformerTest {

	@Mock
	private MuleContext muleContext;

	@Test
	public void testMerge() throws TransformerException {
		List<Map<String, String>> opportunitiesSalesforce = new ArrayList<Map<String,String>>();

		Map<String, String> opportunity0Salesforce = new HashMap<String, String>();
		opportunity0Salesforce.put(VariableNames.ID, "0");
		opportunity0Salesforce.put(VariableNames.NAME, "Name0");
		opportunitiesSalesforce.add(opportunity0Salesforce);

		Map<String, String> opportunity1Salesforce = new HashMap<String, String>();
		opportunity1Salesforce.put(VariableNames.ID, "1");
		opportunity1Salesforce.put(VariableNames.NAME, "Name1");
		opportunitiesSalesforce.add(opportunity1Salesforce);
		
		List<Map<String, String>> salesOrdersSap = new ArrayList<Map<String,String>>();
		
		Map<String, String> salesOrder1Sap = new HashMap<String, String>();
		salesOrder1Sap.put(VariableNames.ID, "1");
		salesOrder1Sap.put(VariableNames.NAME, "Name1");
		salesOrdersSap.add(salesOrder1Sap);

		Map<String, String> salesOrder2Sap = new HashMap<String, String>();
		salesOrder2Sap.put(VariableNames.ID	, "2");
		salesOrder2Sap.put(VariableNames.NAME, "Name2");
		salesOrdersSap.add(salesOrder2Sap);

		MuleMessage message = new DefaultMuleMessage(null, muleContext);
		message.setInvocationProperty(VariableNames.OPPORTUNITIES_FROM_SALESFORCE, opportunitiesSalesforce.iterator());
		message.setInvocationProperty(VariableNames.SALES_ORDERS_FROM_SAP, salesOrdersSap.iterator());

		OpportunityMergerTransformer transformer = new OpportunityMergerTransformer();
		List<Map<String, String>> mergedList = Utils.buildList(transformer.transform(message, "UTF-8"));

		System.out.println(mergedList);
		Assert.assertEquals("The merged list obtained is not as expected", createExpectedList().size(), mergedList.size());
	}

	private List<Map<String, String>> createExpectedList() {
		Map<String, String> record0 = new HashMap<String, String>();
		record0.put(VariableNames.ID_IN_SALESFORCE, "0");
		record0.put(VariableNames.ID_IN_SAP, "");
		record0.put(VariableNames.NAME, "Name0");

		Map<String, String> record1 = new HashMap<String, String>();
		record1.put(VariableNames.ID_IN_SALESFORCE, "1");
		record1.put(VariableNames.ID_IN_SAP, "1");
		record1.put(VariableNames.NAME, "Name1");

		Map<String, String> record2 = new HashMap<String, String>();
		record2.put(VariableNames.ID_IN_SALESFORCE, "");
		record2.put(VariableNames.ID_IN_SAP, "2");
		record2.put(VariableNames.NAME, "Name2");

		List<Map<String, String>> expectedList = new ArrayList<Map<String, String>>();
		expectedList.add(record0);
		expectedList.add(record1);
		expectedList.add(record2);

		return expectedList;
	}

}
