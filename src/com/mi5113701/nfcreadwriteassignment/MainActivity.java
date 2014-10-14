package com.mi5113701.nfcreadwriteassignment;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;


public class MainActivity extends NfcActivity {

	private Button btnReadTag;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get resource
        btnReadTag = (Button)findViewById(R.id.btnReadTag);
        
        //Set button listener
        btnReadTag.setOnClickListener(btnReadTagOnClick);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void alertShow(String msg){
    	AlertDialog.Builder popupBuilder = new AlertDialog.Builder(this);
    	popupBuilder.setPositiveButton("OK", null);
    	popupBuilder.setMessage(msg);
    	popupBuilder.show();
    }
    
    private Button.OnClickListener btnReadTagOnClick = new Button.OnClickListener(){
    	public void onClick(View v){
    		alertShow("test");
    	}
    };
    
	@Override
	protected void onNfcFeatureNotFound() {
		alertShow((String)this.getResources().getText(R.string.nfc_unsupported));
	}

	@Override
	protected void onNfcStateEnabled() {
		alertShow((String)this.getResources().getText(R.string.nfc_ready));
	}

	@Override
	protected void onNfcStateDisabled() {
		alertShow((String)this.getResources().getText(R.string.nfc_disabled));
	}
	
	@Override
	protected void onTagDiscovered(Intent intent) {
		Toast.makeText(this, getString(R.string.hello_nfc), Toast.LENGTH_SHORT).show();
		//Get Tag object
		Tag tag= intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		/*String technologies="";
		for (String txt : tag.getTechList()){
			technologies = technologies + txt + '\n';
		}*/
		//Get an instance of Ndef for the given tag.
		android.nfc.tech.Ndef currentNdef = android.nfc.tech.Ndef.get(tag);
		alertShow("This tag is a " + convertTypeToString(currentNdef.getType()) + "\nLength of this tag:\n" + currentNdef.getMaxSize() + " bytes.");
		alertShow(currentNdef.getCachedNdefMessage().toString());
	}
	
	public String convertTypeToString(String constant) {
		if (constant == "org.nfcforum.ndef.type1")
			return "NFC FORUM TYPE 1";
		else if (constant == "org.nfcforum.ndef.type2")
			return "NFC FORUM TYPE 2";
		else if (constant == "org.nfcforum.ndef.type3")
			return "NFC FORUM TYPE 3";
		else if (constant == "org.nfcforum.ndef.type4")
			return "NFC FORUM TYPE 4";
		else if (constant == "com.nxp.ndef.mifareclassic")
			return "MIFARE CLASSIC";
		return "Unknown";
	}
}
