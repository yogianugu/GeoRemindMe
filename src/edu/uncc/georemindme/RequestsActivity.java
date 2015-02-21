package edu.uncc.georemindme;

import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import library.UserFunctions;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class RequestsActivity extends Activity {

	private ProgressDialog progressDialog;
	int pos;
	UserFunctions userFunctions;
	String emailId;
	private JSONObject json;

	private final String ACCEPTED = "1";
	private final String REJECTED = "2";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_requests);

		userFunctions = new UserFunctions();
		if (userFunctions.isUserLoggedIn(getApplicationContext())) {

			emailId = userFunctions.getEmail(getApplicationContext());

			ListView lv = (ListView) findViewById(R.id.ListView1);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, android.R.id.text1,
					DashboardActivity.emailList);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					pos = position;

					AlertDialog.Builder builder = new AlertDialog.Builder(
							RequestsActivity.this);
					builder.setMessage("Take Action")
							.setPositiveButton("Accept",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Log.d("inner",
													"you clicked accept"
															+ DashboardActivity.emailList
																	.get(pos)
															+ emailId);
											try {

												JSonAsync jSonAsync = new JSonAsync(
														ACCEPTED);
												json = jSonAsync.execute()
														.get();
											} catch (InterruptedException e1) {
												e1.printStackTrace();
											} catch (ExecutionException e1) {
												e1.printStackTrace();
											}
											Log.d("req", json + "");
											Toast.makeText(
													getApplicationContext(),
													"Friend Request Accepted",
													Toast.LENGTH_SHORT).show();
											finish();
										}
									})
							.setNegativeButton("Reject",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											Log.d("inner",
													"you clicked reject"
															+ DashboardActivity.emailList
																	.get(pos)
															+ emailId);
											try {
												JSonAsync jSonAsync = new JSonAsync(
														REJECTED);
												json = jSonAsync.execute()
														.get();
											} catch (InterruptedException e1) {
												e1.printStackTrace();
											} catch (ExecutionException e1) {
												e1.printStackTrace();
											}
											Log.d("req", json + "");
											Toast.makeText(
													getApplicationContext(),
													"Friend Request Rejected",
													Toast.LENGTH_SHORT).show();
											
											finish();

										}
									}).show();
					
					// builder.create();
					Log.d("pos", DashboardActivity.emailList.get(position));

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

	class JSonAsync extends AsyncTask<Void, Void, JSONObject> {

		private String status;

		public JSonAsync(String status) {
			this.status = status;
		}

		@Override
		protected JSONObject doInBackground(Void... params) {

			JSONObject jsonF = userFunctions.updateFriendRequest(
					DashboardActivity.emailList.get(pos), emailId, status);
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
			progressDialog = ProgressDialog.show(RequestsActivity.this, "",
					"Loading...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.requests, menu);
		return true;
	}

}
