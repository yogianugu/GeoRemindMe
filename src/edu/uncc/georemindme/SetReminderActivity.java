package edu.uncc.georemindme;

import java.util.concurrent.ExecutionException;

import library.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SetReminderActivity extends Activity {

	private ProgressDialog progressDialog;
	EditText locationEText, radiusEText, descriptionEText, frndEmailAdd;
	private JSONObject json;
	private double latitude, longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_reminder);

		frndEmailAdd = (EditText) findViewById(R.id.reg_frnd_email);
		if (DashboardActivity.SCREEN_FLAG) {
			((TextView) findViewById(R.id.reg_frnd_email_lbl))
					.setVisibility(View.VISIBLE);
			frndEmailAdd.setVisibility(View.VISIBLE);
		}
		String msg = getIntent().getExtras().getString(
				ReminderActivity.ADDRESS_KEY);
		latitude = getIntent().getExtras().getDouble(
				ReminderActivity.LATITUDE_KEY);
		longitude = getIntent().getExtras().getDouble(
				ReminderActivity.LONGITUDE_KEY);
		locationEText = (EditText) findViewById(R.id.reg_location);
		locationEText.setText(msg);
		locationEText.setFocusable(false);
		locationEText.setClickable(false);

		radiusEText = (EditText) findViewById(R.id.reg_radius);

		descriptionEText = (EditText) findViewById(R.id.reg_description);

		Button btn = (Button) findViewById(R.id.btnSetReminder);
		btn.setOnClickListener(new View.OnClickListener() {
			UserFunctions userFunctions = new UserFunctions();

			@Override
			public void onClick(View v) {
			
				try {
					json = new AsyncTask<Void, Void, JSONObject>() {

						@Override
						protected JSONObject doInBackground(Void... params) {
							String toEmailAddress;
							if (DashboardActivity.SCREEN_FLAG) {
								toEmailAddress = frndEmailAdd.getText()
										.toString();
							} else {
								toEmailAddress = userFunctions
										.getEmail(SetReminderActivity.this);
							}
							JSONObject jsonF = userFunctions.setReminder(
									userFunctions
											.getEmail(SetReminderActivity.this),
									toEmailAddress, descriptionEText.getText()
											.toString(), latitude + "",
									longitude + "", locationEText.getText()
											.toString(), radiusEText.getText()
											.toString());
							return jsonF;

						}

						@Override
						protected void onPostExecute(JSONObject result) {
							super.onPostExecute(result);
							progressDialog.dismiss();
						}

						@Override
						protected void onPreExecute() {
							super.onPreExecute();
							progressDialog = ProgressDialog.show(
									SetReminderActivity.this, "", "Loading...");
							progressDialog
									.setProgressStyle(ProgressDialog.STYLE_SPINNER);
							progressDialog.setCancelable(false);
						}
					}.execute().get();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}
				try {
					
					if (Integer.parseInt(json.getString("success")) == 1) {
						Log.d("success", json + "");

						Toast.makeText(SetReminderActivity.this,
								"Reminder successfully added",
								Toast.LENGTH_LONG).show();
						finish();
					}
					else {
						Toast.makeText(SetReminderActivity.this, json.getString("error_msg"), Toast.LENGTH_LONG).show();
						finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		/*
		 * userFunctions.getReminder(userFunctions.getEmail(SetReminderActivity.this
		 * ), descriptionEText.getText().toString(), latitude+"", longitude+"",
		 * locationEText.getText().toString(),
		 * radiusEText.getText().toString());
		 */

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_reminder, menu);
		return true;
	}

}
