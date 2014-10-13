package com.mi5113701.nfcreadwriteassignment;

import android.app.AlertDialog;
import android.content.Intent;
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
	}
}
