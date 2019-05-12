package com.registro.registromars

import android.os.AsyncTask
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class DownloadRequest{
    companion object{
        fun downloadFile(uri: String){
            val url = URL("http://www.google.com/")

            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"  // optional default is GET

                println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")

/*                inputStream.bufferedReader().use { it ->
                    it.lineSequence().iterator()
                }
                */
            }
        }

        class UpdateTask : AsyncTask<String, String, String>(), Parcelable {
            private var filePath = ""

            override fun doInBackground(vararg parameters: String): String {
                try {
                    //readText = URL("https://web.spaggiari.eu/sdg/app/default/comunicati.php?sede_codice=VIIT0010&referer=http://www.fermibassano.edu.it/").readText()
                    downloadFile("lll")
                } catch (e: Exception) {
                    Log.d("TAG", "///////////////////// ERRORE CONNESIONEEEEEEEEEEE   " + e)
                }
                //while (readText == "");
                return ""
            }
            override fun onPostExecute(result: String) {
                val readText = result

                if (readText != "") {
                    Log.d("TAG", "HTML     $readText")
                    val f = File(filePath)
                    f.writeText(readText)
                } else {
                    Log.d("TAG", "///////////////////// FAILED PAGE IS EMPTY   $readText")
                }
                val f = File(filePath)
                val text = f.readText()
                Log.d("TAG", "CONTENT $text")
            }

            override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(filePath)
            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<UpdateTask> {
                override fun createFromParcel(parcel: Parcel): UpdateTask {
                    return UpdateTask()
                }

                override fun newArray(size: Int): Array<UpdateTask?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}
