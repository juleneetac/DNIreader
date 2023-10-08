package com.example.myapplication.utils

import android.R
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.nfc.NfcAdapter
import android.nfc.NfcAdapter.ReaderCallback
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.gob.jmulticard.jse.provider.DnieProvider
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.Signature
import java.security.SignatureException

class Common {
    private val EXAMPLE_TEXT =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."

    /**
     *
     * @param activity
     * @return
     */
    fun  EnableReaderMode(activity: Activity?): NfcAdapter? {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        val options = Bundle()
        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 1000)
        nfcAdapter.enableReaderMode(
            activity,
            activity as ReaderCallback?,
            NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
                    NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B,
            options
        )
        return nfcAdapter
    }

    /**
     *
     * @param privateKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     * @throws InvalidKeyException
     */
    @Throws(
        NoSuchAlgorithmException::class,
        SignatureException::class,
        InvalidKeyException::class
    )
    fun getSignature(privateKey: PrivateKey?): ByteArray? {
        val algorithm = "SHA256withRSA"
        val signatureEngine = Signature.getInstance(algorithm, DnieProvider())
        signatureEngine.initSign(privateKey)
        signatureEngine.update(EXAMPLE_TEXT.toByteArray())
        return signatureEngine.sign()
    }

    /**
     *
     * @param title
     * @param message
     */
    fun showDialog(context: Context?, title: String?, message: String?) {
        MaterialAlertDialogBuilder(context!!)
            .setTitle(title)
            .setMessage(message)
            .setIcon(R.drawable.ic_dialog_alert)
            .setPositiveButton(
                "Aceptar"
            ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .show()
    }
}