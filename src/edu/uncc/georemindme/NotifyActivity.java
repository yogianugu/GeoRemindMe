package edu.uncc.georemindme;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class NotifyActivity extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notify);
		((TextView)findViewById(R.id.text)).setText(getIntent().getExtras().getString("text"));
		((TextView)findViewById(R.id.title)).setText(getIntent().getExtras().getString("title"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notify, menu);
		return true;
	}

}
