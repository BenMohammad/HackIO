package com.benmohammad.hackio.tasks

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.benmohammad.hackio.R
import com.benmohammad.hackio.stats.StatisticsActivity
import com.benmohammad.hackio.util.addFragmentToActivity
import com.google.android.material.navigation.NavigationView

class TasksActivity: AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_act)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.run{
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.setStatusBarBackground(R.color.colorPrimary)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        if(navigationView != null) {
            setupDrawerContent(navigationView)
        }

        if(supportFragmentManager.findFragmentById(R.id.contentFrame) == null) {
            addFragmentToActivity(supportFragmentManager, TasksFragment(), R.id.contentFrame)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener {
            menuItem -> when(menuItem.itemId) {
            R.id.list_navigation_menu_item -> {

            }

            R.id.statistics_navigation_menu_item -> {
                val inetnt = Intent(this@TasksActivity, StatisticsActivity::class.java)
                startActivity(intent)

        }else -> {
            }
        }
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }


    }

}