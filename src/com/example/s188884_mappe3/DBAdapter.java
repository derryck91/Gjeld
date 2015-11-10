package com.example.s188884_mappe3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBAdapter
{
	Context context;
	static final String TAG = "DbHelper";
	static final String DB_NAVN = "gjeld.db";
	
	static final String PERSONTABELL = "Personer";
	static final String ID = BaseColumns._ID;
	static final String FORNAVN = "fornavn";
	static final String ETTERNAVN = "etternavn";
	static final String NUMMER = "nummer";
	
	static final String GJELDTABELL = "Gjeld";
	static final String SUM = "sum";
	static final String BESKRIVELSE = "beskrivelse";
	static final String OPPRETTET = "tid_opprettet";
	static final String PERSON_ID = "person_id";
	static final String STED = "sted";

	static final int DB_VERSJON = 13;
	
	String[] cols = {FORNAVN,ETTERNAVN,NUMMER};
	
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	public static ArrayList<String> DBArray = new ArrayList<String>();
	
	public DBAdapter(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	public static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DB_NAVN, null, DB_VERSJON);
		}

		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			String sqlPersoner="create table " + PERSONTABELL + " ("
					+ ID + " integer primary key autoincrement, "
					+ FORNAVN + " text, "
					+ ETTERNAVN + " text, "
					+ NUMMER + " text);";
			Log.d(TAG,"oncreated sql" + sqlPersoner);
			db.execSQL(sqlPersoner);

			
			String sqlGjeld="create table " + GJELDTABELL + " ("
					+ ID + " integer primary key autoincrement, "
					+ SUM + " integer, "
					+ BESKRIVELSE + " text, "
					+ OPPRETTET + " datetime, "
					+ STED + " text, "
					+ PERSON_ID + " integer, "
					+ "FOREIGN KEY(" + PERSON_ID + ") REFERENCES " + PERSONTABELL + "(" + ID + "));";
			Log.d(TAG,"oncreated sql" + sqlGjeld);
			db.execSQL(sqlGjeld);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			db.execSQL("drop table if exists " + PERSONTABELL);
			db.execSQL("drop table if exists " + GJELDTABELL);
			Log.d(TAG,"updated");
			onCreate(db);
		}
	}//slutt DatabaseHelper
	
	public DBAdapter open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
		
	}
	//Metode for å legge inn i Person-objektet og videre i database
	public void settInnPerson(Person person)
	{
		db = DBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FORNAVN, person.getFornavn());
		values.put(ETTERNAVN, person.getEtternavn());
		values.put(NUMMER, person.getNummer());
		
		db.insert(PERSONTABELL, null, values);
		db.close();
	}
	
	//Metode for å slette en person med gitt ID fra databasen
	public void slettEnPerson(int id)
	{
		db = DBHelper.getReadableDatabase();
		String whereClause = "_ID = " + id;
		db.delete(PERSONTABELL, whereClause, null);
		db.close();
	}
	//Metode for å slette alle fra databasen
	public void slettDBinnholdPerson()
	{
		db = DBHelper.getWritableDatabase();
		String sql= "DELETE FROM ";
		db.execSQL(sql + PERSONTABELL);
	}
	//Metode for å oppdatere person i databasen etter redigering
	public void oppdater(int id, String fname, String ename, String number)
	{
		db = DBHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		
		cv.put(FORNAVN, fname);
		cv.put(ETTERNAVN, ename);
		cv.put(NUMMER, number);
		
		String whereClause = "_ID = " + id;
		db.update(PERSONTABELL, cv, whereClause, null);
		db.close();
	}
	//Metode for å vise alle personer i listview
	public List<Person> visAllePersoner()
	{
		List<Person> personListe = new ArrayList<Person>();
		String sql = "SELECT * FROM " + PERSONTABELL;
		db = DBHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		
		if(cursor.moveToFirst())
		{
			do
			{
				Person person = new Person();
				person.setID(Integer.parseInt(cursor.getString(0)));
				person.setFornavn(cursor.getString(1));
				person.setEtternavn(cursor.getString(2));
				person.setNummer(cursor.getString(3));

				personListe.add(person);
				Collections.sort(personListe, new PersonNameComparator());
			}
			while(cursor.moveToNext());
		}
		db.close();
		return personListe;
	}
	

	
	//Metode for å finne en person
	public Cursor finnEnPerson(Person person)
	{
		Cursor cur;
		cur = db.query(PERSONTABELL, cols, ID + "='" + person.getID() + "'", null, null, null, null);
		return cur;
	}
	//Metode for å hente ut antall personer i databasen
	public int antallDBPerson() 
	{
		db = DBHelper.getReadableDatabase();
        String tellerQuery = "SELECT * FROM " + PERSONTABELL;
        
        Cursor cursor = db.rawQuery(tellerQuery, null);

        int count = cursor.getCount();
        db.close();
        return count;
    }
	
	//Sortere navn på fornavn
	public class PersonNameComparator implements Comparator<Person> 
	{
	    public int compare(Person p1, Person p2) 
	    {
	        return p1.getFornavn().compareTo(p2.getFornavn());
	    }
	}
	
	//Sortere gjeld på dato
	public class GjeldDatoComparator implements Comparator<Gjeld> 
	{
	    public int compare(Gjeld g1, Gjeld g2) 
	    {
	        return g1.getOpprettet().compareTo(g2.getOpprettet());
	    }
	}
	
	
	//Gjeld-metoder
	public void settInnGjeld(Gjeld gjeld)
	{
		db = DBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SUM, gjeld.getSum());
		values.put(BESKRIVELSE, gjeld.getBeskrivelse());
		values.put(OPPRETTET, gjeld.getOpprettet());
		values.put(STED, gjeld.getSted());
		values.put(PERSON_ID, gjeld.getPersonID());
		
		db.insert(GJELDTABELL, null, values);
		db.close();
	}
	
	public List<Gjeld> visAllGjeld()
	{
		List<Gjeld> gjeldListe = new ArrayList<Gjeld>();
		String sql = "SELECT * FROM " + GJELDTABELL;
		db = DBHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		
		if(cursor.moveToFirst())
		{
			do
			{
				Gjeld gjeld = new Gjeld();
				gjeld.setID(Integer.parseInt(cursor.getString(0)));
				gjeld.setSum(Integer.parseInt(cursor.getString(1)));
				gjeld.setBeskrivelse(cursor.getString(2));
				gjeld.setOpprettet(cursor.getString(3));
				gjeld.setSted(cursor.getString(4));
				gjeld.setPersonID(Integer.parseInt(cursor.getString(5)));

				gjeldListe.add(gjeld);
				Collections.sort(gjeldListe, new GjeldDatoComparator());
			}
			while(cursor.moveToNext());
		}
		db.close();
		return gjeldListe;
	}
	
	public List<Gjeld> finnGjeldPerson(int id)
	{
		List<Gjeld> gjeldListe = new ArrayList<Gjeld>();
		String sql = "SELECT * FROM " + GJELDTABELL + " WHERE " + PERSON_ID + " = " + id + ";";
		db = DBHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		
		if(cursor.moveToFirst())
		{
			do
			{
				Gjeld gjeld = new Gjeld();
				gjeld.setID(Integer.parseInt(cursor.getString(0)));
				gjeld.setSum(Integer.parseInt(cursor.getString(1)));
				gjeld.setBeskrivelse(cursor.getString(2));
				gjeld.setOpprettet(cursor.getString(3));
				gjeld.setSted(cursor.getString(4));
				gjeld.setPersonID(Integer.parseInt(cursor.getString(5)));

				gjeldListe.add(gjeld);
				Collections.sort(gjeldListe, new GjeldDatoComparator());
			}
			while(cursor.moveToNext());
		}
		db.close();
		return gjeldListe;
	}
	
	public int finnSumGjeldPerson(int id)
	{
		db = DBHelper.getReadableDatabase();
		String regneSql = "SELECT SUM(sum) AS TOTALSUM FROM " + GJELDTABELL + " WHERE " + PERSON_ID + " = " + id + ";";
		
		Cursor cursor = db.rawQuery(regneSql, null);
        
        if(cursor.moveToFirst())
        {
        	 return cursor.getInt(0);
        }
        db.close();
		return cursor.getInt(0);
	}
	
	public void slettEnGjeld(int id)
	{
		db = DBHelper.getReadableDatabase();
		String whereClause = "_ID = " + id;
		db.delete(GJELDTABELL, whereClause, null);
		db.close();
	}
	//Metode for å slette alle fra databasen
	public void slettGjeldPerson(int id)
	{
		db = DBHelper.getWritableDatabase();
		String whereClause = PERSON_ID + " = " + id;
		db.delete(GJELDTABELL, whereClause, null);
		db.close();
	}
	
	public int sumGjeld()
	{
		db = DBHelper.getReadableDatabase();
        String regneQuery = "SELECT SUM(sum) AS TOTALSUM FROM " + GJELDTABELL;
        
        Cursor cursor = db.rawQuery(regneQuery, null);
        
        if(cursor.moveToFirst())
        {
        	 return cursor.getInt(0);
        }
        db.close();
		return cursor.getInt(0);
	}
	public int antallGjeld() 
	{
		db = DBHelper.getReadableDatabase();
        String tellerQuery = "SELECT * FROM " + GJELDTABELL;
        
        Cursor cursor = db.rawQuery(tellerQuery, null);

        int count = cursor.getCount();
        db.close();
        return count;
    }
}
