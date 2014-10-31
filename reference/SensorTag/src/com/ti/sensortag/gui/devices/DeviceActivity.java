package com.ti.sensortag.gui.devices;

import static com.ti.sensortag.models.Devices.LOST_DEVICE_;
import static com.ti.sensortag.models.Devices.NEW_DEVICE_;
import static com.ti.sensortag.models.Devices.State.CONNECTED;
import static com.ti.sensortag.models.Measurements.PROPERTY_ACCELEROMETER;
import static com.ti.sensortag.models.Measurements.PROPERTY_AMBIENT_TEMPERATURE;
import static com.ti.sensortag.models.Measurements.PROPERTY_GYROSCOPE;
import static com.ti.sensortag.models.Measurements.PROPERTY_HUMIDITY;
import static com.ti.sensortag.models.Measurements.PROPERTY_IR_TEMPERATURE;
import static com.ti.sensortag.models.Measurements.PROPERTY_MAGNETOMETER;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataAdapterInterface;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.ti.sensortag.R;
import com.ti.sensortag.gui.services.ServicesCardsActivity;
import com.ti.sensortag.models.Devices;
import com.ti.sensortag.models.Measurements;
import com.ti.sensortag.models.Point3D;

public class DeviceActivity extends Activity implements PropertyChangeListener {

	private static final Measurements model = Measurements.INSTANCE;

	TextView sensorValue;

	public static String GRAPH_TYPE = "graph_type";
	public static String GRAPH_TYPE_BAR = "graph_type_bar";
	public static String GRAPH_TYPE_LINE = "graph_type_line";

	private GraphView graphView;
	private GraphViewSeries exampleSeries1;
	private GraphViewSeries exampleSeries2;
	private double graph2LastXValue = 5d;
	private GraphViewSeries exampleSeries3;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sensordetails);

		sensorValue = (TextView) findViewById(R.id.sensorValue);

		if (getIntent().getStringExtra(Measurements.MEASURE_PROPERTY).equals(
				PROPERTY_ACCELEROMETER)) {

			GraphViewSeries measurementSeriesY = new GraphViewSeries(
					new GraphViewDataAdapterInterface() {

						@Override
						public double getX(int index) {

							return index;
						}

						@Override
						public double getY(int index) {

							return Measurements.INSTANCE
									.getAccelerometerElements().get(index).y;
						}

						@Override
						public int size() {

							return Measurements.INSTANCE
									.getAccelerometerElements().size();
						}

					});
			measurementSeriesY.getStyle().color = Color.CYAN;

			GraphViewSeries measurementSeriesX = new GraphViewSeries(
					new GraphViewDataAdapterInterface() {

						@Override
						public double getX(int index) {

							return index;
						}

						@Override
						public double getY(int index) {

							return Measurements.INSTANCE
									.getAccelerometerElements().get(index).x;
						}

						@Override
						public int size() {

							return Measurements.INSTANCE
									.getAccelerometerElements().size();
						}

					});
			measurementSeriesX.getStyle().color = Color.RED;

			GraphViewSeries measurementSeriesZ = new GraphViewSeries(
					new GraphViewDataAdapterInterface() {

						@Override
						public double getX(int index) {

							return index;
						}

						@Override
						public double getY(int index) {

							return Measurements.INSTANCE
									.getAccelerometerElements().get(index).z;
						}

						@Override
						public int size() {

							return Measurements.INSTANCE
									.getAccelerometerElements().size();
						}

					});
			measurementSeriesZ.getStyle().color = Color.YELLOW;

			graphView.addSeries(measurementSeriesX); // data
			graphView.addSeries(measurementSeriesY);
			graphView.addSeries(measurementSeriesZ);

		} else {

			final GraphViewData[] sample1 = new GraphViewData[] {
					new GraphViewData(1, 2.0d),
					new GraphViewData(2, 1.5d),
					new GraphViewData(2.5, 3.0d) // another frequency
					, new GraphViewData(3, 2.5d), new GraphViewData(4, 1.0d),
					new GraphViewData(5, 3.0d) };

			// init example series data
			exampleSeries1 = new GraphViewSeries(
					new GraphViewDataAdapterInterface() {

						@Override
						public int size() {

							return sample1.length;
						}

						@Override
						public double getY(int index) {

							return sample1[index].valueY;
						}

						@Override
						public double getX(int index) {

							return sample1[index].valueX;
						}
					});
			// exampleSeries3 = new GraphViewSeries(new GraphViewData[] {});
			exampleSeries1.getStyle().color = Color.CYAN;
		
			graphView.addSeries(exampleSeries1); // data
		}
		// graph with dynamically generated horizontal and vertical labels
		if (getIntent().getStringExtra(GRAPH_TYPE).equals(GRAPH_TYPE_BAR)) {
			graphView = new BarGraphView(this // context
					, "Measurements" // heading
			);
		} else {
			graphView = new LineGraphView(this // context
					, "Measurements" // heading
			);
		}
		
		

