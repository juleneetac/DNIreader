package com.example.myapplication

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi

class ReadData : AppCompatActivity() {
    private lateinit var rawdataTextView: TextView
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var nfcPendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_data)

        rawdataTextView = findViewById(R.id.rawdataTextView)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }
    override fun onResume() {
        super.onResume()
        enableForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        disableForegroundDispatch()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("MyTag", "1.1 DATA")
        handleNfcIntent(intent)
    }

    private fun enableForegroundDispatch() {
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING)
        nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, null, null)
    }

    private fun disableForegroundDispatch() {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter?.disableForegroundDispatch(this)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun handleNfcIntent(intent: Intent) {
        val action = intent.action
        Log.d("MyTag", "1 DATA")
        Log.d("MyTag", "My action:$action")
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            Log.d("MyTag", "2 DATA")
            val tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                val ndefMessage = ndef.cachedNdefMessage
                if (ndefMessage != null) {
                    val records = ndefMessage.records
                    Log.d("MyTag", "3 DATA")
                    if (records.isNotEmpty()) {
                        val record = records[0]
                        val payload = record.payload
                        // Assuming the payload is in UTF-8 format
                        val data = String(payload, 3, payload.size - 3, Charsets.UTF_8)
                        Log.d("MyTag", "4 DATA")
                        // Parse the data and update TextViews
                        updateTextViews(data)
                    } else {
                        Toast.makeText(this, "No data found on NFC tag.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateTextViews(data: String) {
        // Assuming the data is in the format: "Name:John, Age:30"
        rawdataTextView.text = "$data"
    }
}