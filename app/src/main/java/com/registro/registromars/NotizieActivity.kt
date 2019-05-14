package com.registro.registromars

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.URL

class NotizieActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var notizie : List<Notizia> = listOf()
    val url = "file:///android_asset/Notizie.html"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true
        webView.setWebViewClient(MyWebViewClient())
        webView.addJavascriptInterface(NotizieActivityInterface( this), "Android")
        webView.loadUrl(this.url)

        val task = MyAsyncTask(this)
        task.execute("https://www.fermibassano.edu.it/notizie?format=feed&type=rss")

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }
    fun parseNotizie(filelocation : String?){
//        val listView : ListView = findViewById(R.id.list_view)
        var notizie: List<Notizia>? = null
        try {
            val parser = XmlPullParserHandler()
            val istream = File(filelocation).inputStream()
            notizie = parser.parse(istream)
            this.notizie = notizie
//            val adapter = ArrayAdapter<Notizia>(this, android.R.layout.simple_list_item_1, notizie)
//            listView.adapter = adapter

        } catch (e: IOException) {
            e.printStackTrace()
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
            */
            R.id.nav_notizie -> {

            }
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
        return true
    }

        class MyAsyncTask internal constructor(context: NotizieActivity) : AsyncTask<String, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<NotizieActivity> = WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
            }

            override fun doInBackground(vararg params: String?): String? {
                publishProgress("Download started") // Calls onProgressUpdate()
                val filelocation = getNotizie(params[0])
                return filelocation
            }

            private fun getNotizie(url: String?): String {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return ""
                val readText: String
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
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            Log.d("Perm", "Permission not granted")
                            ActivityCompat.requestPermissions(
                                activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                101
                            )
                        }
                        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED
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
                activity.parseNotizie(result)
            }

            override fun onProgressUpdate(vararg text: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return

                Toast.makeText(activity, text.firstOrNull(), Toast.LENGTH_SHORT).show()

            }
        }

}
class NotizieActivityInterface internal constructor(private var context : NotizieActivity) {

    @JavascriptInterface
    fun getNotizie() : List<Notizia>?{
        val activityReference: WeakReference<NotizieActivity> = WeakReference(context)
        val activity = activityReference.get()
        return activity?.notizie
    }
}