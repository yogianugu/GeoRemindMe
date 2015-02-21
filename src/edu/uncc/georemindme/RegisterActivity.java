package edu.uncc.georemindme;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import library.DatabaseHandler;
import library.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity {
	
	private Button btnRegister;
	private Button btnLinkToLogin;
	private EditText inputFullName;
	private EditText inputEmail;
	private EditText inputPassword;
	private TextView registerErrorMsg;
	private JSONObject json;
	private ProgressDialog progressDialog;

	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		// Importing all assets like buttons, text fields
		inputFullName = (EditText) findViewById(R.id.registerName);
		inputEmail = (EditText) findViewById(R.id.registerEmail);
		inputPassword = (EditText) findViewById(R.id.registerPassword);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
		registerErrorMsg = (TextView) findViewById(R.id.register_error);

		// Register Button Click event
		btnRegister.setOnClickListener(new View.OnClickListener() {
			UserFunctions userFunction = new UserFunctions();

			@SuppressWarnings("unchecked")
			public void onClick(View view) {
				String name = inputFullName.getText().toString();
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();
				ArrayList<String> stringList = new ArrayList<String>();
				stringList.add(name);
				stringList.add(email);
				stringList.add(password);
				if (!(name.equals("") || email.equals("") || password
						.equals(""))) {
					// Log.d("check", name + ":" + password + ":" + password);

					try {
						json = new AsyncTask<ArrayList<String>, Void, JSONObject>() {

							@Override
							protected JSONObject doInBackground(
									ArrayList<String>... params) {

								JSONObject jsonF = userFunction.registerUser(
										params[0].get(0), params[0].get(1),
										params[0].get(2));
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
								progressDialog = ProgressDialog.show(RegisterActivity.this, "",
										"Loading...");
								progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								progressDialog.setCancelable(false);
							}
						}.execute(stringList).get();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// check for login response
					try {
						if (json.getString(KEY_SUCCESS) != null) {
							if (json.getInt(KEY_SUCCESS) == 1) {

								Log.d("success string",
										json.getString(KEY_SUCCESS));
								registerErrorMsg.setText("");
								String res = json.getString(KEY_SUCCESS);
								if (Integer.parseInt(res) == 1) {
									// user successfully registered
									// Store user details in SQLite Database
									DatabaseHandler db = new DatabaseHandler(
											getApplicationContext());
									JSONObject json_user = json
											.getJSONObject("user");

									// Clear all previous data in database
									userFunction
											.logoutUser(getApplicationContext());
									db.addUser(json_user.getString(KEY_NAME),
											json_user.getString(KEY_EMAIL),
											json.getString(KEY_UID),
											json_user.getString(KEY_CREATED_AT));
									// Launch Dashboard Screen
									Intent dashboard = new Intent(
											getApplicationContext(),
											DashboardActivity.class);
									// Close all views before launching
									// Dashboard
									dashboard
											.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(dashboard);
									// Close Registration Screen
									finish();
								}
							} else {
								// Error in registration
								Log.d("inside fail", "i am inside");
								registerErrorMsg.setText(json
										.getString(KEY_ERROR_MSG));
							}
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else if (name.equals("")) {
					registerErrorMsg.setText("Full Name Can't be Empty");
				} else if (email.equals("")) {
					registerErrorMsg.setText("Email Can't be Empty");

				} else if (password.equals("")) {
					registerErrorMsg.setText("Password Can't be Empty");
				} else {
					// Error in registration
					registerErrorMsg.setText("Unknown Error");
				}
			}
		});

		// Link to Login Screen
		btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(i);
				// Close Registration View
				finish();
			}
		});
	}
}
