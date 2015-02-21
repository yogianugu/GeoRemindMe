package edu.uncc.georemindme;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class ReminderActivity extends Activity {
	GoogleMap gmap;
	double lat, lon;
	String msg;
	Context mContext;

	public static final String LATITUDE_KEY = "latitude";
	public static final String LONGITUDE_KEY = "longitude";
	public static final String ADDRESS_KEY = "address";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reminder);
		
		mContext = this;
		gmap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		gmap.setMyLocationEnabled(true);
		gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				35.2269444, -80.8433333), 13));
		gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

			@Override
			public void onMapClick(LatLng position) {
				lat = position.latitude;
				lon = position.longitude;
				msg = getAddress(lat, lon);
				if (msg.equals(null))
					msg = "Can't fetch location";
				// createNotification(Calendar.getInstance().getTimeInMillis(),
				// msg);
				Marker nm = gmap.addMarker(new MarkerOptions().position(
						position).title(msg));
				gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
				nm.showInfoWindow();

			}
		});
		gmap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker arg0) {
				Log.d("inside", "on info window click" + msg);
				Intent i = new Intent(ReminderActivity.this,
						SetReminderActivity.class);
				i.putExtra(LATITUDE_KEY, lat);
				i.putExtra(LONGITUDE_KEY, lon);
				i.putExtra(ADDRESS_KEY, msg);
				startActivity(i);
				finish();

			}
		});

	}

	public String getAddress(double latitude, double longitude) {
		Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
		List<Address> addresses = null;
		String result = null;
		try {
			// Call the synchronous getFromLocation() method by passing in the
			// lat/long values.
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (addresses != null && addresses.size() > 0) {
			String address = addresses.get(0).getAddressLine(0);
			String city = addresses.get(0).getAddressLine(1);
			// String country = addresses.get(0).getAddressLine(2);
			// result=address+","+city+","+country;
			result = address + "," + city;
		}
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reminder, menu);
		return true;
	}

}
