package com.example.rossi.meteo;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Retrieves data from the meteo france web server concerning rain forecasts for
 * cities in France.
 */
public class MeteoFrance {

    private final HttpContext mLocalContext;

    private Map<String, String> map = new HashMap<String, String>();

    public MeteoFrance() {

        // Let the CookieStore store our cookies.
        CookieStore cookieStore = new BasicCookieStore();
        mLocalContext = new BasicHttpContext();
        mLocalContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        map.put("94140", "940020");
        map.put("93300","930010");
        map.put("75020","751200");
        map.put("75019","751190");
        map.put("75018","751180");
        map.put("75017","751170");
        map.put("75016","751160");
        map.put("75015","751150");
        map.put("75014","751150");
        map.put("75013","751130");
        map.put("75012","751120");
        map.put("75011","751110");
        map.put("75010","751100");
        map.put("75009","751090");
        map.put("75008","751080");
        map.put("75007","751070");
        map.put("75006","751060");
        map.put("75005","751050");
        map.put("75004","751040");
        map.put("75003","751030");
        map.put("75002","751020");
        map.put("75001","751010");
        map.put("94400","940810");
        map.put("94220","940180");
        map.put("93500","930550");
        map.put("92400","920260");
        map.put("95110","955820");
        map.put("94130","940520");
    }

    /**
     * @param postalCode
     *            a postal code of a city in France.
     * @return the meteo france city id for this location.
     * @throws java.io.IOException
     */
    public String getCityId(String postalCode) throws IOException {
        return map.get(postalCode);
    }


    /**
     * @param postalCode
     *            a postal code for a city in France
     * @return the rain forecasts for the near future (within the next hour).
     *         May return one forecast for one hour, or multiple forecasts for
     *         smaller intervals (ex: 15 minutes).
     * @throws IOException
     */
    public List<Forecast> getNearForecast(String postalCode, String postalCodeEnd) throws IOException, JSONException {
        List<Forecast> result = new ArrayList<Forecast>();

        String postalCodeRetrieveed = retrieveFromLocation();
        result = getForecast(postalCodeRetrieveed);
        if ( !postalCodeEnd.isEmpty())
            result.addAll(getForecast(postalCodeEnd));
        return result;
    }

    private String retrieveFromLocation() throws IOException, JSONException {
        String longitude= "2.422317";
        String latitude = "48.80093";

        // Execute our http get request for the forecast for this location.
       //String url = String.format("http://nominatim.openstreetmap.org/reverse?format=json&lat=%s&lon=%s&zoom=18&addressdetails=1",latitude,longitude);

        String url = "http://nominatim.openstreetmap.org/reverse?format=json&lat=48.80093&lon=2.422317&zoom=18&addressdetails=1";

        URI uri = URI.create(url);
        HttpGet httpGet = new HttpGet(uri.toString());
        HttpResponse response = mHttpClient.execute(httpGet, mLocalContext);
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();

        BufferedReader r = new BufferedReader(new InputStreamReader(content));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }

        String totalStr= total.toString();
        JSONObject jObject = new JSONObject(totalStr);

        JSONObject location = jObject.getJSONObject("address");
        String codepostal = location.getString("postcode");

        return codepostal;
    }


    private List<Forecast> getForecast(String postalCode) throws IOException, JSONException {
        List<Forecast> result = new ArrayList<Forecast>();
        // Get the city id for this location
        String cityId = getCityId(postalCode);
        if (cityId == null)
            return result;

        // Execute our http get request for the forecast for this location.
        String url = String.format("http://www.meteofrance.com/mf3-rpc-portlet/rest/pluie/"+ cityId, cityId);

        URI uri = URI.create(url);
        HttpGet httpGet = new HttpGet(uri.toString());
        HttpResponse response = mHttpClient.execute(httpGet, mLocalContext);
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();

        BufferedReader r = new BufferedReader(new InputStreamReader(content));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }

        String totalStr= total.toString();

        JSONObject jObject = new JSONObject(totalStr);
        Forecast forecastUpdate = new Forecast(postalCode,"");
        result.add(forecastUpdate);

        JSONArray niveauPluieTest = jObject.getJSONArray("niveauPluieText");
        for (int i=0; i < niveauPluieTest.length(); i++)
        {
            try {
                String onePrevision = niveauPluieTest.getString(i);
                Forecast forecast = new Forecast(onePrevision.substring(0,15),
                        onePrevision.substring(16));
                result.add(forecast);
            } catch (JSONException e) {
                // Oops
            }
        }

        return result;
    }

    private HttpClient mHttpClient = new DefaultHttpClient() {

        @Override
        protected RedirectHandler createRedirectHandler() {
            return mRedirectHandler;
        }
    };

    private RedirectHandler mRedirectHandler = new DefaultRedirectHandler() {

        /*
         * (non-Javadoc)
         *
         * @see
         * org.apache.http.impl.client.DefaultRedirectHandler#isRedirectRequested
         * (org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext)
         * We want to stop redirection if the redirection is to a URL containing
         * the city id. In this case, we want to extract the city id from the
         * location URL.
         */
        @Override
        public boolean isRedirectRequested(HttpResponse response,
                                           HttpContext context) {
            return false;
        }
    };

}