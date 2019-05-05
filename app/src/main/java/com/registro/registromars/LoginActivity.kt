package com.registro.registromars

import android.content.Context
import android.app.Activity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import android.webkit.WebView
import kotlinx.android.synthetic.main.content_main.*
import android.util.Log

class LoginActivity: AppCompatActivity(){
    lateinit var usersDB: UsersDBHelper
    val url = "file:///android_asset/Login.html"
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

    }


}

class WebAppInterface internal constructor(var context_login : Context) {

    @JavascriptInterface
    fun setCategory(category: Int): String{

        SQLite.sqli = UsersDBHelper(context_login as Activity)
        SQLite.sqli.setCategory(category)
        (context_login as Activity).finish()
        return "Return!"
    }

}

class SQLite() {
    companion object {
        lateinit var sqli : UsersDBHelper
    }
}
