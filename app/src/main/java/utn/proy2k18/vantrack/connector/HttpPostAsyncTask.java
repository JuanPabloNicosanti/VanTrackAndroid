package utn.proy2k18.vantrack.connector;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpPostAsyncTask extends AsyncTask<String, Void, Void> {
    // This is the JSON body of the post
    private JSONObject postData;

    // This is a constructor that allows you to pass in the JSON body
    public HttpPostAsyncTask(JSONObject postData) {
        if (postData != null) {
            this.postData = postData;
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpsURLConnection urlConnection = null;
        try {
            // This is getting the url from the string we passed in
            URL url = new URL(params[0]);
            // Create the urlConnection
            urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", "key=" + params[1]);

            // Send the post body
            if (this.postData != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(postData.toString());
                writer.flush();
                writer.close();
                urlConnection.getInputStream(); //do not remove this line. request will not work without it
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return null;
    }
}
