package com.example.didyoufeelit

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val USGS_REQUEST_URL="https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2016-01-01&endtime=2016-05-02&minfelt=50&minmagnitude=5"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Create an AsyncTask to perform the HTTP request to the given URL
        // on a background thread. When the result is received on the main UI thread,
        // then update the UI.
        val earthquakeAsyncTask=EarthquakeAsyncTask()
        earthquakeAsyncTask.execute(USGS_REQUEST_URL)
    }

    private fun updateUi(earthQuake: Event) {
        val titleTextView = findViewById<TextView>(R.id.title_tv)
        titleTextView.text = earthQuake.title

        val tsunamiTextView = findViewById<TextView>(R.id.no_of_people)
        tsunamiTextView.text = getString(R.string.num_of_people_felt_it, earthQuake.numOfPeople)

        val magnitudeTextView = findViewById<TextView>(R.id.perceived_magnitude)
        magnitudeTextView.text = earthQuake.perceivedStrength
    }

    private inner class EarthquakeAsyncTask : AsyncTask<String, Void, Event>() {

        override fun doInBackground(vararg urls: String): Event? {
            // Perform the HTTP request for earthquake data and process the response.
            if (urls.isEmpty() || urls[0] == null) return null
            return Utils.fetchEarthquakeData(urls[0])
        }


        override fun onPostExecute(result: Event?) {
            // Update the information displayed to the user.
            if (result != null) {
                updateUi(result)
            }
        }
    }
}
// The given below will implement Kotlin coroutine instead of AsyncTask:

/*class MainActivity : AppCompatActivity() {
    private val USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2016-01-01&endtime=2016-05-02&minfelt=50&minmagnitude=5"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start the coroutine to fetch earthquake data
        GlobalScope.launch(Dispatchers.Main) {
        //you will also have to change in fetchEarthquakeData() in Utils class to use coroutine which is given in comment block in Utils class , just see that.
            val result = Utils.fetchEarthquakeData(USGS_REQUEST_URL)
            updateUi(result)
        }
    }

    private fun updateUi(earthQuake: Event?) {
        if (earthQuake != null) {
            findViewById<TextView>(R.id.title_tv).text = earthQuake.title
            findViewById<TextView>(R.id.no_of_people).text = getString(R.string.num_of_people_felt_it, earthQuake.numOfPeople)
            findViewById<TextView>(R.id.perceived_magnitude).text = earthQuake.perceivedStrength
        }
    }
}
*/