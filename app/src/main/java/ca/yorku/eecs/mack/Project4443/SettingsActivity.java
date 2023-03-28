package ca.yorku.eecs.mack.Project4443;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends Activity implements OnSharedPreferenceChangeListener
{
	private static final String MYDEBUG = "MYDEBUG"; // for Log.i messages

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Display the fragment as the activity's main content.
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
	}

	/*
	 * We're not doing anything when a Setting is changed, but we're implementing the callback
	 * anyway. The approach in this demo to deal with the changes to settings in the main
	 * application activity after SettingsActivity terminates. See onActivityResult in
	 * DemoQuotationActivity.
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		// build keys (makes the code more readable)
		final String MODE_KEY = getBaseContext().getString(R.string.pref_quiz_mode);

		if (key.equals(MODE_KEY))
		{
			Log.i(MYDEBUG, "VIBRATE setting changed: New value=" + sharedPreferences.getBoolean(key, true));

		}
	}

	/*
	 * As recommended in the Android API Guide, the listener is registered/unregistered in the
	 * onResume and onPause methods. This is "for proper lifecycle management". See...
	 * 
	 * http://developer.android.com/guide/topics/ui/settings.html#Listening
	 * 
	 * Unfortunately, the code given in the API Guide generates a runtime error. This is perhaps
	 * because the class here is defined with "extends Activity" rather than
	 * "extends PreferenceActivity". Using "extends Activity" is recommended for Android 3.0 or
	 * later. The following code runs fine.
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
	}
}