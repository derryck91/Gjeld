package com.example.s188884_mappe3;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class EditFragment extends Fragment
{
	final int PICK_CONTACT = 1;
	DBAdapter db;
	ImageButton endre;
	View inflatedView = null;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		this.inflatedView = inflater.inflate(R.layout.edit_fragment, container, false);
		
		EditText firstname = (EditText) inflatedView.findViewById(R.id.endreFornavn);
	    EditText lastname = (EditText) inflatedView.findViewById(R.id.endreEtternavn);
	    EditText number = (EditText) inflatedView.findViewById(R.id.endreNummer);
	    endre = (ImageButton)inflatedView.findViewById(R.id.endre);
	    firstname.setText(getArguments().getString("fornavn"));
	    lastname.setText(getArguments().getString("etternavn"));
	    number.setText(getArguments().getString("nummer"));
		
	    ImageButton hentkontakt = (ImageButton) inflatedView.findViewById(R.id.addnumber);
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
		endre.setOnClickListener(new OnClickListener()
        {
			EditText firstname = (EditText) inflatedView.findViewById(R.id.endreFornavn);
		    EditText lastname = (EditText) inflatedView.findViewById(R.id.endreEtternavn);
		    EditText number = (EditText) inflatedView.findViewById(R.id.endreNummer);
			public void onClick(View v) 
			{
				String fnavn = firstname.getText().toString();
				String enavn = lastname.getText().toString();
				String nummer = number.getText().toString();
				if(gyldigNavn(fnavn) && gyldigNavn(enavn))
				{
					try
					{
						db.oppdater(getArguments().getInt("id"), fnavn, enavn, nummer);
						getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
						Toast.makeText(getActivity(), fnavn + " " + enavn + " " + getResources().getText(R.string.oppdatert), Toast.LENGTH_SHORT).show();
					}
					catch(SQLiteException e)
					{
						Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
						
					}
				}
				else
				{
					AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(),R.style.dialogdanger));
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
					final EditText phoneInput = (EditText)inflatedView.findViewById(R.id.endreNummer);
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
	                //Hvis kontakt har flere nr, vis alertdialog med alle nummerene
	                AlertDialog alert = builder.create();
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
}
