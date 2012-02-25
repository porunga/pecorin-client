package com.porunga.pecorin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

public class DirectPecoriActivity extends Activity {
  private NfcAdapter mNfcAdapter;
  private NdefMessage mNdefMessage;
  private PendingIntent mNfcPendingIntent;
  private IntentFilter[] mNdefExchangeFilters;
  private IntentFilter[] mWriteTagFilters;
  private boolean mResumed;
  private TextView mMessage;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.direct_pecori);
    mMessage = (TextView)findViewById(R.id.pecori_message);
    
    mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
    try {
      ndefDetected.addDataType("text/plain");
    } catch (MalformedMimeTypeException e) {
      e.printStackTrace();
    }
    mNdefExchangeFilters = new IntentFilter[]{ndefDetected};
    IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
    mWriteTagFilters = new IntentFilter[] { tagDetected };
  }
  
  @Override
  public void onResume() {
    super.onResume();
    mResumed = true;
    Intent intent = getIntent();
    String action = intent.getAction();
    if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
      NdefMessage[] messages = getNdefMessages(intent); 
      mMessage.setText(new String(messages[0].getRecords()[0].getPayload()));
      setIntent(new Intent());
    }
    mNfcAdapter.enableForegroundNdefPush(this,getDefaultNdef());
    mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
  }

  @Override
  public void onPause() {
    super.onPause();
    mNfcAdapter.disableForegroundNdefPush(this);
    mNfcAdapter.disableForegroundDispatch(this);
  }

  @Override
  public void onNewIntent(final Intent intent) {
    String action = intent.getAction(); 
    if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
      new AlertDialog.Builder(this).setTitle("Let's Pecori!!").setPositiveButton("Yes", 
          new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
              NdefMessage[] ndefMessage = getNdefMessages(intent);
              mMessage.setText(new String(ndefMessage[0].getRecords()[0].getPayload()));
          }
        })
        .setNegativeButton("No", 
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                
              }
        }).show();
    }
  }
  
  NdefMessage[] getNdefMessages(Intent intent) {
    // Parse the intent
    NdefMessage[] msgs = null;
    String action = intent.getAction();
    if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs != null) {
            msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
            }
        } else {
            // Unknown tag type
            byte[] empty = new byte[] {};
            NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
            NdefMessage msg = new NdefMessage(new NdefRecord[] {
                record
            });
            msgs = new NdefMessage[] {
                msg
            };
        }
    } else {
        Log.d("NFCBeamSpike", "Unknown intent.");
        finish();
    }
    return msgs;
  }

  private NdefMessage getDefaultNdef() {
    byte[] mimeBytes = "text/plain".getBytes();
    String messageText = "ぺこり!!";
    NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes,
            new byte[] {}, messageText.getBytes());
    return new NdefMessage(new NdefRecord[] {
        textRecord
    });
  }
  
}
