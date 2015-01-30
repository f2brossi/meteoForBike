package com.example.rossi.meteo;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
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


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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


}
