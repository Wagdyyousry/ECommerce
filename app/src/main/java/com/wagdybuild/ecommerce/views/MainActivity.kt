package com.wagdybuild.ecommerce.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var navController:NavController
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        navController = Navigation.findNavController(this,R.id.fragment)
        NavigationUI.setupActionBarWithNavController(this,navController)
        //sendingBack()
    }

    private fun sendingBack() {
        if(supportActionBar!=null){
            navController = Navigation.findNavController(this,R.id.fragment)
            NavigationUI.setupActionBarWithNavController(this,navController)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController,null)

    }
}