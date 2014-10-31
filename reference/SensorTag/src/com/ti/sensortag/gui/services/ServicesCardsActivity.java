package com.ti.sensortag.gui.services;

import static com.ti.sensortag.R.drawable.buttonsoffoff;
import static com.ti.sensortag.R.drawable.buttonsoffon;
import static com.ti.sensortag.R.drawable.buttonsonoff;
import static com.ti.sensortag.R.drawable.buttonsonon;
import static com.ti.sensortag.models.Devices.LOST_DEVICE_;
import static com.ti.sensortag.models.Devices.NEW_DEVICE_;
import static com.ti.sensortag.models.Devices.State.CONNECTED;
import static com.ti.sensortag.models.Measurements.PROPERTY_ACCELEROMETER;
import static com.ti.sensortag.models.Measurements.PROPERTY_AMBIENT_TEMPERATURE;
import static com.ti.sensortag.models.Measurements.PROPERTY_BAROMETER;
import static com.ti.sensortag.models.Measurements.PROPERTY_GYROSCOPE;
import static com.ti.sensortag.models.Measurements.PROPERTY_HUMIDITY;
import static com.ti.sensortag.models.Measurements.PROPERTY_IR_TEMPERATURE;
import static com.ti.sensortag.models.Measurements.PROPERTY_MAGNETOMETER;
import static com.ti.sensortag.models.Measurements.PROPERTY_SIMPLE_KEYS;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.fima.cardsui.objects.Card;
import com.fima.cardsui.views.CardUI;
import com.ti.sensortag.R;
import com.ti.sensortag.gui.devices.DeviceActivity;
import com.ti.sensortag.gui.devices.DeviceCard;
import com.ti.sensortag.gui.devices.DeviceImageCard;
import com.ti.sensortag.models.Devices;
import com.ti.sensortag.models.Measurements;
import com.ti.sensortag.models.Point3D;
import com.ti.sensortag.models.SimpleKeysStatus;

