package com.example.didyoufeelit

import android.text.TextUtils
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

class Utils {
    //When a function or property is defined inside a companion object, it becomes associated with the class itself rather than with instances of the class.
    // This means you can access those functions or properties directly through the class name, without needing to create an instance of the class.
    companion object {
        private  val LOG_TAG: String = Utils::class.java.simpleName

        fun fetchEarthquakeData(requestUrl: String?): Event? {
            if (requestUrl == null) {
                return null
            }
            val url: URL? = createUrl(requestUrl)
            var JsonResponse: String? = null
            try {
                JsonResponse = makeHttpRequest(url)
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Error closing input stream", e)
            }
            val earthQuake: Event? = extractFeatureFromJson(JsonResponse)
            return earthQuake
        }

        /*suspend fun fetchEarthquakeData(requestUrl: String?): Event? {
            if (requestUrl == null) {
                return null
            }
            return withContext(Dispatchers.IO) {
                try {
                    val url = createUrl(requestUrl)
                    val jsonResponse = makeHttpRequest(url)
                    extractFeatureFromJson(jsonResponse)
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
            }
        }
        */

        private fun extractFeatureFromJson(jsonResponse: String?): Event? {
            if (TextUtils.isEmpty(jsonResponse)) return null
            return try {
                val baseJsonResponse = JSONObject(jsonResponse)
                val featureArray = baseJsonResponse.getJSONArray("features")
                if (featureArray.length() > 0) {
                    val firstFeature = featureArray.getJSONObject(0)
                    val properties=firstFeature.getJSONObject("properties")
                    val title = properties.getString("title")
                    val noOfPerson = properties.getString("felt")
                    val preceivedStrength = properties.getString("cdi")

                    Event(title, noOfPerson, preceivedStrength)
                } else {
                    null
                }
            } catch (e: JSONException) {
                Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e)
                null
            }
        }

        private fun makeHttpRequest(url: URL?): String? {
            var jsonResponse: String? = null
            if (url == null) return jsonResponse
            var urlConnection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            return try {
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.connectTimeout = 15000
                urlConnection.readTimeout = 10000
                urlConnection.connect()
                inputStream = urlConnection.inputStream
                jsonResponse = readFromStream(inputStream)
                jsonResponse
            } finally {
                urlConnection?.disconnect()
                inputStream?.close()
            }

        }

        private fun createUrl(requestUrl: String?): URL? {
            var url: URL? = null
            try {
                url = URL(requestUrl)
            } catch (e: MalformedURLException) {
                Log.e(LOG_TAG, "Error with creating URL", e)
            }
            return url
        }

        private fun readFromStream(inputStream: InputStream): String? {

            return try {
                val output = StringBuilder()
                val reader =
                    BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
                var line = reader.readLine()
                while (line != null) {
                    output.append(line)
                    line = reader.readLine()
                }
                reader.close()
                output.toString()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
}