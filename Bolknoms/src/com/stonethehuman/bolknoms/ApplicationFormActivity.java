package com.stonethehuman.bolknoms;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ApplicationFormActivity extends Activity implements Runnable {
	private ApplicationForm aForm;
	private String response;
	private EditText name;
	private EditText email;
	private EditText handicap;
	private TextView output;
	private ApplicationFormActivity acty = this;
	private SharedPreferences sp;
	private String lastMeal;
	private String nameData;
	private String emailData;
	private String handicapData;
	
	public void run() {
		
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_form);

        aForm = new ApplicationForm(this);
        
        sp = getSharedPreferences("main", 0);
        lastMeal = sp.getString("last_meal", null);
        nameData = sp.getString("name", null);
        emailData = sp.getString("email", null);
        handicapData = sp.getString("handicap", null);

        name = (EditText)this.findViewById(R.id.name_field);
        email = (EditText)this.findViewById(R.id.email_field);
        handicap = (EditText)this.findViewById(R.id.handicap_field);
        Button button = (Button)this.findViewById(R.id.send_button);
        
        name.setText(nameData);
        email.setText(emailData);
        handicap.setText(handicapData);
        
        button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setWait(true);
				String nameText = name.getText().toString();
				String emailText = email.getText().toString();
				String handicapText = handicap.getText().toString();

				aForm.setName(nameText);
				aForm.setEmail(emailText);
				aForm.setHandicap(handicapText);
				
				Editor editor = sp.edit();
				if (nameText != nameData)
					editor.putString("name", nameText);
				if (emailText != emailData)
					editor.putString("email", emailText);
				if (handicapText != handicapData)
					editor.putString("handicap", handicapText);
				editor.commit();

				aForm.sendApplication();
				
				while (!aForm.done()); //wait till bolknoms gives a response
				if (aForm.response().contains("html")) {
					setWait(false);
					Log.i("button clicked", "sent application");
					confirm();
				} else {
					setWait(false);
					fail();
				}
			}
        	
        });
    }
    
    public void fail() {
		Intent intent = new Intent(this, FailActivity.class);
    	startActivity(intent);
    }
    
    public void confirm() {
		Intent intent = new Intent(this, ConfirmActivity.class);
    	startActivity(intent);
    }
    
    public void setWait(boolean wait) {
		View wv = findViewById(R.id.loading);
    	if (wait) {
    		wv.setVisibility(View.VISIBLE);
    	} else {
    		wv.setVisibility(View.INVISIBLE);
    	}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.application_form, menu);
        return true;
    }
    
}
