package com.example.s188884_mappe3;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddDebtFragment extends Fragment
{
	LocationManager locationManager;
	Cursor cursor;
	double longitude;
	double latitude;
	String sted = null;
	int pId;
	View inflatedView = null;
	DBAdapter db;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		this.inflatedView = inflater.inflate(R.layout.add_debt_fragment, container, false);
		locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		
		final LocationListener locationListener = new LocationListener() 
		{
		    public void onLocationChanged(Location location) 
		    {
		        longitude = location.getLongitude();
		        latitude = location.getLatitude();
		    }

			@Override
			public void onStatusChanged(String provider, int status,Bundle extras) 
			{}

			@Override
			public void onProviderEnabled(String provider) 
			{}

			@Override
			public void onProviderDisabled(String provider) 
			{}
		};

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
		
		ImageButton settinngjeld = (ImageButton)inflatedView.findViewById(R.id.settinnGjeld);
		final Spinner personid = (Spinner) inflatedView.findViewById(R.id.settinnPersonid);
		db = new DBAdapter(getActivity());
		
		loadSpinnerData();
		settinngjeld.setOnClickListener(new OnClickListener()
        {
			RadioButton jegskylder = (RadioButton) inflatedView.findViewById(R.id.jegskylder);
			EditText sumgjeld = (EditText) inflatedView.findViewById(R.id.settinnSum);
		    EditText beskrivelsegjeld = (EditText) inflatedView.findViewById(R.id.settinnBeskrivelse);
		    
			@Override
			public void onClick(View v) 
			{
				String sumgjeldtekst = sumgjeld.getText().toString();
				String beskrivelsegjeldtekst = beskrivelsegjeld.getText().toString();
				if(!sumgjeldtekst.matches("") && !beskrivelsegjeldtekst.matches(""))
				{
					int sum = Integer.parseInt(sumgjeld.getText().toString());
					
					//Hvis jeg skylder, legges summen*-1 inn i db
					if(jegskylder.isChecked())
					{
						sum = sum * -1;
					}
					
					StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
					StrictMode.setThreadPolicy(policy); 
					
					//Kriterier for gps
					Criteria criteria = new Criteria();
		            criteria.setAccuracy(Criteria.ACCURACY_FINE);
		            criteria.setAltitudeRequired(false);
		            criteria.setBearingRequired(false);
		            criteria.setCostAllowed(true);
		            String provider = locationManager.getBestProvider(criteria, true);
					
		            
				    Location location =locationManager.getLastKnownLocation(provider);
				    if(location != null)
				    {
				    	longitude = location.getLongitude();
				        latitude = location.getLatitude();
				    	JSONObject res = getStedInfo(); 
				    	JSONObject thelocation;
				    	try 
				    	{
				    		//Velger hva som skal skrives ut og lagres i db. getJSONObject(X) henter ut X.
				    	    thelocation = res.getJSONArray("results").getJSONObject(0);
				    	    sted = thelocation.getString("formatted_address");
				    	} 
				    	catch (JSONException e1) 
				    	{
				    	    e1.printStackTrace();
				    	}
				    }
	
				    //Timestamp
					String beskrivelse = beskrivelsegjeld.getText().toString();
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");
					String opprettet = sdf.format(new Date());
	
					if(pId == 0)
					{
						Toast.makeText(getActivity(), getResources().getText(R.string.opprett), Toast.LENGTH_SHORT).show();
					}
					else
					{
						db.settInnGjeld(new Gjeld(sum, beskrivelse, opprettet,sted, pId));
						Toast.makeText(getActivity(), getResources().getText(R.string.opprettetgjeld), Toast.LENGTH_SHORT).show();
						sumgjeld.setText("");
						beskrivelsegjeld.setText("");
					}
				}
				else
					Toast.makeText(getActivity(), getResources().getText(R.string.fyllut), Toast.LENGTH_SHORT).show();
			}
        });
		
		personid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() 
	     {
	         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) 
	         {
	        	 ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
	             Person pValgt = (Person) parent.getItemAtPosition(pos);
	             pId = pValgt.getID();
	         }

	         @Override
	         public void onNothingSelected(AdapterView<?> arg0) 
	         {
	             Log.i("Melding", "Ingen er valgt");
	         }
	     });
		
		return inflatedView;
	}
	
	
	public JSONObject getStedInfo() 
	{
		//request til URL med latitude og longitude hentet fra devicen 
        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&sensor=true");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try 
        {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) 
            {
                stringBuilder.append((char) b);
            }
        } 
        catch (ClientProtocolException e) 
        {} 
        catch (IOException e) 
        {}

        JSONObject jsonObject = new JSONObject();
        try 
        {
            jsonObject = new JSONObject(stringBuilder.toString());
        } 
        catch (JSONException e) 
        {
            e.printStackTrace();
        }
        return jsonObject;
    }
	
	
	//Fyller spinner med data og setter bredden
	 private void loadSpinnerData() 
	 {
	        DBAdapter db = new DBAdapter(getActivity());
	        final Spinner personid = (Spinner) inflatedView.findViewById(R.id.settinnPersonid);
	        personid.setMinimumWidth(310);
	        personid.setDropDownWidth(310);
	        
	        personid.setSelection(0);
	        
	        List<Person> personer = db.visAllePersoner();
	        final ArrayAdapter <Person> arrayadapter = new ArrayAdapter<Person>( getActivity(),android.R.layout.simple_spinner_item,personer);               
	        arrayadapter.setDropDownViewResource(R.layout.spinner_textview);
	        
	     personid.setAdapter(arrayadapter); 
	 }
}