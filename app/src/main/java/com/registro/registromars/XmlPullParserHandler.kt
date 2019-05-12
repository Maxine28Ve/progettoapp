package com.registro.registromars

import android.util.Log
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream

class XmlPullParserHandler {
    private val notizies = ArrayList<Notizia>()
    private var notizie: Notizia? = null
    private var text: String? = null

    fun parse(inputStream: InputStream): List<Notizia> {
        var isItem = false
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagname = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> if (tagname.equals("item", ignoreCase = true)) {
                        // create a new instance of notizie
                        notizie = Notizia()
                        isItem = true
                        Log.d("notizie", "Got here")
                    }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> if (tagname.equals("item", ignoreCase = true)) {
                        // add notizie object to list
                        notizie?.let { notizies.add(it) }
                    } else if(isItem) {
                        if (tagname.equals("title", ignoreCase = true)) {
                            Log.d("XML", "text: " + text)
                            notizie!!.title = text

                        } else if (tagname.equals("link", ignoreCase = true)) {
                            notizie!!.link = text
                            Log.d("XML", "text: " + text)
                        } else if (tagname.equals("description", ignoreCase = true)) {
                            notizie!!.description = text
                            Log.d("XML", "text: " + text)
                        }
                    }


                    else -> {
                    }
                }
                eventType = parser.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return notizies
    }
}