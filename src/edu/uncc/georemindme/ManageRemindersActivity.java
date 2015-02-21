package edu.uncc.georemindme;

import java.util.concurrent.ExecutionException;

import library.UserFunctions;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ManageRemindersActivity extends Activity {
	private ProgressDialog progressDialog;
	UserFunctions userFunctions;
	int pos;
	private JSONObject json;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_reminders);

		userFunctions = new UserFunctions();
		if (userFunctions.isUserLoggedIn(getApplicationContext())) {

			ListView lv = (ListView) findViewById(R.id.ListView1);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, android.R.id.text1,
					DashboardActivity.titleList);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					pos = position;

					AlertDialog.Builder builder = new AlertDialog.Builder(
							ManageRemindersActivity.this);
					builder.setMessage("Take Action")
							.setNeutralButton("Delete",
									new DialogInterface.OnClickListener() {
								UserFunctions userFunction = new UserFunctions();
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Log.d("inner",
													"you clicked delete"
															+ DashboardActivity.idList
																	.get(pos));
											try {
												json = new AsyncTask<Void, Void, JSONObject>() {

													@Override
													protected JSONObject doInBackground(Void... params) {

														JSONObject jsonF = userFunction.deleteReminder(DashboardActivity.idList
																.get(pos)+"");
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
																ManageRemindersActivity.this, "", "Loading...");
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
											
											Log.d("req", json + "");
											Toast.makeText(
													getApplicationContext(),
													"Alert Removed",
													Toast.LENGTH_SHORT).show();
											finish();
										}
									}).show();

					// builder.create();
//					Log.d("pos", DashboardActivity.emailList.get(position));

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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_reminders, menu);
		return true;
	}

}
