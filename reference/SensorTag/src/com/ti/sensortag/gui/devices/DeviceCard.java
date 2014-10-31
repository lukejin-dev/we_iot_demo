package com.ti.sensortag.gui.devices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;
import com.ti.sensortag.R;

public class DeviceCard extends Card {

	int drawableId;

	TextView sensorInfo;

	public DeviceCard(String title, int drawableId) {
		super(title);
		this.drawableId = drawableId;
	}

	public void setText(String text) {
		sensorInfo.setText(text);
	}

	@Override
	public View getCardContent(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.sensor_card,
				null);

		((TextView) view.findViewById(R.id.title)).setText(title);

		sensorInfo = ((TextView) view.findViewById(R.id.sensor));

		sensorInfo.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);

		return view;
	}

}
