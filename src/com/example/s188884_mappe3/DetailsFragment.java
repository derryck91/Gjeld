package com.example.s188884_mappe3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsFragment extends ListFragment
{
	DBAdapter db;
	ImageButton deletebtn;
	ImageButton editbtn;
	ImageButton sendSms;
	ImageButton addbtn;
	Button betal;
	TextView betalt;
	TextView name;
	TextView number;
	View inflatedView = null;
	ListView listView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		this.inflatedView = inflater.inflate(R.layout.details_fragment, container, false);
		//View row = inflater.inflate(R.layout.gjeld_list_item, container,false);
		deletebtn = (ImageButton)inflatedView.findViewById(R.id.deleteButton);
		editbtn = (ImageButton)inflatedView.findViewById(R.id.editButton);
		addbtn = (ImageButton)inflatedView.findViewById(R.id.leggtilgjeld);
		betal = (Button)inflatedView.findViewById(R.id.betal);
		sendSms = (ImageButton)inflatedView.findViewById(R.id.sendsms);
		db = new DBAdapter(getActivity());
		name = (TextView) inflatedView.findViewById(R.id.navn);
		number = (TextView)inflatedView.findViewById(R.id.nummer);
		final String navn = getArguments().getString("fornavn") + " " + getArguments().getString("etternavn");
		
		if(getArguments().getString("nummer").isEmpty()||db.finnGjeldPerson(getArguments().getInt("id")).isEmpty()||db.finnSumGjeldPerson(getArguments().getInt("id"))==0)
		{
			sendSms.setVisibility(View.INVISIBLE);
		}
		
		betal.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("no.dnb.vipps");
				startActivity(launchIntent);
			}
		});
		
		sendSms.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
				alert
					
				 	 .setTitle(R.string.sendsms)
                     .setMessage(getResources().getText(R.string.smsmessage)+ " " + navn+"?")
                     .setCancelable(true)
                     .setNegativeButton(R.string.sendsms, new DialogInterface.OnClickListener() 
                     {
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							int sum = db.finnSumGjeldPerson(getArguments().getInt("id"));
							String sms;
							if(sum>0)
								sms = (String)getResources().getText(R.string.smsdel1) + " " + sum + " " + getResources().getText(R.string.smsdel2);
							else 
							{
								sum = sum * -1;
								sms = (String)getResources().getText(R.string.gjeldsmsdel1) + " " + sum + " " + getResources().getText(R.string.gjeldsmsdel2);
							}
							SmsManager smsManager = SmsManager.getDefault();
							smsManager.sendTextMessage(getArguments().getString("nummer"), null, sms, null, null);
							Toast.makeText(getActivity(), getResources().getText(R.string.smssendt)+ " " + navn,Toast.LENGTH_LONG).show();
						} 
                     })
                     .setPositiveButton(R.string.avbryt, new DialogInterface.OnClickListener() 
                     {
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.cancel();
						} 
                     });
				alert.show();
			}
		});
		
		editbtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				String fnavn = getArguments().getString("fornavn");
				String enavn = getArguments().getString("etternavn");
				String nummer = getArguments().getString("nummer");
				int personid = getArguments().getInt("id");
				
				EditFragment edit = new EditFragment();
				Bundle bun = new Bundle();
				bun.putInt("id", personid);
		        bun.putString("fornavn", fnavn);
		        bun.putString("etternavn", enavn);
		        bun.putString("nummer", nummer);
		        edit.setArguments(bun);
				
				FragmentManager fm = getFragmentManager();
				FragmentTransaction trans = fm.beginTransaction();
				trans.addToBackStack("DetailsFragment");
	    		trans.replace(R.id.startup_layout, edit, "EditFragment");
				
				trans.commit();
			}
			
		});
		
		addbtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				int personid = getArguments().getInt("id");
				String fnavn = getArguments().getString("fornavn");
				String enavn = getArguments().getString("etternavn");
				
				AddDebtFragment add = new AddDebtFragment();
				Bundle bun = new Bundle();
				bun.putInt("id", personid);
				bun.putString("fornavn", fnavn);
		        bun.putString("etternavn", enavn);
		        add.setArguments(bun);
				
				FragmentManager fm = getFragmentManager();
				FragmentTransaction trans = fm.beginTransaction();
				trans.addToBackStack("DetailsFragment");
	    		trans.replace(R.id.startup_layout, add, "AddDebtFragment");
				
				trans.commit();
			}
			
		});
		
		deletebtn.setOnClickListener(new OnClickListener()
        {
			public void onClick(View args) 
			{
				AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
				alert
					
				 	 .setTitle(R.string.deletetitle)
                     .setMessage(getResources().getText(R.string.deletemessage)+ " " + navn+"?")
                     .setCancelable(true)
                     .setNegativeButton(R.string.deletebutton, new DialogInterface.OnClickListener() 
                     {

						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							db.slettEnPerson(getArguments().getInt("id"));
							db.slettGjeldPerson(getArguments().getInt("id"));
							FragmentManager fm = getFragmentManager();
				    		fm.popBackStackImmediate();
						}
                     })
                     .setPositiveButton(R.string.avbryt, new DialogInterface.OnClickListener() 
                     {
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.cancel();
						}
					});
				alert.show();
			}
        });
		
		name.setText(navn);
		name.setPaintFlags(name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		
		if(getArguments().getString("nummer").isEmpty())
		{
			number.setTypeface(null, Typeface.ITALIC);
			number.setText(getResources().getText(R.string.ikkenummer));
		}
		else
			number.setText(getArguments().getString("nummer"));
		
		return inflatedView;
	}

	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		final DBAdapter db = new DBAdapter(getActivity());
		
		ArrayList<HashMap<String, String>> Items = new ArrayList<HashMap<String,String>>();
		List<Gjeld> data = db.finnGjeldPerson(getArguments().getInt("id"));
		
		for(Gjeld val : data)
		{
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("_ID", Integer.toString(val.getID()));
			map.put("sum", Integer.toString(val.getSum()));
			map.put("beskrivelse", val.getBeskrivelse());
			map.put("tid_opprettet", val.getOpprettet());
			map.put("sted", val.getSted());
			map.put("person_id", Integer.toString(val.getPersonID()));
			
			Items.add(map);
		}
		ListAdapter adapter = new SimpleAdapter(getActivity(), Items, R.layout.gjeld_list_item, new String[]{"_ID","sum","beskrivelse","tid_opprettet", "sted", "person_id"}, 
				new int[] {R.id.id, R.id.sum, R.id.beskrivelse, R.id.opprettet, R.id.sted, R.id.personid});
		
		setListAdapter(adapter);
		
		listView = getListView();
		
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) 
			{
				String beskrivelse = ((TextView)view.findViewById(R.id.beskrivelse)).getText().toString();
				AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
				alert
				 	 .setTitle(R.string.deletetitle)
                     .setMessage(getResources().getText(R.string.deletemessage)+ " " + beskrivelse +"?")
                     .setCancelable(true)
                     .setNegativeButton(R.string.betalt, new DialogInterface.OnClickListener() 
                     {
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							int DBid = Integer.parseInt(((TextView)view.findViewById(R.id.id)).getText().toString());
							db.slettEnGjeld(DBid);
							//Reloader fragment
							Fragment oppdater = getFragmentManager().findFragmentByTag("DetailsFragment");
							final FragmentTransaction ft = getFragmentManager().beginTransaction();
							ft.detach(oppdater);
							ft.attach(oppdater);
							ft.commit();
						} 
                     })
                     .setNeutralButton(R.string.deletebutton, new DialogInterface.OnClickListener()
                     {

						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							int DBid = Integer.parseInt(((TextView)view.findViewById(R.id.id)).getText().toString());
							db.slettEnGjeld(DBid);
							//Reloader fragment
							Fragment oppdater = getFragmentManager().findFragmentByTag("DetailsFragment");
							final FragmentTransaction ft = getFragmentManager().beginTransaction();
							ft.detach(oppdater);
							ft.attach(oppdater);
							ft.commit();

						}
                    	 
                     })
				.setPositiveButton(R.string.avbryt, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.cancel();
					}
				});
				alert.show();
			}
		});
	}
}
