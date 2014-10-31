package com.ti.sensortag.gui.devices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;
import com.ti.sensortag.R;

public class DeviceImageCard extends Card {

	int drawableId;

	ImageView sensorInfo;

	public DeviceImageCard(String title, int drawableId) {
		super(title);
		this.drawableId = drawableId;
	}

	public void setImage(int resId) {
		sensorInfo.setImageResource(resId);
	}

	@Override
	public View getCardContent(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.sensor_card,
				null);

		((TextView) view.findViewById(R.id.title)).setText(title);

		sensorInfo = ((ImageView) view.findViewById(R.id.sensor));

		setImage(drawableId);
		
		return view;
	}

}
