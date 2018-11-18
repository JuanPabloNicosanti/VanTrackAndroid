package utn.proy2k18.vantrack.connector;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import utn.proy2k18.vantrack.exceptions.BackendConnectionException;


public class HttpConnector extends AsyncTask<String, Void, String> {

    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    public static HttpConnector getInstance() {
        return new HttpConnector();
    }

    public HttpConnector() {
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... params) {
        final String stringUrl = params[0];
        final String REQUEST_METHOD = params[1];
        String result = null;
        HttpURLConnection connection = null;

        try {
            //Create a URL object holding our url
            URL myUrl = new URL(stringUrl);
            //Create a connection
            connection = (HttpURLConnection) myUrl.openConnection();
            //Set methods and timeouts
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            switch (REQUEST_METHOD) {
                case "GET":
                    connection.connect();
                    result = readResponse(connection);
                    break;
                case "POST":
                    if (params.length > 3) {
                        // params[3] should be the auth key.
                        connection.setRequestProperty("Authorization", "key=" + params[3]);
                    }
                    postData(connection, params[2]);
                    result = readResponse(connection);
                    break;
                case "PATCH":
                    connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
                    if (params.length > 2) {
                        postData(connection, params[2]);
                        connection.getInputStream();
                    } else {
                        connection.connect();
                    }
                    result = String.valueOf(connection.getResponseCode());
                    break;
                case "DELETE":
                    result = String.valueOf(sendDeleteRequest(connection));
                    break;
                case "PUT":
                    if (params.length > 2) {
                        postData(connection, params[2]);
                    } else {
                        connection.connect();
                    }
                    result = readResponse(connection);
                    break;
                default:
                    throw new RuntimeException("Valid Request methods GET, POST, PUT, DELETE and PATCH.");
            }

        } catch (SocketTimeoutException ste) {
            throw new BackendConnectionException("Error accediendo al servidor.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            result = null;
        } finally {
            if (connection != null) connection.disconnect();
        }
        return result;
    }

    public String readUrl(String mapsApiDirectionsUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(mapsApiDirectionsUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception reading url", e.toString());
        } finally {
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        String inputLine;
        InputStream responseStream;
        if (connection.getResponseCode() >= 400) {
            responseStream = connection.getErrorStream();
        } else {
            responseStream = connection.getInputStream();
        }

        //Create a new InputStreamReader
        InputStreamReader streamReader = new InputStreamReader(responseStream);
        //Create a new buffered reader and String Builder
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        //Check if the line we are reading is not null
        while ((inputLine = reader.readLine()) != null) {
            stringBuilder.append(inputLine);
        }
        //Close our InputStream and Buffered reader
        reader.close();
        streamReader.close();
        //Set our result equal to our stringBuilder
        return stringBuilder.toString();
    }

    private void postData(HttpURLConnection urlConnection, String payload) throws IOException {
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("Content-Type", "application/json");

        // Send the post body
        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
        writer.write(payload);
        writer.flush();
        writer.close();
    }

    private int sendDeleteRequest(HttpURLConnection urlConnection) throws IOException {
        urlConnection.setDoOutput(true);
        urlConnection.connect();
        return urlConnection.getResponseCode();
    }

    protected void onPostExecute (String result){
        super.onPostExecute(result);
    }
}
