package com.example.com.sm.smartmattress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HelpBlurb extends ActionBarActivity implements OnClickListener {
	
	public Button backButton;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_blurb);
		backButton = (Button)findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId())
		{	
			case R.id.backButton:
				Intent i = new Intent(this, MainActivity.class);
				finish();
				startActivity(i);
		}
			
		
	}

}
