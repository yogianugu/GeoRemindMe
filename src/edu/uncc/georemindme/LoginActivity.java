package edu.uncc.georemindme;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	private ProgressDialog progressDialog;
	private Button btnLogin;
	private Button btnLinkToRegister;
	private EditText inputEmail;
	private EditText inputPassword;
	private TextView loginErrorMsg;
	private JSONObject json;

	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// Importing all assets like buttons, text fields
		inputEmail = (EditText) findViewById(R.id.loginEmail);
		inputPassword = (EditText) findViewById(R.id.loginPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);

		// Login button Click Event
		btnLogin.setOnClickListener(new View.OnClickListener() {
			UserFunctions userFunction = new UserFunctions();

			public void onClick(View view) {

				try {
					json = new AsyncTask<Void, Void, JSONObject>() {

						@Override
						protected JSONObject doInBackground(Void... params) {
							String email = inputEmail.getText().toString();
							String password = inputPassword.getText()
									.toString();

							JSONObject jsonF = userFunction.loginUser(email,
									password);
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
									LoginActivity.this, "", "Loading...");
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

				// check for login response
				try {
					if (json.getString(KEY_SUCCESS) != null) {
						loginErrorMsg.setText("");
						String res = json.getString(KEY_SUCCESS);
						if (Integer.parseInt(res) == 1) {
							// user successfully logged in
							// Store user details in SQLite Database
							DatabaseHandler db = new DatabaseHandler(
									getApplicationContext());
							JSONObject json_user = json.getJSONObject("user");

							// Clear all previous data in database

							userFunction.logoutUser(getApplicationContext());

							db.addUser(json_user.getString(KEY_NAME),
									json_user.getString(KEY_EMAIL),
									json.getString(KEY_UID),
									json_user.getString(KEY_CREATED_AT));

							// Launch Dashboard Screen
							Intent dashboard = new Intent(
									getApplicationContext(),
									DashboardActivity.class);

							// Close all views before launching Dashboard
							dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(dashboard);

							// Close Login Screen
							finish();
						} else {
							// Error in login
							loginErrorMsg
									.setText("Incorrect username/password");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		// Link to Register Screen
		btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						RegisterActivity.class);
				startActivity(i);
				finish();
			}
		});
	}
}