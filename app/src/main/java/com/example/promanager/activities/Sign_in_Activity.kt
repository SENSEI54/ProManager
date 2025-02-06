package com.example.promanager.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.promanager.R
import com.example.promanager.databinding.ActivitySignInBinding
import com.example.promanager.firebase.fireStore
import com.example.promanager.module.User
import com.google.firebase.auth.FirebaseAuth

class Sign_in_Activity : BaseActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth= FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setActionBar()
        binding.signinSignBtn.setOnClickListener { SignUser() }
    }

    private fun setActionBar() {

        setSupportActionBar(binding.toolbarSignInActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_back_24dp)
        }

        binding.toolbarSignInActivity.setNavigationOnClickListener{onBackPressed()}
    }

    fun SigninSuccess(user:User){
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
        Log.i("data fetch", "data fetched and transfered")
    }

    fun SignUser()
    {
        val email=binding.etSigninEmail.text.toString().trim{it<=' '}
        val password=binding.etSigninPassword.text.toString().trim{it<=' '}

        if(validateUser( email, password))
        {
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){
                task->
                if(task.isSuccessful)
                {
                    Log.i("Signin","Signing with email is successful")
                    val user=auth.currentUser!!
                    fireStore().loadUserData(this)
                }
                else{
                    Log.w("Sign in","Signing Failure"+task.exception)
                    Toast.makeText(this,"Authentication Failed",Toast.LENGTH_LONG).show()
                    hideProgressDialog()
                }
            }
        }
    }

    private fun validateUser(email:String, password:String):Boolean{
        return when{
            TextUtils.isEmpty(email)-> {
                showErrorSnackBar("Please Enter Your Email")
                false
            }
            TextUtils.isEmpty(password)-> {
                showErrorSnackBar("PLease Enter Your Password")
                false
            }
            else->{true}
        }
    }
}