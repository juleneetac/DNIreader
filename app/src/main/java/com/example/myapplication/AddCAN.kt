package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddCAN : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_can)

        val addCanButton = findViewById<Button>(R.id.can_button)

        addCanButton.setOnClickListener {
            val editCan = findViewById<EditText>(R.id.can_edit)
            //editCan.setText()
            val intent = Intent(this, ReadDataDNI::class.java)
            intent.putExtra("CAN", editCan.text.toString())
            startActivity(intent)
        }
    }
}