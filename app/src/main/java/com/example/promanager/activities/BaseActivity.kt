package com.example.promanager.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.promanager.R
import com.example.promanager.databinding.DialogProgressBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

open class BaseActivity : AppCompatActivity() {

    private var doubleBacktoExitPressedOnce=false

    private lateinit var mProgressDialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressDialog(text:String){
        mProgressDialog= Dialog(this)

        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.findViewById<TextView>(R.id.progress_textView).text=text
        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun getCurrentUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doublePressBack(){
        if(doubleBacktoExitPressedOnce)
        {
            super.onBackPressed()
            return
        }

        doubleBacktoExitPressedOnce=true
        Toast.makeText(this,resources.getString(R.string.please_click_back_again_to_exit),Toast.LENGTH_SHORT).show()

        Handler().postDelayed({doubleBacktoExitPressedOnce=false}, 2000)

    }

    fun showErrorSnackBar(message:String){
        val snackBar=Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        val snackBarView=snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
        snackBar.show()
    }
}