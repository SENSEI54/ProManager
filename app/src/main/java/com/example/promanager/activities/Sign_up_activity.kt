package com.example.promanager.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.promanager.R
import com.example.promanager.databinding.ActivitySignUpBinding
import com.example.promanager.firebase.fireStore
import com.example.promanager.module.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Sign_up_activity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setActionBar()
        binding.signupSignBtn.setOnClickListener { registeruser() }
    }

    private fun setActionBar(){

        setSupportActionBar(binding.toolbarSignUpActivity)

        val actionBar =supportActionBar
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_back_24dp)
        }
        binding.toolbarSignUpActivity.setNavigationOnClickListener { onBackPressed() }

    }
    fun RegisteredUserSuccessfull(name:String,Email:String){
        Toast.makeText(this,"$name you have successfully registered with this $Email address",Toast.LENGTH_LONG).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    fun registeruser()
    {
        val name=binding.etSignupName.text.toString().trim{it<=' '}
        val email=binding.etSignupEmail.text.toString().trim{it<=' '}
        val password=binding.etSignupPassword.text.toString().trim{it<=' '}

        if(validateDetails(name, email, password))
        {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                task->
                if(task.isSuccessful)
                {
                    val firebaseUser:FirebaseUser=task.result!!.user!!
                    val registeredUser=firebaseUser.email!!
                    val user=User(firebaseUser.uid,name,email)
                    fireStore().registerUser(this,user)
                }
                else{
                    Log.i("signUp","Registration Fialure"+task.exception)
                    Toast.makeText(this,"Registration Failed",Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    private fun validateDetails(name:String,email:String,password:String):Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please Enter Your Name")
                false
            }
            TextUtils.isEmpty(email)-> {
                showErrorSnackBar("Please Enter Your Email")
                false
            }
            TextUtils.isEmpty(password)-> {
                showErrorSnackBar("PLease Enter Your Password")
                false
            }
            else->{
                true
            }
        }
    }
}