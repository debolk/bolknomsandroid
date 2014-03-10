package com.stonethehuman.bolknoms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.util.Log;
import android.view.View;

public class ApplicationForm implements Runnable{
	private String hostname;
	private String path;
	private String response;

	private String params;
	private String name;
	private String email;
	private String handicap;
	private String meals;
	private boolean sent;
	private ArrayList<String> mealList;
	private Activity act;
	private boolean done;


	public ApplicationForm(Activity act) {
		hostname = "noms.debolk.nl"; //noms.debolk.nl
		path = "/uitgebreid-inschrijven"; ///
		sent = false;
		
		name = "";
		email = "";
		handicap = "";
		meals = "";
		
		mealList = new ArrayList<String>();
		this.act = act;
		done = false;
		setMeals();
	}
	
	public void run() {
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setHandicap(String handicap) {
		this.handicap = handicap;
	}

	public void setMeals() {
		Thread networkThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (sent == false) {
						sent = true;
						URL url = new URL("http://" + hostname + path);
						HttpURLConnection con = (HttpURLConnection) url.openConnection();
						InputStreamReader isr = new InputStreamReader(con.getInputStream());
						BufferedReader br = new BufferedReader(isr);
	
						while(!br.ready())
							System.out.println("Waiting for rd"); //wait till reader is ready
						Log.i("setMeals()", "rd ready.");
						String mealResponse = "";
						String line;
						while((line = br.readLine()) != null) {
							mealResponse += line + "\n";
//							Log.i("setMeals()", "line:" + line);
						}
	//					socket.close();
						
						Log.i("setMeals()", "written mealResponse.");
						System.out.println(mealResponse);
						
						Pattern pat = Pattern.compile("<input name=\"meals\\[\\]\" type=\"checkbox\" value=\"");
						Matcher mat = pat.matcher(mealResponse);
						
						while (mat.find()) {
							int start = mat.end();
							int end = mealResponse.indexOf("\"", start) + 1;
							mealList.add(mealResponse.substring(start, end - 1));
						}
						
						meals = mealList.get(0);
						System.out.println(mealList.toString());
					}
				} catch(Exception e) {
					Log.i("ApplicationForm.setMeals", "problem with connecting:");
					e.printStackTrace();
					sent = false;
				}
			}
		});
		networkThread.start();
	}

	public void initParams() {
		try {
			params = "";
			params += URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
			params += "&";
			params += URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
			params += "&";
			params += URLEncoder.encode("handicap", "UTF-8") + "=" + URLEncoder.encode(handicap, "UTF-8");
			params += "&";
			params += URLEncoder.encode("meals[]", "UTF-8") + "=" + URLEncoder.encode(meals, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendApplication() {
		Thread networkThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(meals == null)
						System.out.println("meals be null"); //wait till the meal value is set
					initParams();
					System.out.println("Signing up for meal: " + meals);
					
					URL url = new URL("http://noms.debolk.nl/uitgebreidaanmelden");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setReadTimeout(10000);
					conn.setConnectTimeout(15000);
					conn.setRequestMethod("POST");
					conn.setDoInput(true);
					conn.setDoOutput(true);
					
					Log.i("params", params);

					OutputStream os = conn.getOutputStream();
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
					writer.write(params);
					writer.flush();
					writer.close();
					os.close();

					conn.connect();
					
					try {
						BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						while(!rd.ready()); //wait till reader is ready
				
						response = "";
						String line;
						while((line = rd.readLine()) != null) {
							response += line + "\n";
	//						System.out.println(line);
						}
						System.out.println(response);
					} catch (Exception e) {
						e.printStackTrace();
						response = "";
					}
					done = true;
				} catch (Exception e) {
					e.printStackTrace();
				}					
			}
		});
		networkThread.start();
	}
	
	public String response() {
		return response;
	}
	
	public boolean done() {
		return done;
	}
}