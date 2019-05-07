package com.registro.registromars

import android.content.Context
import android.content.Intent
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.TextView
import kotlinx.android.synthetic.main.content_main.webView
import kotlinx.android.synthetic.main.nav_header_main.view.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var usersDB: UsersDBHelper = UsersDBHelper(this)
    val url = "file:///android_asset/Home.html"
    var backpressed_counter : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true
        webView.setWebViewClient(MyWebViewClient())
        webView.addJavascriptInterface(HomeActivityInterface( this), "Android")
        webView.loadUrl(this.url)

        SQLite.sqli = UsersDBHelper(this)
        var category: Int = SQLite.sqli.getCategory()

        if(category == -1) {
            val sendIntent = Intent(this, LoginActivity::class.java).apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "")
            }
            startActivity(sendIntent)
        }
        Log.d("TAG", "Cookie: "+ category)
        //TODO Add startActivityForResult or something
        category = SQLite.sqli.getCategory()
        nav_view.getHeaderView(0).category_TextView.text = translateCategory(category)
        Log.d("TAG", "Cookie: "+ category)
        Log.d("TAG", "Cookie translated: "+ translateCategory(category))

        Log.d("SQL", "setCategory: $category")
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    fun translateCategory(category : Int) : String{
        if(category == 0)
            return "Studente"
        else if(category == 1)
            return "Insegnante"
        else if(category == 2)
            return "Genitore"
        else if(category == 3)
            return "Segreteria"
        return "Undefined"
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            backpressed_counter = 0
        } else {
            backpressed_counter++
            if(backpressed_counter == 1){
                val message = "Press Back again to close app"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, message, duration)
                toast.show()
            }
            else{
                super.onBackPressed()
            }
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
                webView.loadUrl("file:///android_asset/OrariLezioni.html")
            }
            R.id.nav_specializzazioni -> {

            }R.id.nav_linkutili -> {
                webView.loadUrl("file:///android_asset/LinkUtili.html")
            }
            R.id.nav_logout -> {
                SQLite.sqli.setCategory(-1)
                val sendIntent = Intent(this, LoginActivity::class.java).apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "")
                }
                startActivity(sendIntent)

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        backpressed_counter = 0
        return true
    }
}
class MyWebViewClient : WebViewClient(){
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        return !("file://" in Uri.parse(url).host!!)
        // This is my web site, so do not override; let my WebView load the page
        // reject anything other
    }
}
class HomeActivityInterface internal constructor(var context_login : Context) {

    @JavascriptInterface
    fun openLinkInBrowser(link : String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        val bundle = Bundle()
        bundle.putBoolean("new_window", true)
        intent.putExtras(bundle)
        context_login.startActivity( intent, null)

    }
}


class SQLite() {
    companion object {
        lateinit var sqli : UsersDBHelper
    }
}
