package com.registro.registromars

import android.Manifest
import android.app.Activity
import java.io.File
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.widget.Toast
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
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
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.URL

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var usersDB: UsersDBHelper = UsersDBHelper(this)
    val url = "file:///android_asset/Home.html"
    private var backPressedTwice : Boolean = false
    var category = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true
        webView.setWebViewClient(MyWebViewClient())
        webView.addJavascriptInterface(HomeActivityInterface( this, this), "Android")
        webView.loadUrl(this.url)

        SQLite.sqli = UsersDBHelper(this)
        var category: Int = SQLite.sqli.getCategory()
        nav_view.getHeaderView(0).category_TextView.text = translateCategory(category)

        if(category == -1) {
            val sendIntent = Intent(this, LoginActivity::class.java).apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "")
            }
        // startActivity(sendIntent)
            startActivityForResult(sendIntent, 1)
        }
        this.category = category
        dynamicMenu(category)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }


    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        Log.d("TAG", "requestCode $requestCode")
        Log.d("TAG", "resultCode $resultCode")

        val category = SQLite.sqli.getCategory()

        if (SQLite.sqli.getCategory() == -1) {
            Log.e("TAG","ERROR CATEGORY IS $category")
            finish()
            //SQLite.sqli.setCategory(0)
            //Log.e("TAG", "Forced category: "+SQLite.sqli.getCategory())
        }
        nav_view.getHeaderView(0).category_TextView.text = translateCategory(category)
        dynamicMenu(category)
        webView.loadUrl("file:///android_asset/Home.html")
        Log.d("TAG", "Category: $category")
    }

    fun translateCategory(category : Int) : String{
        this.category = category
        if(category < 0)
            return "Undefined"
        val categories = arrayOf<String>("Studente delle Superiori", "Studente delle Medie", "Insegnante", "Genitore", "Segreteria")
        return categories[category]
    }

    fun dynamicMenu(category: Int){
            var priorities = arrayOf<String>()
            when (category){
                0 -> priorities = arrayOf<String>("OrariLezioni", "Notizie")
                1 -> priorities = arrayOf<String>("OrariLezioni", "Notizie", "Mappa")
                2 -> priorities = arrayOf<String>("Notizie", "Registro")
                3 -> priorities = arrayOf<String>("Notizie", "Mappa", "orarisegreteria")
                4 -> priorities = arrayOf<String>("Notizie", "Registro")
            }
            nav_view.menu.clear()
            var counter = 0
            for(item in priorities){
                var menu_item = nav_view.menu.add(0, counter, 0, item)
                menu_item.apply {
                    setOnMenuItemClickListener {
                        webView.clearHistory()
                        webView.loadUrl("file:///android_asset/$item.html")

                        drawer_layout.closeDrawer(GravityCompat.START)

                        true
                    }
                }
            }
        if(category == 1){
            var menu_item = nav_view.menu.add(0, counter, 0, "Specializzazioni")
            menu_item.apply {
                setOnMenuItemClickListener {
                    webView.clearHistory()
                    webView.loadUrl("file:///android_asset/specializzazione/specializzazioni.html")

                    drawer_layout.closeDrawer(GravityCompat.START)

                    true
                }
            }
        }

        var submenu = nav_view.menu.addSubMenu("Altro")
        var logout_item = submenu.add(1, 0, 0, "logout")
            logout_item.apply {
            setOnMenuItemClickListener {
                webView.clearHistory()
                webView.loadUrl("file:///android_asset/Home.html")
                SQLite.sqli.setCategory(-1)
                val sendIntent = Intent(this@HomeActivity, LoginActivity::class.java).apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "")
                }
                startActivityForResult(sendIntent, 1)

                drawer_layout.closeDrawer(GravityCompat.START)

                true
            }

        }
        var linkutili = submenu.add(1, 0, 0, "Link Utili")
        linkutili.apply {
            setOnMenuItemClickListener {
                webView.clearHistory()
                webView.loadUrl("file:///android_asset/LinkUtili.html")
                drawer_layout.closeDrawer(GravityCompat.START)

                true
            }

        }


    }


    override fun onBackPressed() {
        
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            return
        }
        else {
            if(backPressedTwice == true){
                super.onBackPressed()
                return
            }
            this.backPressedTwice = true
            val message = "Press Back again to close the app"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(this, message, duration)
            toast.show()
            Handler().postDelayed({
                this.backPressedTwice = false
            }, 1500)
                        
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
            /*
            R.id.nav_home -> {
                webView.loadUrl("file:///android_asset/Home.html") //WIP
            }
            R.id.nav_notizie -> {

            }
            R.id.nav_mappa -> {
                webView.loadUrl("file:///android_asset/Mappa.html") //WIP

            }
            R.id.nav_orari -> {
                webView.loadUrl("file:///android_asset/OrariLezioni.html")
            }
            R.id.nav_specializzazioni -> {

            }R.id.nav_linkutili -> {
                webView.loadUrl("file:///android_asset/LinkUtili.html")
            }*/
            R.id.nav_notizie -> {
                val sendIntent = Intent(this, NotizieActivity::class.java).apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "")
                }
                startActivityForResult(sendIntent, 2)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
class MyWebViewClient : WebViewClient(){
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        Log.d("URL", "Handling $url")
        var shouldOverride = !(("file://" in Uri.parse(url).host!!) || (".html" in Uri.parse(url).path!!))
        shouldOverride = shouldOverride && !(Uri.parse(url).toString() == "https://www.fermibassano.edu.it/progetti")
        Log.d("URL", "Should handle: $shouldOverride")
        return shouldOverride
        // This is my web site, so do not override; let my WebView load the page
        // reject anything other
    }
}
class HomeActivityInterface internal constructor(private var context : Context, private var cont : HomeActivity) {
    val activityReference: WeakReference<HomeActivity> = WeakReference(cont)

    @JavascriptInterface
    fun openLinkInBrowser(link : String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        val bundle = Bundle()
        bundle.putBoolean("new_window", true)
        intent.putExtras(bundle)
        context.startActivity( intent, null)

    }
    @JavascriptInterface
    fun translateCategory() : String{
        val activity = activityReference.get()
        if (activity == null || activity.isFinishing) return "Undefined"
        Log.d("JS", "Translating cat")
        return activity.translateCategory(activity.category)
    }
    @JavascriptInterface
    fun updateMenu(category: Int){
        val activity = activityReference.get()
        if (activity == null || activity.isFinishing) return
        activity.dynamicMenu(category)
        Log.d("JS", "In update menu")
    }
}


class SQLite() {
    companion object {
        lateinit var sqli : UsersDBHelper
    }
}
