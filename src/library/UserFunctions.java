package library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class UserFunctions {


	private JSONParser jsonParser;

	private static String URL = "http://198.57.240.19/~rockerz/georemindme/";

	private static String login_tag = "login";
	private static String register_tag = "register";
	private static String send_req = "sendRequest";
	private static String check_req = "checkRequest";
	private static String up_req = "addRequest";

	private static String set_reminder = "addReminder";
	private static String check_reminder = "checkReminder";
	private static String del_reminder = "deleteReminder";

	// constructor
	public UserFunctions() {
		jsonParser = new JSONParser();
	}

	/**
	 * function make Login Request
	 * 
	 * @param email
	 * @param password
	 * */
	public JSONObject loginUser(String email, String password) {
		// Building Parameters
		final List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("tag", login_tag));
		paramsList.add(new BasicNameValuePair("email", email));
		paramsList.add(new BasicNameValuePair("password", password));

		JSONObject json = jsonParser.getJSONFromUrl(URL, paramsList);
		// return json
		// Log.e("JSON", json.toString());
		return json;

	}

	/**
	 * function make Login Request
	 * 
	 * @param name
	 * @param email
	 * @param password
	 * */
	public JSONObject registerUser(String name, String email, String password) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));

		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(URL, params);
		// return json
		return json;
	}

	public JSONObject sendFriendRequest(String emailId, String toEmailId) {
		// Building Parameters
		List<NameValuePair> paramsa = new ArrayList<NameValuePair>();
		paramsa.add(new BasicNameValuePair("tag", send_req));
		paramsa.add(new BasicNameValuePair("fromemail", emailId));
		paramsa.add(new BasicNameValuePair("toemail", toEmailId));
		// Log.d("emails", emailId + " dfg " + toEmailId);

		// getting JSON Object
		JSONObject json1 = jsonParser.getJSONFromUrl(URL, paramsa);
		Log.d("req", json1 + "");
		return json1;
	}

	public JSONObject checkFriendRequest(String emailId) {
		// Building Parameters
		List<NameValuePair> paramsa = new ArrayList<NameValuePair>();
		paramsa.add(new BasicNameValuePair("tag", check_req));
		paramsa.add(new BasicNameValuePair("email", emailId));

		// getting JSON Object
		JSONObject json2 = jsonParser.getJSONFromUrl(URL, paramsa);
		Log.d("req", json2 + "");
		return json2;
	}

	public JSONObject updateFriendRequest(String fromEmailId, String toEmailId,
			String newstatus) {
		// Building Parameters
		List<NameValuePair> paramsa = new ArrayList<NameValuePair>();
		paramsa.add(new BasicNameValuePair("tag", up_req));
		paramsa.add(new BasicNameValuePair("fromemail", fromEmailId));
		paramsa.add(new BasicNameValuePair("toemail", toEmailId));
		paramsa.add(new BasicNameValuePair("status", newstatus));
		Log.d("req", paramsa + "");

		// getting JSON Object
		JSONObject json2 = jsonParser.getJSONFromUrl(URL, paramsa);
		Log.d("req", json2 + "");
		return json2;
	}

	/**
	 * Function get Login status
	 * */
	public boolean isUserLoggedIn(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount();
		if (count > 0) {
			// user logged in
			return true;
		}
		return false;
	}

	public String getEmail(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		HashMap<String, String> user = db.getUserDetails();
		String Email = user.get("email");
		return Email;
	}

	/**
	 * Function to logout user Reset Database
	 * */
	public boolean logoutUser(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}

	public JSONObject setReminder(String fromEmail, String toEmail, String description,
			String latitude, String longitude, String address, String radius) {
		// Building Parameters
		List<NameValuePair> paramsa = new ArrayList<NameValuePair>();
		paramsa.add(new BasicNameValuePair("tag", set_reminder));
		paramsa.add(new BasicNameValuePair("fromemail", fromEmail));
		paramsa.add(new BasicNameValuePair("toemail", toEmail));
		paramsa.add(new BasicNameValuePair("title", description));
		paramsa.add(new BasicNameValuePair("latitude", latitude));
		paramsa.add(new BasicNameValuePair("longitude", longitude));
		paramsa.add(new BasicNameValuePair("address", address));
		paramsa.add(new BasicNameValuePair("radius", radius));

		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(URL, paramsa);
		Log.d("req", json + "");
		return json;
	}

	public JSONObject checkReminder(String emailId) {
		// Building Parameters
		List<NameValuePair> paramsa = new ArrayList<NameValuePair>();
		paramsa.add(new BasicNameValuePair("tag", check_reminder));
		paramsa.add(new BasicNameValuePair("email", emailId));

		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(URL, paramsa);
		Log.d("req", json + "");
		return json;
	}

	public JSONObject deleteReminder(String id) {
		// Building Parameters
		List<NameValuePair> paramsa = new ArrayList<NameValuePair>();
		paramsa.add(new BasicNameValuePair("tag", del_reminder));
		paramsa.add(new BasicNameValuePair("id", id));

		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(URL, paramsa);
		Log.d("req", json + "");
		return json;
	}

}