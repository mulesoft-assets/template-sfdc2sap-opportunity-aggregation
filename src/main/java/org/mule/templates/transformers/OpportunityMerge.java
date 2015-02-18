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

import org.apache.commons.lang3.StringUtils;
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
	List<Map<String, String>> mergeList(List<Map<String, String>> opportunitiesFromSalesforce, List<Map<String, String>> salesOrdersFromSap) {
		List<Map<String, String>> mergedOpportunityList = new ArrayList<Map<String, String>>();

		// Put all opportunities from Salesforce in the merged mergedOpportunityList
		for (Map<String, String> opportunityFromSalesforce : opportunitiesFromSalesforce) {
			Map<String, String> mergedOpportunity = createMergedOpportunity(opportunityFromSalesforce);
			mergedOpportunity.put(VariableNames.ID_IN_SALESFORCE, opportunityFromSalesforce.get(VariableNames.ID));
			mergedOpportunityList.add(mergedOpportunity);
		}

		// Add the new opportunities from SAP and update the exiting ones
		for (Map<String, String> salesOrderFromSap : salesOrdersFromSap) {
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
		mergedOpportunity.put(VariableNames.ID_IN_SALESFORCE, StringUtils.EMPTY);
		mergedOpportunity.put(VariableNames.ID_IN_SAP, StringUtils.EMPTY);
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

}
