package com.example.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    //navController помечен как lateinit, поскольку он будет установлен в onCreate.
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //получим ссылку на nav_host_fragment(из activity_main.xml)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        //назначим наш navHostFragment собственностью navController
        navController = navHostFragment.navController

        //Это гарантирует, что иконки action bar, такие как пункты меню будут видны.
        setupActionBarWithNavController(navController)
    }
}