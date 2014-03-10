package com.stonethehuman.bolknoms;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class FailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("Bolknoms");
		setContentView(R.layout.activity_fail);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fail, menu);
		return true;
	}

}
