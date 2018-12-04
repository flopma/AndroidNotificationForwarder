package lu.binarylogic.notificationforwarder;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

@TargetApi(Constants.ANDROID_API)
public class SettingsActivity extends PreferenceActivity {
    private static final String TAG = "SettingsActivity";

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);

                if (preference.getTitle().toString().contains("assword")) {
                    preference.setSummary("******");
                }
            }
            return true;
        }
    };


    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content
        addPreferencesFromResource(R.xml.settings);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        bindPreferenceSummaryToValue(findPreference("smtp-server"));
        bindPreferenceSummaryToValue(findPreference("smtp-port"));
        bindPreferenceSummaryToValue(findPreference("username"));
        bindPreferenceSummaryToValue(findPreference("password"));
        bindPreferenceSummaryToValue(findPreference("recipient"));

        if (this.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Application is requesting the READ PHONE STATE permission");
            this.requestPermissions(new String[] {Manifest.permission.READ_PHONE_STATE}, 0);
        } else {
            Log.v(TAG, "Application has the READ PHONE STATE permission");
        }

        if (this.checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Application is requesting the READ_CALL_LOG permission");
            this.requestPermissions(new String[] {Manifest.permission.READ_CALL_LOG}, 0);
        } else {
            Log.v(TAG, "Application has the READ_CALL_LOG permission");
        }
    }



}
