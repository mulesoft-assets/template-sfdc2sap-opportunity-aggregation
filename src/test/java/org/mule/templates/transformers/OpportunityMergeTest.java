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
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.api.MuleContext;
import org.mule.api.transformer.TransformerException;
import org.mule.templates.utils.VariableNames;

@RunWith(MockitoJUnitRunner.class)
public class OpportunityMergeTest {

	@Mock
	private MuleContext muleContext;
	
	@Test
	public void testMerge() throws TransformerException {
		List<Map<String, String>> sfdcList = prepareSFDCopportunities();
		List<List<Map<String, String>>> sapList = prepareSAPopportunities();

		OpportunityMerge oppMerge = new OpportunityMerge();
		List<Map<String, String>> mergedList = oppMerge.mergeList(sfdcList, sapList);
				
		assertTwoLists( "The merged list obtained is not as expected", createExpectedList(), mergedList);
	}
	
	public static void assertTwoLists(String message ,List<Map<String, String>> expectedList, List<Map<String, String>> actualList) {
		
		for ( int i=0; i< expectedList.size(); i++) {			
			Iterator<Entry<String, String>> it = expectedList.get(i).entrySet().iterator();
			
		    while (it.hasNext()) {
		        Entry<String, String> mapEntry =it.next();		        
		        Assert.assertEquals(message,expectedList.get(i).get( mapEntry.getValue()), actualList.get(i).get(mapEntry.getValue()));		        
		    }
		}
	}

	static List<Map<String, String>> prepareSFDCopportunities(){
		List<Map<String, String>> opportunitiesSalesforce = new ArrayList<Map<String,String>>();

		Map<String, String> opportunity0Salesforce = new HashMap<String, String>();
		opportunity0Salesforce.put(VariableNames.ID, "0");
		opportunity0Salesforce.put(VariableNames.NAME, "Name0");
		opportunitiesSalesforce.add(opportunity0Salesforce);

		Map<String, String> opportunity1Salesforce = new HashMap<String, String>();
		opportunity1Salesforce.put(VariableNames.ID, "1");
		opportunity1Salesforce.put(VariableNames.NAME, "Name1");
		opportunitiesSalesforce.add(opportunity1Salesforce);
		
		return opportunitiesSalesforce;
	}
	
	static List<List<Map<String, String>>> prepareSAPopportunities(){
		List<List<Map<String, String>>> salesOrdersSap = new ArrayList<List<Map<String, String>>>();
		
		List<Map<String, String>> statusHeaders = new ArrayList<Map<String, String>>();
		HashMap<String, String> sh1 = new HashMap<String, String>();
		sh1.put(VariableNames.ID, "1");
		sh1.put(VariableNames.STATUS, "null");
		HashMap<String, String> sh2 = new HashMap<String, String>();
		sh2.put(VariableNames.ID, "2");
		sh2.put(VariableNames.STATUS, "null");
		statusHeaders.add(sh1);
		statusHeaders.add(sh2);
		salesOrdersSap.add(statusHeaders);
		
		List<Map<String, String>> textLines = new ArrayList<Map<String, String>>();
		HashMap<String, String> tl1 = new HashMap<String, String>();
		tl1.put("Line", "Name1");
		tl1.put("TextName", "Name1");
		HashMap<String, String> tl2 = new HashMap<String, String>();
		tl2.put("Line", "Name2");
		tl2.put("TextName", "Name2");
		textLines.add(tl1);
		textLines.add(tl2);
		salesOrdersSap.add(textLines);
		
		List<Map<String, String>> textHeaders = new ArrayList<Map<String, String>>();
		HashMap<String, String> th1 = new HashMap<String, String>();
		th1.put(VariableNames.ID, "1");
		th1.put("TextName", "Name1");
		HashMap<String, String> th2 = new HashMap<String, String>();
		th2.put(VariableNames.ID, "2");
		th2.put("TextName", "Name2");
		textHeaders.add(th1);
		textHeaders.add(th2);
		salesOrdersSap.add(textHeaders);	
				
		return salesOrdersSap;
	}		
	
	static List<Map<String, String>> createExpectedList() {
		Map<String, String> record0 = new HashMap<String, String>();
		record0.put(VariableNames.ID_IN_SALESFORCE, "0");
		record0.put(VariableNames.ID_IN_SAP, "");
		record0.put(VariableNames.NAME, "Name0");
		record0.put(VariableNames.STATUS, null);
		record0.put(VariableNames.PROBABILITY, null);
		record0.put(VariableNames.AMOUNT, null);
		
		Map<String, String> record1 = new HashMap<String, String>();
		record1.put(VariableNames.ID_IN_SALESFORCE, "1");
		record1.put(VariableNames.ID_IN_SAP, "1");
		record1.put(VariableNames.NAME, "Name1");
		record1.put(VariableNames.STATUS, null);
		record1.put(VariableNames.PROBABILITY, null);
		record1.put(VariableNames.AMOUNT, null);
		
		Map<String, String> record2 = new HashMap<String, String>();
		record2.put(VariableNames.ID_IN_SALESFORCE, "");
		record2.put(VariableNames.ID_IN_SAP, "2");
		record2.put(VariableNames.NAME, "Name2");
		record2.put(VariableNames.STATUS, null);
		record2.put(VariableNames.PROBABILITY, null);
		record2.put(VariableNames.AMOUNT, null);
		
		List<Map<String, String>> expectedList = new ArrayList<Map<String, String>>();
		expectedList.add(record0);
		expectedList.add(record1);
		expectedList.add(record2);
		
		return expectedList;
	}

}
