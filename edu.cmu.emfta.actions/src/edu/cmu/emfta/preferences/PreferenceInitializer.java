package edu.cmu.emfta.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.cmu.emfta.actions.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * Initialize defaults value for preferences of the plug-in
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		store.setDefault(PreferenceConstants.REPORT_TYPE, PreferenceConstants.REPORT_TYPE_SINGLEPAGE);
		store.setDefault(PreferenceConstants.ANALYSIS_TYPE, PreferenceConstants.ANALYSIS_TYPE_QUALITATIVE);
	}

}
