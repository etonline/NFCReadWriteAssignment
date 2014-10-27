package com.mi5113701.nfcreadwriteassignment;

import java.io.IOException;

import org.ndeftools.Message;
import org.ndeftools.MimeRecord;
import org.ndeftools.Record;
import org.ndeftools.externaltype.ExternalTypeRecord;
import org.ndeftools.wellknown.TextRecord;
import org.ndeftools.wellknown.UriRecord;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.NdefRecord;
import android.nfc.NdefMessage;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;


public class MainActivity extends NfcActivity {

	private Button btnReadTag;
	private TextView txtStatus;
	private TextView txtBox;
	private EditText telInput;
	private boolean writePending = false;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get resource
        btnReadTag = (Button)findViewById(R.id.btnReadTag);
        txtStatus = (TextView)findViewById(R.id.textView1);
        txtBox = (TextView)findViewById(R.id.textView2);
        telInput = (EditText)findViewById(R.id.editText1);

        //Set button listener
        btnReadTag.setOnClickListener(btnWriteTagOnClick);
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
    
    private Button.OnClickListener btnWriteTagOnClick = new Button.OnClickListener(){
    	public void onClick(View v){
    		alertShow("Approach NFC for writing");
    		//Dirty lazy work
    		writePending = true;
    		txtStatus.setText("Current mode: Writing, please approach a NFC card.");
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
		if(writePending){
			//Uri for tel
			NdefRecord customRtdUriRecord = NdefRecord.createUri("tel:"+telInput.getText());
			NdefMessage ndefMsgPending = new NdefMessage(customRtdUriRecord);
			Ndef currentNdef = Ndef.get(tag);
			if (currentNdef.isWritable()
					&& currentNdef.getMaxSize() > ndefMsgPending.toByteArray().length) {
					try {
						currentNdef.connect();
						currentNdef.writeNdefMessage(ndefMsgPending);
						currentNdef.close();
					} catch (IOException e) {
						alertShow("IO Exception, do not move the tag during writing.");
					} catch (FormatException e) {
						alertShow("Format Exception.");
					}
				}
			writePending = false;
			txtStatus.setText("Current mode: Reading, please approach a NFC card.");
		}
		else {
			txtBox.setText("Tag information:\n");
			//Get an instance of Ndef for the given tag.
			android.nfc.tech.Ndef currentNdef = android.nfc.tech.Ndef.get(tag);
			txtBox.append("This tag is a " + convertTypeToString(currentNdef.getType()) + "\nLength of this tag: " + currentNdef.getMaxSize() + " bytes.");
			NdefMessage ndefMessage = currentNdef.getCachedNdefMessage();
			NdefRecord [] ndefRecord = ndefMessage.getRecords();
			txtBox.append("\n" + "TNF: " + convertTNFToString(ndefRecord[0].getTnf()));
			//txtBox.append("\n" + "Type: " + ndefRecord[0].getType());
			//txtBox.append("\n" + "paylord:" + ndefRecord[0].getPayload());
			try {
				Message highLevelNdef = new Message(ndefMessage);
				 for(int k = 0; k < highLevelNdef.size(); k++) {
                     Record record = highLevelNdef.get(k);
                     // your own code here, for example:
                     if(record instanceof MimeRecord) {
                    	 txtBox.append("\nIt is a MIME Record");
                     } else if(record instanceof ExternalTypeRecord) {
                    	 txtBox.append("\nIt is a External Type Record");
                     } else if(record instanceof TextRecord) {
                    	 txtBox.append("\nIt is a Text Record");
                     } else if(record instanceof UriRecord) {
                    	 txtBox.append("\nTo Uri: " + record.getNdefRecord().toUri().toString());
                    	 
                    	 //It'f it's tel: uri, call
                    	 if(record.getNdefRecord().toUri().toString().contains("tel:")){
	                    	 Intent callIntent1 = new Intent(Intent.ACTION_CALL);
	                    	 callIntent1.setData(record.getNdefRecord().toUri());
	                    	 startActivity(callIntent1);
                    	 }
                     } else { // more else
                             // ..
                     }
             }
			} catch (FormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	
	public String convertTNFToString(short constant) {
		if (constant == android.nfc.NdefRecord.TNF_WELL_KNOWN)
			return "TNF WELL Known";
		else if (constant == android.nfc.NdefRecord.TNF_UNKNOWN)
			return "TNF Unknown";
		else if (constant == android.nfc.NdefRecord.TNF_UNCHANGED)
			return "TNF Unchanged";
		else if (constant == android.nfc.NdefRecord.TNF_MIME_MEDIA)
			return "TNF MIME Media";
		else if (constant == android.nfc.NdefRecord.TNF_EXTERNAL_TYPE)
			return "TNF External Type";
		else if (constant == android.nfc.NdefRecord.TNF_EMPTY)
			return "TNF Empty";
		else if (constant == android.nfc.NdefRecord.TNF_ABSOLUTE_URI)
			return "TNF Absolute URI";
		return "TNF Unknown";
	}
}
