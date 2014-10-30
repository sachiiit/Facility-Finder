package com.sachchit.facilityfinder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

//import com.androidhive.androidlistview.SingleListItem;

//import com.sachchit.listviewonclick.MainActivity;

//import com.sachchit.currentlocation.R;

//import com.sachchit.currentlocation.R;

//import com.example.wherehealth.R;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MedicalSearchActivity extends Activity implements LocationListener 
{

	protected LocationManager locationManager;
	protected LocationListener locationListener;
	JSONObject jloc;
	TextView resultView;
	ListView replyView;
	TextView valView;
	EditText searchBar;
	Button search;
	String result = "";
	Spinner spin;
	double mylat=26.249622;
	double mylong=78.174322;
	JSONArray jArray;
	long t1,t2,t;

	
	//sachchit code
	
	
	public float getBatteryLevel() {
	float status ;
    Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
    if(level == -1 || scale == -1) {
        return 50.0f;
    }
    status = ((float)level / (float)scale) * 100.0f;
    return status; 
}

	public void turnGPSOff()
	{
    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
    if(provider.contains("gps"))
    {
        final Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("3"));
        sendBroadcast(intent);
    }
}
	
	
	public void turnGPSOn()
	{
    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
    if(!provider.contains("gps"))
    {
        final Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("3"));
        sendBroadcast(intent);
    }
}
	

	// sachchit bansal code ends
	
	
	
	
	
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		t1=System.currentTimeMillis();
		
		//full screen
				requestWindowFeature(Window.FEATURE_NO_TITLE);
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		setContentView(R.layout.activity_test_external_database);
		//StrictMode.enableDefaults();
		replyView = (ListView)findViewById(R.id.replylist);
		searchBar = (EditText) findViewById(R.id.search_bar);
		search = (Button) findViewById(R.id.search);
		spin = (Spinner) findViewById(R.id.spin);
		
		
		//sachchit code
		float level2=getBatteryLevel();



		if(level2>50)
		{
			turnGPSOn();
			float level3=level2/10;
			float minus=10-level3;
			long tym= (long) Math.pow(minus, 1.8);
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tym*1000, 0,(LocationListener) this);
		}

		else
		{
			turnGPSOff();
			float level3=level2/10;
			float minus=5-level3;
			long tym= (long) Math.pow(minus, 1.8);
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, tym*1000, 0, (LocationListener) this);
		}

		// geocoding
		
		 try{
			 HttpClient httpclient = new DefaultHttpClient();
			 HttpPost httploc = new HttpPost("http://maps.googleapis.com/maps/api/geocode/json?latlng="+mylat+","+mylong+"&sensor=false");
			 HttpResponse resploc = httpclient.execute(httploc);
			 HttpEntity entityloc = resploc.getEntity();
			 InputStream isrloc = entityloc.getContent();
			 BufferedReader reader = new BufferedReader(new InputStreamReader(isrloc, "iso-8859-1"),8);
			 StringBuilder sb = new StringBuilder();
			 String line = null ;
			 while((line = reader.readLine())!= null){
			 sb.append(line + "\n");
			 }
			 result = sb.toString();
			 JSONObject job = new JSONObject(result);
			 JSONArray jArray = job.getJSONArray("results");
			 jloc = jArray.getJSONObject(0);

			 }
			 catch(Exception e){
			 Log.e("log_tag", "Error in getting your location name"+e.toString());
			 //Toast.makeText(getApplicationContext(), "Couldn't connect to Database", Toast.LENGTH_LONG).show();
			 }

		
		
		
		// end geocoding
		// sachchit code ends
		
		
		
		
		String[] spc = {"Not Specific","24 X 7 Help","General Physician","Multi-Speciality","ICU Facility","Blood Bank","X-Ray & Scans","Dentist","Eye Specialist","Ear-Nose-Throat Specialist","Skin Specialist","Child Specialist","Gynecologist","Surgery","Psychiatrist","Sexual Health","Test Tube Baby(IVF)","Physiotherapists","Heart Care","Cancer Treatment","Dietitian"};
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MedicalSearchActivity.this, android.R.layout.simple_spinner_dropdown_item, spc);
		spin.setAdapter(spinnerArrayAdapter);
		getData();
		
		replyView.setTextFilterEnabled(true);
		replyView.setOnItemClickListener(new OnItemClickListener()
		{
			

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				
				/*AlertDialog.Builder adb = new AlertDialog.Builder(MedicalSearchActivity.this);
				adb.setTitle("ListView OnClick");*/
				String sel = replyView.getItemAtPosition(position).toString();
				sel = sel.replaceAll("\n", " ");
				sel = sel.replaceAll(" km away", "");
				 
				// TODO Auto-generated method stub
				//int hid = 0;
				String hname,htitle,haddress,contact;
				double lat2,long2;
				try{
					for(int i=0;i<jArray.length();i++)
					{
						JSONObject json = jArray.getJSONObject(i);
						if((json.getString("Name")+" "+json.getString("Distance")).toLowerCase().trim().equals((sel.toLowerCase().trim())))
						{
							hname=json.getString("Name");
							htitle=json.getString("Title");
							haddress=json.getString("Address");
							contact=json.getString("Contact");
							lat2=json.getDouble("Latitude");
							long2=json.getDouble("Longitude");
							//launching new activity
							
							Intent i1 = new Intent(getApplicationContext(), SingleListItem.class);
							//i1.putExtra("hosp", hid);
							i1.putExtra("hospname", hname);
							i1.putExtra("htit", htitle);
							i1.putExtra("hadd", haddress);
							i1.putExtra("con", contact);
							i1.putExtra("latt2", lat2); 
							i1.putExtra("longg2", long2);
							startActivity(i1);
				        	
				        	  break;
							
						}
						else
						{
							Log.e("log_tag", (json.getString("Name")+" "+json.getString("Distance")).toLowerCase()+" && "+sel.toLowerCase());
						}
					}
				}
				catch(Exception e){ }
				//adb.setMessage("Selected Hospital is "+ hid);
				//adb.setPositiveButton("Ok", null);
				//adb.show(); 
			}
        });
		
		
	}
	
	

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.medical_search, menu);
		return true;
	}
	
	public void getData(){
		
		InputStream isr = null;
		try{
			HttpClient httpclient = new DefaultHttpClient();
			//HttpPost httppost = new HttpPost("http://192.168.67.52/adbc/MedicalDB.php");
			//HttpPost httppost = new HttpPost("http://samdarshiapps.host56.com/BTP_MedicalDB.php");
			HttpPost httppost = new HttpPost("http://samdarshiapps.host56.com/geo_test.php");
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			isr = entity.getContent();
		}
		catch(Exception e){
			Log.e("log_tag", "Error in http connection"+e.toString());
			Toast.makeText(getApplicationContext(), "Couldn't connect to Database", Toast.LENGTH_LONG).show();
		}
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(isr, "iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line = null ;
			while((line = reader.readLine())!= null){
				sb.append(line + "\n");			
			}
			isr.close();
			result = sb.toString();
			t2=System.currentTimeMillis();
			t=t2-t1;
			String s=String.valueOf(t);
			//Toast.makeText(getApplicationContext(), ch, Toast.LENGTH_LONG).show();
			Log.e("Time is ", s);
		}
		catch(Exception e){
			Log.e("log_tag", "Error converting result" + e.toString());
		}
		
		search.setOnClickListener(new View.OnClickListener() {
						
			public void onClick(View arg0) {
				try{
					
					Toast.makeText(getApplicationContext(), "Showing distance from "+jloc.getString("formatted_address"), Toast.LENGTH_LONG).show();

					//sachchit code
					/*float level2=getBatteryLevel();
					String s = Float.toString(level2);
					Toast.makeText(getApplicationContext(), 
			                s, Toast.LENGTH_LONG).show();
					*/
					// sachchit code ends
					
					
					
					jArray = new JSONArray(result);
					String query = searchBar.getText().toString();
					ArrayList<String> reply = new ArrayList<String>();
					String spec = spin.getSelectedItem().toString();
					Log.e("log_tag", query);
					if(query.trim().length() > 0)
					{
						for(int i=0;i<jArray.length();i++)
						{
							JSONObject json = jArray.getJSONObject(i);
							double temp = ((int)distFrom(mylat, mylong, json.getDouble("Latitude"), json.getDouble("Longitude"))/100)/10.0;
							json.put("Distance", temp);
							temp = similarity(json.getString("Name").toLowerCase(), query.toLowerCase());
							json.put("Similarity", temp);
						}
						for(int i=0;i<jArray.length();i++)
						{
							for(int j=0;j<jArray.length()-i-1;j++)
							{
								if(jArray.getJSONObject(j+1).getDouble("Similarity")>jArray.getJSONObject(j).getDouble("Similarity"))
								{
									JSONObject json = jArray.getJSONObject(j);
									jArray.put(j, jArray.getJSONObject(j+1));
									jArray.put(j+1, json);
								}
							}
						}
						for(int i=0;i<jArray.length();i++)
						{
							JSONObject json = jArray.getJSONObject(i);
							if(json.getString("Facilities").toLowerCase().contains(spec.toLowerCase()))
							{
								reply.add(json.getString("Name")+"\n"+json.getString("Distance")+" km away");
							}	
						}
					}
					else
					{
						//geocoding toast 
						
						

						
						for(int i=0;i<jArray.length();i++)
						{
							JSONObject json = jArray.getJSONObject(i);
							double temp = ((int)distFrom(mylat, mylong, json.getDouble("Latitude"), json.getDouble("Longitude"))/100)/10.0;
							json.put("Distance", temp);						
						}
						for(int i=0;i<jArray.length();i++)
						{
							for(int j=0;j<jArray.length()-i-1;j++)
							{
								if(jArray.getJSONObject(j+1).getDouble("Distance")<jArray.getJSONObject(j).getDouble("Distance"))
								{
									JSONObject json = jArray.getJSONObject(j);
									jArray.put(j, jArray.getJSONObject(j+1));
									jArray.put(j+1, json);
								}
							}
						}
						for(int i=0;i<jArray.length();i++)
						{
							JSONObject json = jArray.getJSONObject(i);
							if(json.getString("Facilities").toLowerCase().contains(spec.toLowerCase()))
							{
								reply.add(json.getString("Name")+"\n"+json.getString("Distance")+" km away");
							}	
						}
					}

					ArrayAdapter<String> adapter = new ArrayAdapter<String>(MedicalSearchActivity.this,
		                    android.R.layout.simple_list_item_1, reply);
		        
					replyView.setAdapter(adapter);
				}
				catch(Exception e){
					
					Log.e("log_tag", "Error Parsing Data"+e.toString());
				}
			}	
	});
		
	}
	
	public static double similarity(String s1, String s2) {
        if (s1.length() < s2.length()) { // s1 should always be bigger
            String swap = s1; s1 = s2; s2 = swap;
        }
        int bigLen = s1.length();
        if (bigLen == 0) { return 1.0; /* both strings are zero length */ }
        return (bigLen - computeEditDistance(s1, s2)) / (double) bigLen;
    }
	
	//distance from latitude and longitude
	
	@SuppressLint("UseValueOf")
	public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
	    double earthRadius = 3958.75;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;
	    
	    return new Double(dist * meterConversion).doubleValue();
	    }

	// end distance
	
    public static int computeEditDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		mylat=location.getLatitude();
		mylong=location.getLongitude();
		getData();
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		getData();
		
	}
	

}
