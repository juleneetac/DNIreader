package com.example.myapplication

import android.graphics.Color
import android.graphics.Bitmap
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.utils.Common
import com.example.myapplication.utils.DecodeJp2Coroutine
import com.example.myapplication.utils.graphics.CanvasView
import es.gob.fnmt.dniedroid.gui.PasswordUI
import es.gob.fnmt.dniedroid.help.Loader
import java.text.SimpleDateFormat
import com.gemalto.jp2.JP2Decoder

class ReadDataDNI : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var infoTextView: TextView
    private lateinit var birthDNITextView: TextView
    private lateinit var nameDNITextView: TextView
    private lateinit var progressBarDNIload: ProgressBar
    private lateinit var loadingTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var professionTextView: TextView
    private lateinit var otherinfoTextView: TextView

    private lateinit var dniImageView: ImageView
    //private var photoCanvasView: CanvasView? = null
    private var _can: String? = null
    val common = Common()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_data_dni)

        infoTextView = findViewById(R.id.infotextView)
        birthDNITextView = findViewById(R.id.birthtextView)
        nameDNITextView = findViewById(R.id.nametextView)
        progressBarDNIload = findViewById(R.id.progressBarReadingDNI)
        loadingTextView = findViewById(R.id.readingtextView)
        dniImageView = findViewById(R.id.dniimageView)
        phoneTextView = findViewById(R.id.phonetextView)
        professionTextView = findViewById(R.id.professiontextView)
        otherinfoTextView = findViewById(R.id.otherinfotextView)

        _can = intent.getStringExtra("CAN")

        common.EnableReaderMode(this)

        PasswordUI.setAppContext(this)
        PasswordUI.setPasswordDialog(null) //Diálogo de petición de contraseña por defecto


        infoTextView.setText("Aproxime el DNIe al dispositivo")
    }

    private fun updateInfo(string: String) {
        runOnUiThread {
            if (string.contains("ERROR")){
                infoTextView.setTextColor(Color.RED)
            }
            else{
                infoTextView.setTextColor(Color.GREEN)
            }
            // Assuming the data is in the format: "Name:John, Age:30"
            infoTextView.text = string
            progressBarDNIload.visibility = View.GONE
            loadingTextView.visibility = View.GONE
        }
    }

    private fun loadedData(date: String, name: String, photodniarray: ByteArray, phone: String, profession: String, otherinfo:String) {
        runOnUiThread {
            // Assuming the data is in the format: "Name:John, Age:30"
            birthDNITextView.text = date
            nameDNITextView.text = name
            dniImageView.visibility = View.VISIBLE
            val bitmap = DecodeJp2Coroutine(dniImageView, photodniarray).doInBackground()
            dniImageView.setImageBitmap(bitmap)
            phoneTextView.text = phone
            professionTextView.text = profession
            otherinfoTextView.text = otherinfo
            progressBarDNIload.visibility = View.GONE
            loadingTextView.visibility = View.GONE
        }
    }

    private fun progresBarVisible() {
        runOnUiThread {
            val colordefault = ContextCompat.getColor(baseContext, com.google.android.material.R.color.m3_default_color_secondary_text)
            infoTextView.setTextColor(colordefault)
            infoTextView.text = "Porfavor, no separe el DNIe del dispositivo"
            birthDNITextView.text = "Fecha de nacimiento:"
            nameDNITextView.text = "Nombre"
            phoneTextView.text = "Phone:"
            professionTextView.text = "Porfession"
            otherinfoTextView.text = "Other Info:"
            dniImageView.visibility = View.GONE
            progressBarDNIload.visibility = View.VISIBLE
            loadingTextView.visibility = View.VISIBLE
        }
    }

    /**
     * Evento de detección de dispositivo NFC (NfcAdapter.ReaderCallback)
     * @param tag
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onTagDiscovered(tag: Tag?) {

        try {
            progresBarVisible()
            val mrtdCardInfo = Loader.init(arrayOf(_can), tag).mrtdCardInfo
            //obtain all type of data
            val data1 = mrtdCardInfo.dataGroup1
            val data2 = mrtdCardInfo.dataGroup2
            val data7 = mrtdCardInfo.dataGroup7
            val data11 = mrtdCardInfo.dataGroup11
            val data13 = mrtdCardInfo.dataGroup13
            Log.d("MyTagDNI", "DATA1: $data1")
            Log.d("MyTagDNI", "DATA2: $data2")
            Log.d("MyTagDNI", "DATA7: $data7")
            Log.d("MyTagDNI", "DATA11: $data11")
            Log.d("MyTagDNI", "DATA13: $data13")
            //value of data
            val date = SimpleDateFormat("yyMMdd").parse(data1.dateOfBirth)
            val name = data1.name
            val image = data2.imageBytes
            val phone = data11.phone
            val profession = data11.profession
            val otherinfo = data11.otherInfo
            val ot = data13.expirationDate
            Log.d("MyTagDNI", "Phone: $phone")
            Log.d("MyTagDNI", "Profession: $profession")
            Log.d("MyTagDNI", "OTHER info: $otherinfo")
            updateInfo("¡ Lectura correcta ! =)")
            loadedData("Fecha de nacimiento: " + SimpleDateFormat("dd-MMMM-yyyy").format(date),
                "Nombre: $name",
                image,
                "Phone: $phone",
                "Profession: $profession",
                "Other Info: $ot")
        } catch (e: Exception) {
            Log.d("MyTagDNI", "ERROR 1 DATA")
            if (e.message != null && e.message!!.contains("CAN incorrecto")) {
                updateInfo(
                    "ERROR: CAN incorrecto, Verifique el número de 6 dígitos que aparece en el border inferior derecho del DNIe.")
            } else {
                Log.d("MyTagDNI", "ERROR 2 DATA")
                updateInfo("ERROR: " + e.message)
                Log.d("MyTagDNI", "ERROR: " + e.message)
            }
        }
    }
}