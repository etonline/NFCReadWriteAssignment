package com.mi5113701.nfcreadwriteassignment;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;

public abstract class NfcActivity extends Activity {

    protected NfcAdapter nfcAdapter;
    protected PendingIntent intent;
    protected IntentFilter[] filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PackageManager packageManager = getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            onNfcFeatureNotFound();
        } else {
            onNfcFeatureFound();
        }
    }

    protected void onNfcFeatureFound() {
        initializeNfc();
        detectInitialNfcState();
    }

    protected void initializeNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        intent = PendingIntent.getActivity(this, 0, new Intent(
                this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        filters = new IntentFilter[] { ndefDetected, tagDetected, techDetected };
    }

    protected void detectInitialNfcState() {
        if (nfcAdapter.isEnabled())
            onNfcStateEnabled();
        else
            onNfcStateDisabled();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, intent, filters, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            onTagDiscovered(intent);
            return;
        }

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            onTagDiscovered(intent);
            return;
        }

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            onTagDiscovered(intent);
            return;
        }
    }

    protected abstract void onNfcFeatureNotFound();

    protected abstract void onNfcStateEnabled();

    protected abstract void onNfcStateDisabled();

    protected abstract void onTagDiscovered(Intent intent);

}