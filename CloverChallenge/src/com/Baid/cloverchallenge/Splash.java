package com.Baid.cloverchallenge;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Splash extends Activity {

	Button begin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		begin = (Button)findViewById(R.id.button1);
		begin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					
					Class ourClass = Class.forName("com.Baid.cloverchallenge.MainActivity");
					Intent ourIntent= new Intent(Splash.this, ourClass);
					//if the criteria is met, activity wills start
					startActivity(ourIntent);

						
				} catch (ClassNotFoundException e) {
					
					e.printStackTrace();
				}
				
			}
		});
		
	}
	
	

}
