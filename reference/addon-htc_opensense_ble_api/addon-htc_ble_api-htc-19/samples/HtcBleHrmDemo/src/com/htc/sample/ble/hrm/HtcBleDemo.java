package com.htc.sample.ble.hrm;

import java.util.Locale;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.android.bluetooth.le.gatt.BleConstants;

public class HtcBleDemo extends FragmentActivity implements
		ActionBar.OnNavigationListener, OnInitListener {

	private String TAG = "HtcBleDemo";
	private Button mResetButton;
	private Button mBodySensorLocButton;
	private boolean appRegistered = false;
	private boolean mIsConnected = false;
	private BluetoothHrmClient mHeartRateMonitor = null;
	private BluetoothDevice mDevice = null;
	private BluetoothAdapter mBtAdapter;
	private TextView mTextView;
	private TextToSpeech tts;
	private TextView mSensorLocation;
	private TextView mSensorContact;
	private TextView mEnergyExpended;
	private TextView mRRInterval;
	private TextView mHeartrate;
	private ImageView mBlelogo;
	private int mBPM;

	private static final int REQUEST_SELECT_DEVICE = 0;
	private SharedPreferences mPrefs;
	private String mDeviceName;
	private String mDeviceAddress;
	private TextView mDeviceNameView;
	private boolean enableTTS;
	private CheckBox mAudio;
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_htc_ble_demo);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);

		mResetButton = (Button) findViewById(R.id.button2);
		mResetButton.setOnClickListener(mButtonListener);

		mBodySensorLocButton = (Button) findViewById(R.id.button3);
		mBodySensorLocButton.setOnClickListener(mButtonListener);

		mTextView = (TextView) findViewById(R.id.con_status);
		mSensorLocation = (TextView) findViewById(R.id.sensor_location);
		mSensorContact = (TextView) findViewById(R.id.sensor_contact_status);
		mEnergyExpended = (TextView) findViewById(R.id.energy_expended);
		mRRInterval = (TextView) findViewById(R.id.rr_interval);
		mHeartrate = (TextView) findViewById(R.id.heartrate);
		mBlelogo = (ImageView) findViewById(R.id.blelogo);
		tts = new TextToSpeech(this, this);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mDeviceName=mPrefs.getString("device","");
		mDeviceAddress=mPrefs.getString("address","");
		mDeviceNameView = (TextView) findViewById(R.id.device_name);
		mDeviceNameView.setText(mDeviceName+"-"+mDeviceAddress);
		mAudio = (CheckBox) findViewById(R.id.audio);
		enableTTS=mPrefs.getBoolean("audio", false);
		mAudio.setChecked(enableTTS);
		mAudio.setOnClickListener(new OnClickListener() {
			  @Override
			  public void onClick(View view) {
				if (((CheckBox) view).isChecked()) {
					enableTTS=true;
				} else {
					enableTTS=false;
				}
				Editor editor = mPrefs.edit();
				editor.putBoolean("audio", enableTTS);
				editor.apply();
			  }
			});

		// register receiver for the following client and service event actions: 
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothHrmClient.HRM_REGISTERED);
		intentFilter.addAction(BluetoothHrmClient.HRM_CONNECTED);
		intentFilter.addAction(BluetoothHrmClient.HRM_DISCONNECTED);
		intentFilter.addAction(BluetoothHrmService.HRM_MEASUREMENT);
		intentFilter.addAction(BluetoothHrmService.HRM_BODY_SENSOR_LOCATION);
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(regReceiver, intentFilter);

		// Ensure Bluetooth is enabled
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter != null) {
			if(!mBtAdapter.isEnabled()) {
				mBtAdapter.enable();
				Toast toast = Toast.makeText(this,"NOTE: Enabling Bluetooth!", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			} else {
				Log.i(TAG, "Registering HRM client");
	    		mHeartRateMonitor = new BluetoothHrmClient(HtcBleDemo.this);
			}
		} else {
			Toast toast = Toast.makeText(this,"NOTE: Bluetooth not supported!", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();			
		}
	}

	private final BroadcastReceiver regReceiver = new BroadcastReceiver() {

		public void onReceive(final Context context, final Intent intent) {
			if (intent.getAction().equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
			    if((Integer)(intent.getExtras().get(BluetoothAdapter.EXTRA_CONNECTION_STATE))
			    		==BluetoothAdapter.STATE_CONNECTED) {
			    	if(mHeartRateMonitor!=null) {
						Log.i(TAG, "Registering HRM client");
			    		mHeartRateMonitor = new BluetoothHrmClient(HtcBleDemo.this);
			    	}
			    }
		    } else if (intent.getAction().equals(BluetoothHrmClient.HRM_REGISTERED)) {
				Log.i(TAG, "Received HRM Registered Intent");
				appRegistered = true;
				if(mDeviceAddress.length()>0) {
					mTextView.setText("Connecting...");
					connectToDevice(mDeviceAddress);
				}
			} else if (intent.getAction().equals(
					BluetoothHrmClient.HRM_CONNECTED)) {
				mTextView.setTextColor(Color.BLUE);
				mBlelogo.setImageAlpha(60);
				mTextView.setText("Connected.");
				mIsConnected = true;
				mDeviceNameView.setText(mDeviceName+" "+mDeviceAddress);			
				Editor editor = mPrefs.edit();
				editor.putString("device", mDeviceName);
				editor.putString("address", mDeviceAddress);
				editor.apply();
			} else if (intent.getAction().equals(
					BluetoothHrmService.HRM_MEASUREMENT)) {
				int hrmMeasurementValue = intent.getIntExtra(
						BluetoothHrmService.EXTRA_HRMVALUE, Integer.MIN_VALUE);
				int energyExpendedValue = intent.getIntExtra(
						BluetoothHrmService.EXTRA_ENERGY_EXPENDED,
						Integer.MIN_VALUE);
				int[] rrIntervalValue = intent
						.getIntArrayExtra(BluetoothHrmService.EXTRA_RR_INTERVAL);
				int sensorContactStatus = intent.getIntExtra(
						BluetoothHrmService.EXTRA_HRMSENSORCONTACTSTATUS,
						Integer.MIN_VALUE);

				Log.i(TAG, " HRMVALUE = " + hrmMeasurementValue
						+ "  ENERGY_EXPENDED= " + energyExpendedValue
						+ "  RR_INTERVAL= " + rrIntervalValue[0]
						+ "  HRMSENSORCONTACTSTATUS = " + sensorContactStatus);

				mSensorContact.setText("");
				if (sensorContactStatus == 0 || sensorContactStatus == 1) {
					mSensorContact.setText("Not Supported");
				} else if (sensorContactStatus == 2) {
					mSensorContact.setText("Not Detected");
				} else if (sensorContactStatus == 3) {
					mSensorContact.setText("Detected");
				}

				if (energyExpendedValue == -1)
					mEnergyExpended.setText("Not Supported");
				else
					mEnergyExpended.setText(energyExpendedValue
							+ " kilo joules");

				if (rrIntervalValue != null) {
					StringBuffer stringBuffer = new StringBuffer();
					for (int i = 0; i < rrIntervalValue.length; i++) {
						if (rrIntervalValue[i] != -1) { // invalid RR interval value
							stringBuffer.append((rrIntervalValue[i] * 1000) / 1024);
							stringBuffer.append("  ");
						} else {
							stringBuffer.append("  ");
							break;
						}
					}
					mRRInterval.setText(stringBuffer.toString());
					if (rrIntervalValue.length == 1 && rrIntervalValue[0] == -1) {
						mRRInterval.setText("Not Supported");
					}
				}
				updateHeartPulse(hrmMeasurementValue);
			} else if (intent.getAction().equals(BluetoothHrmClient.HRM_DISCONNECTED)) {
				mTextView.setTextColor(Color.RED);
				mBlelogo.setImageAlpha(255);
				mTextView.setText("Disconnected.");
				mIsConnected = false;
			} else if (intent.getAction().equals(
					BluetoothHrmService.HRM_BODY_SENSOR_LOCATION)) {
				int location = intent.getIntExtra(BluetoothHrmService.EXTRA_LOCATION, Integer.MIN_VALUE);
				mSensorLocation.setText("N/A");
				switch (location) {
				case 0:
					mSensorLocation.setText("Other");
					break;
				case 1:
					mSensorLocation.setText("Chest");
					break;
				case 2:
					mSensorLocation.setText("Wrist");
					break;
				case 3:
					mSensorLocation.setText("Finger");
					break;
				case 4:
					mSensorLocation.setText("Hand");
					break;
				case 5:
					mSensorLocation.setText("Ear Lobe");
					break;
				case 6:
					mSensorLocation.setText("Foot");
					break;
				}
			}
		}
	};

	private void updateHeartPulse(final int hrmMeasurementValue) {
		runOnUiThread(new Runnable() {
			public void run() {
				updateHeartrate(hrmMeasurementValue);
			}
		});

	}

	private synchronized void updateHeartrate(final int percentage) {
		try {
			Integer.parseInt((String) mHeartrate.getText());
		} catch (NumberFormatException nfe) {
		}
		if (percentage != mBPM) {
			mBPM=percentage;
			mHeartrate.setText(""+percentage);
			if(enableTTS) {
				tts.speak((String) mHeartrate.getText(), TextToSpeech.QUEUE_FLUSH, null);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SELECT_DEVICE) {
			if (resultCode == Activity.RESULT_OK && data != null && appRegistered == true) {
				mDeviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
				connectToDevice(mDeviceAddress);
				mDeviceName = data.getStringExtra(BluetoothDevice.EXTRA_NAME);
			} else {
				if (!appRegistered) {
					Toast toast = Toast.makeText(this, "app not registered yet - try again", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}
		}
	}

	private void connectToDevice(String deviceAddress) {
		mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
		if (mIsConnected) {
			mHeartRateMonitor.disconnect(mDevice);
		}
		mHeartRateMonitor.connect(mDevice); // or .connectBackground(mDevice);
	}

	private OnClickListener mButtonListener = new OnClickListener() {

		public void onClick(View arg0) {
			int result = 0;
			if (arg0.getId() == R.id.button2) {
				if (mIsConnected) {
					result = mHeartRateMonitor.writeHeartRateControlPointCharacteristic(
									mDevice,
									BluetoothHrmService.HEART_RATE_CONTROL_POINT_CHARACTERISTIC_UUID,
									1);
					Log.i(TAG,"writeHeartRateControlPointCharacteristic call returned "+result);
				} else {
					Toast.makeText(HtcBleDemo.this, "not connected yet", Toast.LENGTH_LONG).show();
				}
			} else if (arg0.getId() == R.id.button3) {
				if (mIsConnected) {
					result = mHeartRateMonitor.readBodySensorLocationCharacteristic(mDevice);
					Log.i(TAG,"readBodySensorLocationCharacteristic call returned "+result);
				} else {
					Toast.makeText(HtcBleDemo.this, "not connected yet", Toast.LENGTH_LONG).show();
				}
			}
			if (result == BleConstants.SERVICE_UNAVAILABLE) {
				Toast.makeText(HtcBleDemo.this, "service unavailable", Toast.LENGTH_LONG).show();
			}
		}
	};

	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		try {
			unregisterReceiver(regReceiver);
		} catch (Exception ignore) {
		}
		if (mHeartRateMonitor != null && mDevice != null) {
			mHeartRateMonitor.cancelBackgroundConnection(mDevice);
		}
		if (mHeartRateMonitor != null) {
			mHeartRateMonitor.deregisterProfile(); //for multiple connections: mHeartRateMonitor.deregister()
			mHeartRateMonitor.finish();
		}
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_htc_ble_demo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent newIntent = new Intent(HtcBleDemo.this, DeviceListActivity.class);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
			return (true);
		}
		return (super.onOptionsItemSelected(item));
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
		return true;
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			return textView;
		}
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			tts.setLanguage(Locale.US);
		}
	}
}
