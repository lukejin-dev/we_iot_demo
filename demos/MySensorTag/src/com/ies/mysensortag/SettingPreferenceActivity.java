package com.ies.mysensortag;
import android.os.Bundle;
import android.preference.PreferenceActivity;


public class SettingPreferenceActivity extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        
        addPreferencesFromResource(R.xml.preference);
    }
}
