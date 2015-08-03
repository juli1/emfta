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