//		graphView.setViewPort(1, 8);
//		graphView.setScalable(true);

		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.addView(graphView);

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

						String msg = "X: "
								+ ServicesCardsActivity.decimal
										.format(newValue.x)
								+ "g"
								+ "\nY: "
								+ ServicesCardsActivity.decimal
										.format(newValue.y)
								+ "g"
								+ "\nZ: "
								+ ServicesCardsActivity.decimal
										.format(newValue.z) + "g";
						sensorValue.setText(msg);

					} else if (property.equals(PROPERTY_AMBIENT_TEMPERATURE)) {
						double newAmbientValue = (Double) event.getNewValue();
						// TextView textView = (TextView)
						// findViewById(R.id.ambientTemperatureTxt);
						String formattedText = ServicesCardsActivity.decimal
								.format(newAmbientValue)
								+ ServicesCardsActivity.DEGREE_SYM;
						// textView.setText(formattedText);
						sensorValue.setText(formattedText);
					} else if (property.equals(PROPERTY_IR_TEMPERATURE)) {
						double newIRValue = (Double) event.getNewValue();
						// TextView textView = (TextView)
						// findViewById(R.id.ir_temperature);
						String formattedText = ServicesCardsActivity.decimal
								.format(newIRValue)
								+ ServicesCardsActivity.DEGREE_SYM;
						// textView.setText(formattedText);
						sensorValue.setText(formattedText);
					} else if (property.equals(PROPERTY_HUMIDITY)) {
						double newHumidity = (Double) event.getNewValue();
						// TextView textView = (TextView)
						// findViewById(R.id.humidityTxt);
						String formattedText = ServicesCardsActivity.decimal
								.format(newHumidity) + "%rH";
						// textView.setText(formattedText);
						sensorValue.setText(formattedText);

					} else if (property.equals(PROPERTY_MAGNETOMETER)) {
						Point3D newValue = (Point3D) event.getNewValue();

						String msg = "X: "
								+ ServicesCardsActivity.decimal
										.format(newValue.x)
								+ "uT"
								+ "\nY: "
								+ ServicesCardsActivity.decimal
										.format(newValue.y)
								+ "uT"
								+ "\nZ: "
								+ ServicesCardsActivity.decimal
										.format(newValue.z) + "uT";

						sensorValue.setText(msg);
					} else if (property.equals(PROPERTY_GYROSCOPE)) {
						Point3D newValue = (Point3D) event.getNewValue();

						String msg = "X: "
								+ ServicesCardsActivity.decimal
										.format(newValue.x)
								+ "deg/s"
								+ "\nY: "
								+ ServicesCardsActivity.decimal
										.format(newValue.y)
								+ "deg/s"
								+ "\nZ: "
								+ ServicesCardsActivity.decimal
										.format(newValue.z) + "deg/s";

						sensorValue.setText(msg);
					} else if (property.equals(Measurements.PROPERTY_BAROMETER)) {
						Double newValue = (Double) event.getNewValue();

						String msg = new DecimalFormat("+0.0;-0.0")
								.format(newValue / 100) + " hPa";

						sensorValue.setText(msg);
					} else if (property.equals(LOST_DEVICE_ + CONNECTED)) {
						// A device has been disconnected
						// We notify the user with a toast

						int duration = Toast.LENGTH_SHORT;
						String text = "Lost connection";

						Toast.makeText(DeviceActivity.this, text, duration)
								.show();
						finish();
					} else if (property.equals(NEW_DEVICE_ + CONNECTED)) {
						// A device has been disconnected
						// We notify the user with a toast

						int duration = Toast.LENGTH_SHORT;
						String text = "Established connection";

						Toast.makeText(DeviceActivity.this, text, duration)
								.show();
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
