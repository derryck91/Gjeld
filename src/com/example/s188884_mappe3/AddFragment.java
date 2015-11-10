package com.example.s188884_mappe3;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class AddFragment extends Fragment
{
	final int PICK_CONTACT = 1;
	View inflatedView = null;
	DBAdapter db;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		this.inflatedView = inflater.inflate(R.layout.add_fragment, container, false);
		ImageButton settinn = (ImageButton)inflatedView.findViewById(R.id.settinn);
		ImageButton hentkontakt = (ImageButton)inflatedView.findViewById(R.id.addnumber);
		hentkontakt.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, PICK_CONTACT);
			}
        });
		
		db = new DBAdapter(getActivity());
		settinn.setOnClickListener(new OnClickListener()
        {
			EditText firstname = (EditText) inflatedView.findViewById(R.id.settinnFornavn);
		    EditText lastname = (EditText) inflatedView.findViewById(R.id.settinnEtternavn);
		    EditText number = (EditText) inflatedView.findViewById(R.id.settinnNummer);
			public void onClick(View v) 
			{
				String fnavn = firstname.getText().toString();
				String enavn = lastname.getText().toString();
				String nummer = number.getText().toString();
				if(gyldigNavn(fnavn) && gyldigNavn(enavn))
				{
					try
					{
						db.settInnPerson(new Person(fnavn,enavn,nummer));
						Toast.makeText(getActivity(), fnavn+" "+enavn+" "+" " + getResources().getText(R.string.lagttil), Toast.LENGTH_SHORT).show();
						firstname.setText("");
						lastname.setText("");
						number.setText("");
					}
					catch(SQLiteException e)
					{
						Toast.makeText(getActivity(), "Feil!", Toast.LENGTH_SHORT).show();
						
					}
				}
				else
				{
					AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
					alert
						
					 	 .setTitle(R.string.deletetitle)
	                     .setMessage(getResources().getText(R.string.regex))
	                     .setCancelable(true)
	                     .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() 
	                     {
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.cancel();
							}
						});
					alert.show();
				}
			}
        });
		
		return inflatedView;
	}

	public void onActivityResult(int reqCode, int resultCode, Intent data)
    {
		super.onActivityResult(reqCode, resultCode, data);
		//Henter ut nr fra kontakter
			if(resultCode == Activity.RESULT_OK)
				switch(reqCode)
				{
				case(PICK_CONTACT):
				{
					final EditText phoneInput = (EditText)inflatedView.findViewById(R.id.settinnNummer);
					Cursor cursor = null;
					String phoneNumber = "";
					List<String> allNumbers = new ArrayList<String>();
					int phoneIdx = 0;
					try
					{
						Uri result = data.getData();
						String id = result.getLastPathSegment();
						cursor = getActivity().getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + "=?", new String[]{ id }, null);
						phoneIdx = cursor.getColumnIndex(Phone.DATA);
						if(cursor.moveToFirst())
						{
							while(cursor.isAfterLast() == false)
							{
								phoneNumber = cursor.getString(phoneIdx);
								allNumbers.add(phoneNumber);
								cursor.moveToNext();
							}
						}
						else
						{}
					}
					catch(Exception e)
					{
						
					}
					finally
					{
						if(cursor != null)
							cursor.close();
					}
					
	               final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
	                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	                builder.setTitle(getResources().getText(R.string.velgnr));
	                builder.setItems(items, new DialogInterface.OnClickListener() 
	                {
	                    public void onClick(DialogInterface dialog, int item) 
	                    {
	                        String selectedNumber = items[item].toString();
	                        selectedNumber = selectedNumber.replace("-", "");
	                        phoneInput.setText(selectedNumber);
	                    }
	                });
	                
	                AlertDialog alert = builder.create();
	                //Hvis kontakt har flere nr, vis alertdialog med alle nummerene
	                if(allNumbers.size() > 1) 
	                {
	                    alert.show();
	                } 
	                else 
	                {
	                	//Hvis det bare er ett nr, legg det direkte inn
	                    String selectedNumber = phoneNumber.toString();
	                    selectedNumber = selectedNumber.replace("-", "");
	                    phoneInput.setText(selectedNumber);
	                }
	                if (phoneNumber.length() == 0) 
	                {  
	                	//Ingen kontakter lagret på tlf
	                    Toast.makeText(getActivity(), getResources().getText(R.string.ingenkontakter), Toast.LENGTH_LONG).show();  
	                }  
	            }  
	            break;  
	        }  
	    }
	
	private boolean gyldigNavn(String navn) 
	{
		String NAVN_PATTERN = "^[\\p{L} .'-]+$";

		Pattern pattern = Pattern.compile(NAVN_PATTERN);
		Matcher matcher = pattern.matcher(navn);
		return matcher.matches();
	}

	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
}
