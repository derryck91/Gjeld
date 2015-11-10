package com.example.s188884_mappe3;

public class Gjeld 
{
	int _id;
	int _sum;
	String _beskrivelse;
	String _opprettet;
	int _person_id;
	String _sted;
	
	public Gjeld()
	{}
	
	public Gjeld(int sum, String beskrivelse, String opprettet, String sted, int person_id)
	{
		this._sum = sum;
		this._beskrivelse = beskrivelse;
		this._opprettet = opprettet;
		this._person_id = person_id;
		this._sted = sted;
	}
	
	//Get og set-metoder for Gjeld
	public int getID()
	{
		return this._id;
	}
	public void setID(int id)
	{
		this._id = id;
	}
	
	public int getSum()
	{
		return this._sum;
	}
	public void setSum(int sum)
	{
		this._sum = sum;
	}
	
	public String getBeskrivelse()
	{
		return this._beskrivelse;
	}
	public void setBeskrivelse(String beskrivelse)
	{
		this._beskrivelse = beskrivelse;
	}
	
	public String getOpprettet()
	{
		return this._opprettet;
	}
	public void setOpprettet(String opprettet)
	{
		this._opprettet = opprettet;
	}
	
	public int getPersonID()
	{
		return this._person_id;
	}
	
	public String getSted()
	{
		return this._sted;
	}
	public void setSted(String sted)
	{
		this._sted = sted;
	}
	
	public void setPersonID(int personid)
	{
		this._person_id = personid;
	}
}
