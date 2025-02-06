package com.example.promanager.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.promanager.databinding.ActivityIntroScreenBinding

class Intro_Activity : AppCompatActivity() {
    private lateinit var binding:ActivityIntroScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityIntroScreenBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding.introSignupBtn.setOnClickListener {
            startActivity(Intent(this, Sign_up_activity::class.java))
        }

        binding.introSigninBtn.setOnClickListener{
            startActivity(Intent(this, Sign_in_Activity::class.java))
        }

    }
}

