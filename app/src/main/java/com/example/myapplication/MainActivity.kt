package com.example.myapplication

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var nfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val readNfcButton = findViewById<Button>(R.id.readNfcButton)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (!isNfcEnabled(this)) {
            readNfcButton.setBackgroundColor(
                ContextCompat.getColor(this, R.color.disabledButtonColor)
            )
            readNfcButton.setOnClickListener {
                Log.d("MyTag", "disabled log")
                Toast.makeText(this, "NFC is not activated on this device.", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        else {
            readNfcButton.setOnClickListener {
                val intent = Intent(this, AddCAN::class.java)
                startActivity(intent)
            }
        }
    }

    private fun isNfcEnabled(context: Context): Boolean {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        return nfcAdapter != null && nfcAdapter.isEnabled
    }

}