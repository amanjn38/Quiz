package com.example.quiz;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.prefs.PreferencesFactory;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends PreferenceFragment {

  @Override
    public void onCreate(Bundle savedInstanceState){
      super.onCreate(savedInstanceState);

      addPreferencesFromResource(R.xml.quiz_preferences);
  }
}
