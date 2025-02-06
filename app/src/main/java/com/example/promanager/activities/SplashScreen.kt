package com.example.promanager.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

import android.view.WindowManager
import com.example.promanager.databinding.ActivitySpalshScreenBinding
import com.example.promanager.firebase.fireStore


class splashScreen : AppCompatActivity() {
    private lateinit var binding:ActivitySpalshScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySpalshScreenBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val typeface:Typeface= Typeface.createFromAsset(assets,"Louis George Cafe.ttf")
        val typeface2:Typeface=Typeface.createFromAsset(assets,"Louis George Cafe Bold.ttf")
        binding.splashTvTitle.typeface = typeface2
        binding.splashTvIntro.typeface = typeface


        Handler().postDelayed({
            val currentUserId=fireStore().getCurrentUserUid()
            if(currentUserId.isEmpty())
            {
                startActivity(Intent(this, Intro_Activity::class.java))
            }else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        },2500)
    }
}