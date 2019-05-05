package com.registro.registromars

import android.content.Context
import android.content.Intent
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import android.webkit.WebView
import kotlinx.android.synthetic.main.content_main.webView

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var usersDB: UsersDBHelper
    val url = "file:///android_asset/Home.html"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true
        webView.setWebViewClient(WebViewClient())
        webView.addJavascriptInterface(WebAppInterface( this), "Android")
        webView.loadUrl(this.url)

        usersDB = UsersDBHelper(this)
        SQLite.sqli = UsersDBHelper(this)
        var category: Int = SQLite.sqli.getCategory()
        var text = category.toString()
        val duration = Toast.LENGTH_LONG

        var toast = Toast.makeText(this, text, duration)
        toast.show()
        if(category == -1) {
            val sendIntent = Intent(this, LoginActivity::class.java).apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "")
            }
            startActivity(sendIntent)
        }
        category = usersDB.getCategory()
        text = category.toString()
        toast = Toast.makeText(this, text, duration)
        toast.show()

        Log.d("SQL", "setCategory: $category")
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        when (item.itemId) {
            R.id.nav_home -> {
                webView.loadUrl("file:///android_asset/Home.html")
            }
            R.id.nav_notizie -> {

            }
            R.id.nav_orari -> {

            }
            R.id.nav_specializzazioni -> {

            }
            R.id.nav_logout -> {
                usersDB.deleteTable()
                webView.loadUrl("file:///android_asset/Login.html")
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}

