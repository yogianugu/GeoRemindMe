package edu.uncc.georemindme;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import library.UserFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DashboardActivity extends Activity {

	private ProgressDialog progressDialog;
	private UserFunctions userFunctions;
	private Button btnLogout, btnSendReq, btnCheckReq, btnSetReminder,
			btnSetReminderForFriend, btnManageReminders;
	private String emailId;
	private JSONObject json;
	static ArrayList<String> emailList;
	static ArrayList<String> titleList;
	static ArrayList<Integer> idList;
	static ArrayList<JSONArray> reminderList;
	static ArrayList<AlertReminder> listAlerts;
	final Context context = this;
	int REQUEST_CODE;
	public static boolean SCREEN_FLAG;
	private static final String PROX_ALERT_INTENT = "edu.uncc.georemindme";
	private static final long PROX_ALERT_EXPIRATION = -1;
	LocationManager locationManager;
	static String addr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * Dashboard Screen for the application
		 * */
		// Check login status in database
		userFunctions = new UserFunctions();
		if (userFunctions.isUserLoggedIn(getApplicationContext())) {
			// user already logged in show databoard
			setContentView(R.layout.dashboard);
			emailId = userFunctions.getEmail(getApplicationContext());
			btnLogout = (Button) findViewById(R.id.btnLogout);

			btnLogout.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					userFunctions.logoutUser(getApplicationContext());
					Intent login = new Intent(getApplicationContext(),
							LoginActivity.class);
					login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(login);
					// Closing dashboard screen
					finish();
				}
			});

			btnManageReminders = (Button) findViewById(R.id.btnMngReminders);
			btnManageReminders.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						json = new AsyncTask<Void, Void, JSONObject>() {

							@Override
							protected JSONObject doInBackground(Void... params) {
								JSONObject jsonF = userFunctions
										.checkReminder(emailId);
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
										DashboardActivity.this, "",
										"Loading...");
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

					titleList = new ArrayList<String>();
					idList = new ArrayList<Integer>();
					try {
						if (json.getString("reminders").equals("no reminders")) {
							Toast.makeText(getApplicationContext(),
									"No Reminders Found", Toast.LENGTH_SHORT)
									.show();
						} else {

							try {
								JSONArray reminderArr = json
										.getJSONArray("reminders");
								for (int i = 0; i < reminderArr.length(); i++) {
									Log.d("len", reminderArr.getJSONObject(i)
											.getString("title") + "");
									titleList.add(reminderArr.getJSONObject(i)
											.getString("title"));
									idList.add(Integer.parseInt(reminderArr
											.getJSONObject(i).getString("id")));
								}
								// JSONObject objJson = titleList.get(0);
								Log.d("size", titleList.get(0) + "");

								Intent intent = new Intent(
										getApplicationContext(),
										ManageRemindersActivity.class);
								startActivity(intent);

							} catch (JSONException e) {
								e.printStackTrace();
							}
							Log.d("teqa", json + "");

						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			btnSendReq = (Button) findViewById(R.id.btnSendReq);
			btnSendReq.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					LayoutInflater layoutInflater = LayoutInflater
							.from(context);

					View promptView = layoutInflater.inflate(R.layout.prompts,
							null);
					final EditText input = (EditText) promptView
							.findViewById(R.id.userInput);

					new AlertDialog.Builder(DashboardActivity.this)
							.setCancelable(true)
							.setView(promptView)
							.setNeutralButton("Add Friend",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											final Editable value = input
													.getText();
											Log.d("req", value.toString());
											try {
												json = new AsyncTask<Void, Void, JSONObject>() {

													@Override
													protected JSONObject doInBackground(
															Void... params) {

														JSONObject jsonF = userFunctions
																.sendFriendRequest(
																		emailId,
																		value.toString());
														return jsonF;

													}

													@Override
													protected void onPostExecute(
															JSONObject result) {
														super.onPostExecute(result);
														progressDialog
																.dismiss();
													}

													@Override
													protected void onPreExecute() {
														super.onPreExecute();
														progressDialog = ProgressDialog
																.show(DashboardActivity.this,
																		"",
																		"Loading...");
														progressDialog
																.setProgressStyle(ProgressDialog.STYLE_SPINNER);
														progressDialog
																.setCancelable(false);
													}
												}.execute().get();
											} catch (InterruptedException e1) {
												e1.printStackTrace();
											} catch (ExecutionException e1) {
												e1.printStackTrace();
											}
											try {
												if (json.getInt("success") == 1)
													Toast.makeText(
															getApplicationContext(),
															json.getString("success_msg"),
															Toast.LENGTH_SHORT)
															.show();
												else {
													Toast.makeText(
															getApplicationContext(),
															json.getString("error_msg"),
															Toast.LENGTH_SHORT)
															.show();
												}
											} catch (JSONException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
											Log.d("req", json + "");
										}
									}).show();
					// userFunctions.getEmail(getApplicationContext());
					// JSONObject json = userFunctions.sendFriendRequest(Email);

				}
			});

			btnSetReminder = (Button) findViewById(R.id.btnSetRmndrs);
			btnSetReminder.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(DashboardActivity.this,
							ReminderActivity.class);
					SCREEN_FLAG = false;
					startActivity(i);
					// Log.d("hmm", ReminderActivity.hello);

				}
			});

			btnSetReminderForFriend = (Button) findViewById(R.id.btnStRmndFrnd);
			btnSetReminderForFriend
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent i = new Intent(DashboardActivity.this,
									ReminderActivity.class);
							SCREEN_FLAG = true;
							startActivity(i);
						}
					});

			btnCheckReq = (Button) findViewById(R.id.btnCheckReq);
			btnCheckReq.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					try {
						json = new AsyncTask<Void, Void, JSONObject>() {

							@Override
							protected JSONObject doInBackground(Void... params) {
								JSONObject jsonF = userFunctions
										.checkFriendRequest(emailId);
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
										DashboardActivity.this, "",
										"Loading...");
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

					emailList = new ArrayList<String>();
					try {
						if (json.getString("emails").equals("no friends")) {
							Toast.makeText(getApplicationContext(),
									"No Requests Found", Toast.LENGTH_SHORT)
									.show();
						} else {

							try {
								JSONArray emailArr = json
										.getJSONArray("emails");
								for (int i = 0; i < emailArr.length(); i++) {
									Log.d("len", emailArr.getString(i) + "");
									emailList.add(emailArr.getString(i));
								}
								Log.d("size", emailList.get(0) + "");

								Intent intent = new Intent(
										getApplicationContext(),
										RequestsActivity.class);
								startActivity(intent);

							} catch (JSONException e) {
								e.printStackTrace();
							}
							Log.d("teqa", json + "");

						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

		} else {
			// user is not logged in show login screen
			Intent login = new Intent(getApplicationContext(),
					LoginActivity.class);
			login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(login);
			// Closing dashboard screen
			finish();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.set_alerts) {
			// TODO
			try {
				json = new AsyncTask<Void, Void, JSONObject>() {

					@Override
					protected JSONObject doInBackground(Void... params) {
						JSONObject jsonF = userFunctions.checkReminder(emailId);
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
								DashboardActivity.this, "", "Loading...");
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

			titleList = new ArrayList<String>();
			idList = new ArrayList<Integer>();
			try {
				if (json.getString("reminders").equals("no reminders")) {
					Toast.makeText(getApplicationContext(),
							"No Reminders Found", Toast.LENGTH_SHORT).show();
				} else {

					try {
						listAlerts = new ArrayList<AlertReminder>();
						JSONArray reminderArr = json.getJSONArray("reminders");

						for (int i = 0; i < reminderArr.length(); i++) {
							Log.d("len", reminderArr.getJSONObject(i)
									.getString("title") + "");
							AlertReminder alertReminder = new AlertReminder();
							alertReminder.setDescription(reminderArr
									.getJSONObject(i).getString("title"));
							alertReminder.setName(reminderArr.getJSONObject(i)
									.getString("setByUser"));
							alertReminder.setLatitude(reminderArr
									.getJSONObject(i).getDouble("latitude"));
							alertReminder.setLongitude(reminderArr
									.getJSONObject(i).getDouble("longitude"));
							alertReminder.setRadius(reminderArr
									.getJSONObject(i).getInt("radius"));
							alertReminder.setLocation(reminderArr
									.getJSONObject(i).getString("address"));
							alertReminder.setId(reminderArr
									.getJSONObject(i).getInt("id"));
							listAlerts.add(alertReminder);
						}
						// JSONObject objJson = titleList.get(0);
						//Log.d("size", titleList.get(0) + "");
						locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
						Intent intent = new Intent(PROX_ALERT_INTENT);
					
						
						for (int i = 0; i < listAlerts.size(); i++) {
							intent.putExtra("addr", listAlerts.get(i).getLocation());
							intent.putExtra("name", listAlerts.get(i).getName());
							intent.putExtra("desc", listAlerts.get(i).getDescription());
							
							locationManager.addProximityAlert(listAlerts.get(i).getLatitude(), listAlerts.get(i).getLongitude(),
									listAlerts.get(i).getRadius(), 
									PROX_ALERT_EXPIRATION, 
									PendingIntent
									.getBroadcast(this, listAlerts.get(i).getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT));
							
							addr = listAlerts.get(i).getLocation();
							IntentFilter filter = new IntentFilter(
									PROX_ALERT_INTENT);
							registerReceiver(new ProximityIntentReceiver(),
									filter);
							Toast.makeText(getApplicationContext(),
									"Alert Added", Toast.LENGTH_SHORT).show();

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					// Log.d("teqa", json + "");

				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return true;
	}

}