/**
 * Copyright (c) 2015 Carnegie Mellon University.
 * All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS," WITH NO WARRANTIES WHATSOEVER.
 * CARNEGIE MELLON UNIVERSITY EXPRESSLY DISCLAIMS TO THE FULLEST 
 * EXTENT PERMITTEDBY LAW ALL EXPRESS, IMPLIED, AND STATUTORY 
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND 
 * NON-INFRINGEMENT OF PROPRIETARY RIGHTS.

 * This Program is distributed under a BSD license.  
 * Please see license.txt file or permission@sei.cmu.edu for more
 * information. 
 * 
 * DM-0003411
 */


package edu.cmu.emfta.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import edu.cmu.emfta.actions.Activator;

public class PreferencesValues {

	public static String getReportType() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return (store.getString(PreferenceConstants.REPORT_TYPE));
	}

	public static boolean useReportSinglePage() {
		return (getReportType().equalsIgnoreCase(PreferenceConstants.REPORT_TYPE_SINGLEPAGE));
	}

	public static boolean useReportMultiPages() {
		return (getReportType().equalsIgnoreCase(PreferenceConstants.REPORT_TYPE_MULTIPAGES));
	}

	public static String getAnalysisType() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return (store.getString(PreferenceConstants.ANALYSIS_TYPE));
	}

	public static boolean useAnalysisQuantitative() {
		return (getAnalysisType().equalsIgnoreCase(PreferenceConstants.ANALYSIS_TYPE_QUANTITATIVE));
	}

	public static boolean useAnalysisQualitative() {
		return (getAnalysisType().equalsIgnoreCase(PreferenceConstants.ANALYSIS_TYPE_QUALITATIVE));
	}
}
