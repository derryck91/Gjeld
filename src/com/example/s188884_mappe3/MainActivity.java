package com.example.s188884_mappe3;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity
{
	DBAdapter db;
	FragmentManager fm = getFragmentManager();
	FragmentTransaction transaction = fm.beginTransaction();
	public Menu mainMenu;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);

        MyListFragment list = new MyListFragment();
		transaction.add(R.id.startup_layout, list, "ListFragment");
		transaction.commit();
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
    	getMenuInflater().inflate(R.menu.main, menu);
    	mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	FragmentManager fm = getFragmentManager();
		FragmentTransaction trans = fm.beginTransaction();
    	switch (item.getItemId())
    	{
    	case R.id.exit:
    		finish();
	        break;
    	case R.id.add:
    		AddFragment add = new AddFragment();
    		trans.replace(R.id.startup_layout, add, "AddFragment");
    		fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    		mainMenu.findItem(R.id.add).setIcon(R.drawable.addpersonpressed);
    		mainMenu.findItem(R.id.plusdebt).setIcon(R.drawable.plusdebt1);
    		mainMenu.findItem(R.id.list).setIcon(R.drawable.list);
    		trans.commit();
    		break;
    	case R.id.list:
    		MyListFragment list = new MyListFragment();
    		trans.replace(R.id.startup_layout, list, "ListFragment");
    		fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    		mainMenu.findItem(R.id.add).setIcon(R.drawable.plus1);
    		mainMenu.findItem(R.id.plusdebt).setIcon(R.drawable.plusdebt1);
    		mainMenu.findItem(R.id.list).setIcon(R.drawable.listpressed);
    		trans.commit();
    		break;
    	case R.id.plusdebt:
    		AddDebtFragment addDebt = new AddDebtFragment();
    		trans.replace(R.id.startup_layout, addDebt, "AddDebtFragment");
    		//ENDRER ICON item.setIcon(R.drawable.icon_highlightedlist);
    		fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    		mainMenu.findItem(R.id.add).setIcon(R.drawable.plus1);
    		mainMenu.findItem(R.id.plusdebt).setIcon(R.drawable.addmoneypressed);
    		mainMenu.findItem(R.id.list).setIcon(R.drawable.list);
    		trans.commit();
    		break;
    	case android.R.id.home:
    		MyListFragment thelist = new MyListFragment();
    		trans.replace(R.id.startup_layout, thelist, "AddDebtFragment");
    		fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    		mainMenu.findItem(R.id.add).setIcon(R.drawable.plus1);
    		mainMenu.findItem(R.id.plusdebt).setIcon(R.drawable.plusdebt1);
    		mainMenu.findItem(R.id.list).setIcon(R.drawable.listpressed);
    		trans.commit();
    		break;
    	}
        return super.onOptionsItemSelected(item);
    }
}
