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

import org.mule.templates.utils.VariableNames;

/**
 * This transformer will take two lists as input and create a third one that
 * will be the merge of the previous two. The identity of list's element is
 * defined by its Name.
 * 
 * @author damian.sima
 */
public class OpportunityMerge {

	/**
	 * The method will merge the opportunities from the two lists creating a new one.
	 * 
	 * @param opportunitiesFromOrgA
	 *            opportunities from organization A
	 * @param opportunitiesFromOrgB
	 *            opportunities from organization B
	 * @return a list with the merged content of the to input lists
	 */
	List<Map<String, String>> mergeList(List<Map<String, String>> opportunitiesFromSalesforce, List<List<Map<String, String>>> salesOrdersFromSap) {
		List<Map<String, String>> mergedOpportunityList = new ArrayList<Map<String, String>>();

		// Put all opportunities from Salesforce in the merged mergedOpportunityList
		for (Map<String, String> opportunityFromSalesforce : opportunitiesFromSalesforce) {
			Map<String, String> mergedOpportunity = createMergedOpportunity(opportunityFromSalesforce);
			mergedOpportunity.put(VariableNames.ID_IN_SALESFORCE, opportunityFromSalesforce.get(VariableNames.ID));
			mergedOpportunityList.add(mergedOpportunity);
		}			
		
		// Add the new opportunities from SAP and update the exiting ones		
		List<Map<String, String>> preparedSalesOrdersFromSap = prepareSapSalesOrders(salesOrdersFromSap);
		for (Map<String, String> salesOrderFromSap : preparedSalesOrdersFromSap) {
			Map<String, String> opportunityFromSalesforce = findOpportunityInList(salesOrderFromSap.get(VariableNames.IDENTITY_FIELD_KEY), mergedOpportunityList);
			if (opportunityFromSalesforce != null) {
				opportunityFromSalesforce.put(VariableNames.ID_IN_SAP, salesOrderFromSap.get(VariableNames.ID));
				opportunityFromSalesforce.put("Status", salesOrderFromSap.get("Status"));
			} else {
				Map<String, String> mergedOpportunity = createMergedOpportunity(salesOrderFromSap);
				mergedOpportunity.put(VariableNames.ID_IN_SAP, salesOrderFromSap.get(VariableNames.ID));
				mergedOpportunityList.add(mergedOpportunity);
			}				
		}		
		
		return mergedOpportunityList;
	}

	private static Map<String, String> createMergedOpportunity(Map<String, String> opportunity) {
		Map<String, String> mergedOpportunity = new HashMap<String, String>();
		mergedOpportunity.put(VariableNames.IDENTITY_FIELD_KEY, opportunity.get(VariableNames.IDENTITY_FIELD_KEY));
		mergedOpportunity.put(VariableNames.ID_IN_SALESFORCE, "");
		mergedOpportunity.put(VariableNames.ID_IN_SAP, "");
		mergedOpportunity.put("Amount", opportunity.get("Amount"));
		mergedOpportunity.put("Probability", opportunity.get("Probability"));
		mergedOpportunity.put("Status", opportunity.get("Status"));
		return mergedOpportunity;
	}

	private static Map<String, String> findOpportunityInList(String opportunityName, List<Map<String, String>> opportunityList) {
		for (Map<String, String> opportunity : opportunityList) {
			String name = opportunity.get(VariableNames.IDENTITY_FIELD_KEY);
			if (name != null && name.equals(opportunityName)) {
				return opportunity;
			}
		}
		return null;
	}
	
	private List<Map<String, String>> prepareSapSalesOrders( List<List<Map<String, String>>> salesOrdersFromSap) {
		
		HashMap<String, Map<String, String>> soMap = new HashMap<String, Map<String, String>>();
		
		List<Map<String, String>> statusHeaders = salesOrdersFromSap.get(0);
		for (final Map<String, String> statusHeader: statusHeaders)
			soMap.put(statusHeader.get("Id"), new HashMap<String, String>() {{						
				put("Status", statusHeader.get("Status"));
				put("Id", statusHeader.get("Id"));
			}});
				
		Map<String, String> textNameToLine = new HashMap<String, String>();
		for (Map<String, String> textLine : salesOrdersFromSap.get(1)) {
				textNameToLine.put(textLine.get("TextName"), textLine.get("Line"));
			}
			
		for (Map<String, String> textHeader : salesOrdersFromSap.get(2)) {					
			Map<String, String> item = soMap.get(textHeader.get("Id"));
			if (item != null)
				item.put("Name", textNameToLine.get( textHeader.get("TextName")));
		}
		
		return new ArrayList<Map<String, String>>(soMap.values());
	}
}
