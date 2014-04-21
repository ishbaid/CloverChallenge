package com.Baid.cloverchallenge;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;





import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{

	final static String EMPTY_STRING = "";
	
	//takes in input and calculates primes
	Button calculate;
	
	//allows user to input value
	EditText input, results;
	
	//shows information
	TextView header;
	
	//used to hide keyboard 
	RelativeLayout back;
	
	//keeps track of all prime number
	ArrayList<Integer> allPrimes;
	
	//keeps track of how many primes have been calculated
	int primeN = 0;
	
	//keeps track on number of primes currently calculated
	int numPrimes = 0;
	//number we have to calculate up to
	int N = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		calculate = (Button)findViewById(R.id.button1);
		calculate.setOnClickListener(this);
		
		input = (EditText)findViewById(R.id.editText1);
		results = (EditText)findViewById(R.id.results);
		
		header = (TextView)findViewById(R.id.textView1);
		
		//hides keyboard on touch
		back = (RelativeLayout)findViewById(R.id.back);
	    back.setOnTouchListener(new OnTouchListener() {


			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
	            InputMethodManager inputManager = (InputMethodManager) getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
	            inputManager.hideSoftInputFromWindow(input
	                    .getWindowToken(), 0);
	            return true;
			}
	    });
	    
	    //keeps track of all primes
	    allPrimes = new ArrayList<Integer>();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	//save data
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		//saves arraylist
		ArrayList <String>s1=new ArrayList<String>();


		for(int i=0;i < allPrimes.size();i++){

			//converts all primes into strings in order to be stored
			s1.add(allPrimes.get(i) + "");
		}
		
		FileOutputStream output;
		try {
			output = openFileOutput("lines.txt",MODE_PRIVATE);
			DataOutputStream dout = new DataOutputStream(output);
			dout.writeInt(s1.size()); // Save line count
			for(String line : s1) // Save lines
				dout.writeUTF(line);
			dout.flush(); // Flush stream ...
			dout.close(); // ... and close.
		} catch (IOException exc) { exc.printStackTrace(); } 

		//saves important data
		SharedPreferences sharedPreferences = getPreferences(MainActivity.this.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("numPrimes", primeN);
		editor.putInt("N", N);
		editor.commit();
	}

	//load data
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//gets saved data
		ArrayList<String>s1= new ArrayList<String>();
		
		FileInputStream input;
		
		try {
			input = openFileInput("lines.txt");
			DataInputStream din = new DataInputStream(input);
			int sz = din.readInt(); // Read line count
			for (int i=0;i<sz;i++) { // Read lines
				String line = din.readUTF();
				s1.add(line);
			}			
			din.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} // Open input stream
		catch (IOException e) {
			// TODO Auto-generated catch block
		
		}
		
		//recreate the allPrimes arraylist
		allPrimes = new ArrayList<Integer>();
		
		for(int i = 0; i < s1.size(); i ++){
			
			String item = s1.get(i);
			//converts string back to int
			int itemVal = Integer.parseInt(item);
			allPrimes.add(itemVal);
		}
		
		//gets saved data
		SharedPreferences sharedPreferences = getPreferences(MainActivity.this.MODE_PRIVATE);
		primeN =sharedPreferences.getInt("numPrimes", 0);
		N = sharedPreferences.getInt("N", primeN);
		
		if(primeN != 0 && allPrimes.size() != 0){
		
			Log.d("Baid", "Success: " + primeN);
			printResults();
			
		}
	}
	
	
	//handles click of calculate button
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.getId() == calculate.getId()){
			
			String inputString = input.getText().toString();
			
			//clear text
			input.setText("");
			//surround by try-catch?
			
			
			if(!inputString.equals(EMPTY_STRING)){
				N = Integer.parseInt(inputString);
				
				
				//calculates primes only if we haven't already calculated it
				if(N > numPrimes){
					
					new load().execute(N);
					
					//turn these gray or something
					calculate.setEnabled(false);
					input.setEnabled(false);
				}
				//print results
				else
					printResults();
				
			}	
		}
		
		
	}
	
	//prints results into edit text
	private void printResults(){
		
		
		//clear last results
		results.setText(EMPTY_STRING);
		
		int value = 0;
		int next = 0;
		int counter = 0;
		//prints results up to the requested number, N
		do{
			
			value = (Integer)allPrimes.get(counter);
			results.setText(value + "\n" + results.getText().toString());
			counter ++;
			
			if(counter < allPrimes.size())
				next = (Integer)allPrimes.get(counter);
			
		}while(counter < N && counter < allPrimes.size());
		
		header.setText("Showing first " + N + " prime numbers\n Primes calculated to: " + primeN );
		
	}
	
	//asyntask for calculating prime numbers
	public class load extends AsyncTask<Integer, Integer, String>{

		ProgressDialog prog;
		
		//sets up progress dialog
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			prog = new ProgressDialog(MainActivity.this);
			prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			prog.setMax(N);
			prog.show();
		}

		//handles what to do after calculation
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			//prints results
			printResults();
			
			//enables button and edittext
			calculate.setEnabled(true);
			input.setEnabled(true);
		}


		//updates progress
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			prog.incrementProgressBy(values[0]);
			
		}

		//calculates prime numbers
		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			
			int counter = allPrimes.size();

			//need to start checking primes from 1 above what we already
			//calculated to the number requested
			//we add to the counter until we reach the number of prime numbers we need
			for(int i = primeN + 1; counter <= params[0]; i ++){

				publishProgress(1);
				
				boolean isPrime = true;
				//don't need to check if multiple of 2
				//i dont like the check to see if it is equal to 2
				if(i % 2 == 0 && i != 2)
					isPrime = false;
				//only need to go up to sqrt(i)
				for(int j = 3; j <= Math.sqrt(i) && isPrime; j += 2){

					if(i % j == 0)
						isPrime = false;
					
				}
				//i is prime
				if(isPrime){
					
					
					allPrimes.add(i);
					//we have found a prime, therefore we increment counter
					counter ++;
					Log.d("Baid", "I is " + i);
					//results.setText(results.getText().toString() + i + "\n");

				}
			}
			prog.dismiss();
			
			//the number of prime numbers we have calculated corresponds to counter
			numPrimes = counter;
			
			
			//updates the number of primes that have been calculated up to
			if(params[0] > primeN)
				primeN = params[0];
			Log.d("Baid", "PrimeN is " + primeN);
			return null;
		}
		
		
	}
	

	
	

}
