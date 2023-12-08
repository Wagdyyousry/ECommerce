package com.wagdybuild.ecommerce.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var  binding:ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //custom status bar
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        supportActionBar!!.hide()

        val anim = AnimationUtils.loadAnimation(this,R.anim.left_to_right)
        binding.ivDeliveryBoy.animation = anim

        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }, 4000)

    }
}