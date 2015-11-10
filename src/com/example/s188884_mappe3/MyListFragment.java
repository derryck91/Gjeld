package com.example.s188884_mappe3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MyListFragment extends ListFragment
{
	DBAdapter db;
	View inflatedView = null;
	View rowView = null;
	ListView listView;
	TextView antall;
	TextView radSum;
	TextView totalsumview;
	ImageView upImage;
	ImageView downImage;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		this.inflatedView = inflater.inflate(R.layout.my_list_fragment, container, false);
		
		antall = (TextView)inflatedView.findViewById(R.id.antall);
		upImage = (ImageView)inflatedView.findViewById(R.id.totalup);
		downImage = (ImageView)inflatedView.findViewById(R.id.totaldown);
		totalsumview = (TextView) inflatedView.findViewById(R.id.totalsum);
		return inflatedView;	
	}

	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		final DBAdapter db = new DBAdapter(getActivity());
		
		ArrayList<HashMap<String, String>> Items = new ArrayList<HashMap<String,String>>();
		List<Person> data = db.visAllePersoner();
		for(Person val : data)
		{
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("_ID", Integer.toString(val.getID()));
			map.put("fornavn", val.getFornavn());
			map.put("etternavn", val.getEtternavn());
			map.put("nummer", val.getNummer());
			map.put("sumRad", Integer.toString(db.finnSumGjeldPerson(val.getID())));
			
			if(db.finnSumGjeldPerson(val.getID())==0)
			{
				map.put("sumRad", "-");
			}

			Items.add(map);
		}
		ListAdapter adapter = new SimpleAdapter(getActivity(), Items, R.layout.list_item, new String[]{"_ID","fornavn","etternavn","nummer","bilde","sumRad"}, 
				new int[] {R.id.id, R.id.fornavn, R.id.etternavn, R.id.nummer,R.id.bilde, R.id.sum});
		
		setListAdapter(adapter);
		
		String count = Integer.toString(db.antallDBPerson());
		antall.setText(getResources().getText(R.string.count) + " " + count);
		if(db.antallDBPerson() < 1)
		{
			antall.setVisibility(View.GONE);
		}
		
		int antallgjeld = db.antallGjeld();
		int totalsum = db.sumGjeld();
		if(totalsum>0)
		{
			downImage.setVisibility(View.INVISIBLE);
			upImage.setVisibility(View.VISIBLE);
			totalsumview.setText(getResources().getText(R.string.overskudd) + " " + totalsum + " " + getResources().getText(R.string.kr));
		}
		else if(totalsum<0)
		{
			upImage.setVisibility(View.INVISIBLE);
			downImage.setVisibility(View.VISIBLE);
			totalsumview.setText(getResources().getText(R.string.underskudd) + " " + totalsum + " " + getResources().getText(R.string.kr));
		}
		else if(antallgjeld<1)
		{
			upImage.setVisibility(View.GONE);
			downImage.setVisibility(View.GONE);
			totalsumview.setWidth(480);
			totalsumview.setText(getResources().getText(R.string.gjeldsfri));
		}
		else
		{
			upImage.setVisibility(View.GONE);
			downImage.setVisibility(View.GONE);
			totalsumview.setWidth(480);
			totalsumview.setText(getResources().getText(R.string.likestore));
		}
		
		listView = getListView();
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				String fnavn = ((TextView)view.findViewById(R.id.fornavn)).getText().toString();
				String enavn = ((TextView)view.findViewById(R.id.etternavn)).getText().toString();
				String nummer = ((TextView)view.findViewById(R.id.nummer)).getText().toString();
				int personid = Integer.parseInt(((TextView)view.findViewById(R.id.id)).getText().toString());
				
				FragmentManager fm = getFragmentManager();
				fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	    		FragmentTransaction trans = fm.beginTransaction();
				DetailsFragment details = new DetailsFragment();
				
				Bundle bun = new Bundle();
				bun.putInt("id", personid);
		        bun.putString("fornavn", fnavn);
		        bun.putString("etternavn", enavn);
		        bun.putString("nummer", nummer);
		        details.setArguments(bun);
				
	    		trans.replace(R.id.startup_layout, details, "DetailsFragment");
	    		trans.addToBackStack("ListFragment");
	    		trans.commit();
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view,int position, long id) 
			{
				String navn = ((TextView)view.findViewById(R.id.fornavn)).getText().toString() + " " + ((TextView)view.findViewById(R.id.etternavn)).getText().toString();
				AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
				alert
				.setTitle(R.string.deletetitle)
                .setMessage(getResources().getText(R.string.deletemessage)+ " " + navn+"?")
                .setCancelable(true)
				.setNegativeButton(R.string.deletebutton, new DialogInterface.OnClickListener() 
                {
                	int DBid = Integer.parseInt(((TextView) view.findViewById(R.id.id)).getText().toString());
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						db.slettEnPerson(DBid);
						db.slettGjeldPerson(DBid);
						FragmentManager fm = getFragmentManager();
						FragmentTransaction trans = fm.beginTransaction();
						MyListFragment list = new MyListFragment();
			    		trans.remove(list);
			    		trans.replace(R.id.startup_layout, list, "ListFragment");
						trans.commit();
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
				AlertDialog alertDialog = alert.create();
                alertDialog.show();
				return true;
			}
		});
	}
}
