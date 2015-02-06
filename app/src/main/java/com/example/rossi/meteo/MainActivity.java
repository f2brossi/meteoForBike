package com.example.rossi.meteo;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends Activity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    protected TextView mLatitudeText;
    protected TextView mLongitudeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
   /* protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Read the search term the user entered, retrieve the near forecast, and
     * update the TextView with the forecast details.
     */
    public void getForecast(View v) {

        final TextView textViewSearch = (TextView) findViewById(R.id.txt_search_start);
        if (TextUtils.isEmpty(textViewSearch.getText()))
            return;
        final TextView textViewForecast = (TextView) findViewById(R.id.txt_forecast);
        textViewForecast.setText("");

        final TextView textViewSearch2 = (TextView) findViewById(R.id.txt_search_end);
        if (TextUtils.isEmpty(textViewSearch.getText()))
            return;

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... args) {
                MeteoFrance mfForecast = new MeteoFrance();
                String forecastText = "";
                try {
                    List<Forecast> forecasts = mfForecast
                            .getNearForecast(String.valueOf(
                                    textViewSearch.getText()).trim(),String.valueOf(
                                    textViewSearch2.getText()).trim() );

                    if (forecasts.size() == 0) {
                        forecastText = "Uhm, where is that?";
                    } else {
                        for (Forecast forecast : forecasts) {
                            forecastText += forecast.key + " "
                                         + forecast.value + "\n";
                        }
                    }
                } catch (IOException e) {
                    Log.v(TAG, e.getMessage(), e);
                } catch (JSONException e) {
                    Log.v(TAG, e.getMessage(), e);
                }
                return forecastText;
            }

            @Override
            protected void onPostExecute(String forecastText) {
                textViewForecast.setText(forecastText);
            };
        }.execute();
    }


    @Override
    public void onConnected(Bundle bundle) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
      /*  mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        } else {
        */
           // Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
      //  }
    }

    @Override
    public void onConnectionSuspended(int i) {
     //   mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
