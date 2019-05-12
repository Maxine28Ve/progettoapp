package com.registro.registromars

import android.Manifest
import java.io.File
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
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
    private var backpressed_counter : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val task = MyAsyncTask(this)
        task.execute("https://www.fermibassano.edu.it/notizie?format=feed&type=rss")
        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true
        webView.setWebViewClient(MyWebViewClient())
        webView.addJavascriptInterface(HomeActivityInterface( this), "Android")
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
        //TODO Add startActivityForResult or something

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    companion object {
        class MyAsyncTask internal constructor(context: HomeActivity) : AsyncTask<String, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<HomeActivity> = WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
            }

            override fun doInBackground(vararg params: String?): String? {
                publishProgress("Download started") // Calls onProgressUpdate()
                val filelocation = getNotizie(params[0])
                var notizie: List<Notizia>? = null
                try {
                    val parser = XmlPullParserHandler()
                    val istream = File(filelocation).inputStream()
                    notizie = parser.parse(istream)

                } catch (e: IOException) {
                    e.printStackTrace()
                }
                for (i in (notizie!!)) {
                    println(i)
                }
                return ""
            }

            private fun getNotizie(url: String?) : String{
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return ""
                val readText : String
                var location = ""
                try {
                    val remote = URL(url)

                    readText = remote.readText()
                    Log.d("readText", readText)
                    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val fileName = "notizie.xml"
                    location = "${path}/$fileName"
                    val notiziefile = File(path, fileName)
                    if (!notiziefile.exists()) {
                        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PERMISSION_GRANTED
                        ) {
                            Log.d("Perm", "Permission not granted")
                            ActivityCompat.requestPermissions(
                                activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                101
                            )
                        }
                        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PERMISSION_GRANTED
                        ) {
                            Log.d("Perm", "Permission granted")
                            notiziefile.createNewFile()
                        }
                    }
                    notiziefile.printWriter().use { out ->
                        out.print(readText)
                    }

                    Log.d("File", "file notizie.xml exists? " + notiziefile.exists())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    resp = e.message
                } catch (e: Exception) {
                    e.printStackTrace()
                    resp = e.message
                }

                return location

            }

            override fun onPostExecute(result: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                Toast.makeText(activity, "Done", Toast.LENGTH_SHORT).show()
//                activity.textView.text = result.let { it }
                //              activity.myVariable = 100
            }

            override fun onProgressUpdate(vararg text: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return

                Toast.makeText(activity, text.firstOrNull(), Toast.LENGTH_SHORT).show()

            }
        }
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

        Log.d("TAG", "Category: $category")
    }

    fun translateCategory(category : Int) : String{
        if(category < 0)
            return "Undefined"
        val categories = arrayOf<String>("Studente Superiori", "Studente Medie", "Genitore", "Insegnante", "Segreteria")
        return categories[category]
    }

    fun dynamicMenu(category: Int){
        var priorities = arrayOf<String>()
        when (category){
            0 -> priorities = arrayOf<String>("OrarioLezioni")
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            backpressed_counter = 0
        } else {
            backpressed_counter++
            if(backpressed_counter == 1){
                val message = "Press Back again to close the app"
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
            R.id.nav_logout -> {
                SQLite.sqli.setCategory(-1)
                val sendIntent = Intent(this, LoginActivity::class.java).apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "")
                }
                startActivityForResult(sendIntent, 1)

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
class HomeActivityInterface internal constructor(private var context_login : Context) {

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