public class ServicesCardsActivity extends Activity implements
		PropertyChangeListener {

	private static final Measurements model = Measurements.INSTANCE;
	
	public static final char DEGREE_SYM = '\u2103';
	public static DecimalFormat decimal = new DecimalFormat("+0.00;-0.00");

	private CardUI mCardView;
	private HashMap<String, Card> cards;

	volatile boolean b = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.services_browser_cards);

		cards = new HashMap<String, Card>();

		// init CardView
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(false);

		Card card;

		card = new DeviceImageCard("Simple Keys", R.drawable.buttonsoffoff);
		cards.put(PROPERTY_SIMPLE_KEYS, card);
		mCardView.addCard(card);
		card.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("http://www.androidviews.net/"));
				startActivity(intent);

			}
		});

		
		card = new DeviceCard("Accelerometer", R.drawable.accelerometer);
		cards.put(PROPERTY_ACCELEROMETER, card);
		mCardView.addCard(card);
		card.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ServicesCardsActivity.this, DeviceActivity.class);
			    intent.putExtra(DeviceActivity.GRAPH_TYPE, DeviceActivity.GRAPH_TYPE_LINE);
			    intent.putExtra(Measurements.MEASURE_PROPERTY, PROPERTY_ACCELEROMETER);
			    startActivity(intent);

			}
		});

		card = new DeviceCard("Magnetometer", R.drawable.sensortag_magnetometer);
		cards.put(PROPERTY_MAGNETOMETER, card);
		mCardView.addCard(card);

		card = new DeviceCard("Gyroscope", R.drawable.gyroscope);
		cards.put(PROPERTY_GYROSCOPE, card);
		mCardView.addCard(card);

		card = new DeviceCard("Object temperature", R.drawable.irtemperature);
		cards.put(PROPERTY_IR_TEMPERATURE, card);
		mCardView.addCard(card);

		card = new DeviceCard("Ambient temperature", R.drawable.temperature);
		cards.put(PROPERTY_AMBIENT_TEMPERATURE, card);
		mCardView.addCard(card);

		card = new DeviceCard("Humidity", R.drawable.humidity);
		cards.put(PROPERTY_HUMIDITY, card);
		mCardView.addCard(card);

		card = new DeviceCard("Barometer", R.drawable.barometer);
		cards.put(PROPERTY_BAROMETER, card);
		mCardView.addCard(card);

		mCardView.refresh();

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onResume() {
		super.onResume();

		// Setup this view to listen to the model
		// in the traditional MVC pattern.
		model.addPropertyChangeListener(this);

		// Also listen to changes in connection state so
		// we can notify the user with toasts that
		// the device has been disconnected.
		Devices.INSTANCE.addPropertyChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		// Stop listening to changes.
		model.removePropertyChangeListener(this);
		Devices.INSTANCE.removePropertyChangeListener(this);
	}

	/**
	 * This class listens to changes in the model of sensor values.
	 * */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		final String property = event.getPropertyName();

		runOnUiThread(new Runnable() {
			public void run() {
				try {
					if (property.equals(PROPERTY_ACCELEROMETER)) {
						// A change in accelerometer data has occured.
						Point3D newValue = (Point3D) event.getNewValue();

						String msg = "X: " + decimal.format(newValue.x) + "g"
								+ " Y: " + decimal.format(newValue.y) + "g"
								+ " Z: " + decimal.format(newValue.z) + "g";

						// ((TextView) findViewById(R.id.accelerometerTxt))
						// .setText(msg);

						((DeviceCard) cards.get(PROPERTY_ACCELEROMETER))
								.setText(msg);

					} else if (property.equals(PROPERTY_AMBIENT_TEMPERATURE)) {
						double newAmbientValue = (Double) event.getNewValue();
						// TextView textView = (TextView)
						// findViewById(R.id.ambientTemperatureTxt);
						String formattedText = decimal.format(newAmbientValue)
								+ DEGREE_SYM;
						// textView.setText(formattedText);
						((DeviceCard) cards.get(PROPERTY_AMBIENT_TEMPERATURE))
								.setText(formattedText);
					} else if (property.equals(PROPERTY_IR_TEMPERATURE)) {
						double newIRValue = (Double) event.getNewValue();
						// TextView textView = (TextView)
						// findViewById(R.id.ir_temperature);
						String formattedText = decimal.format(newIRValue)
								+ DEGREE_SYM;
						// textView.setText(formattedText);
						((DeviceCard) cards.get(PROPERTY_IR_TEMPERATURE))
								.setText(formattedText);
					} else if (property.equals(PROPERTY_HUMIDITY)) {
						double newHumidity = (Double) event.getNewValue();
						// TextView textView = (TextView)
						// findViewById(R.id.humidityTxt);
						String formattedText = decimal.format(newHumidity)
								+ "%rH";
						// textView.setText(formattedText);
						((DeviceCard) cards.get(PROPERTY_HUMIDITY))
								.setText(formattedText);

					} else if (property.equals(PROPERTY_MAGNETOMETER)) {
						Point3D newValue = (Point3D) event.getNewValue();

						String msg = "X: " + decimal.format(newValue.x) + "uT"
								+ " Y: " + decimal.format(newValue.y) + "uT"
								+ " Z: " + decimal.format(newValue.z) + "uT";

						// ((TextView) findViewById(R.id.magnetometerTxt))
						// .setText(msg);
						((DeviceCard) cards.get(PROPERTY_MAGNETOMETER))
								.setText(msg);
					} else if (property.equals(PROPERTY_GYROSCOPE)) {
						Point3D newValue = (Point3D) event.getNewValue();

						String msg = "X: " + decimal.format(newValue.x)
								+ "deg/s" + " Y: "
								+ decimal.format(newValue.y) + "deg/s"
								+ " Z: " + decimal.format(newValue.z)
								+ "deg/s";

						// ((TextView) findViewById(R.id.gyroscopeTxt))
						// .setText(msg);
						((DeviceCard) cards.get(PROPERTY_GYROSCOPE))
								.setText(msg);
					} else if (property.equals(Measurements.PROPERTY_BAROMETER)) {
						Double newValue = (Double) event.getNewValue();

						String msg = new DecimalFormat("+0.0;-0.0")
								.format(newValue / 100) + " hPa";

						// ((TextView) findViewById(R.id.barometerTxt))
						// .setText(msg);
						((DeviceCard) cards.get(PROPERTY_BAROMETER))
								.setText(msg);
					} else if (property.equals(PROPERTY_SIMPLE_KEYS)) {
						SimpleKeysStatus newValue = (SimpleKeysStatus) event
								.getNewValue();

						final int img;
						switch (newValue) {
						case OFF_OFF:
							img = buttonsoffoff;
							break;
						case OFF_ON:
							img = buttonsoffon;
							break;
						case ON_OFF:
							img = buttonsonoff;
							break;
						case ON_ON:
							img = buttonsonon;
							break;
						default:
							throw new UnsupportedOperationException();
						}

						// ((ImageView) findViewById(R.id.buttons))
						// .setImageResource(img);

						((DeviceImageCard) (cards.get(PROPERTY_SIMPLE_KEYS)))
								.setImage(img);
					} else if (property.equals(LOST_DEVICE_ + CONNECTED)) {
						// A device has been disconnected
						// We notify the user with a toast

						int duration = Toast.LENGTH_SHORT;
						String text = "Lost connection";

						Toast.makeText(ServicesCardsActivity.this, text,
								duration).show();
						finish();
					} else if (property.equals(NEW_DEVICE_ + CONNECTED)) {
						// A device has been disconnected
						// We notify the user with a toast

						int duration = Toast.LENGTH_SHORT;
						String text = "Established connection";

						Toast.makeText(ServicesCardsActivity.this, text,
								duration).show();
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
					// Could be that the ServicesFragment is no longer visible
					// But we still receive property change events.
					// referring to the views with findViewById will then return
					// a null.
				}
			}
		});
	}
}
