package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

/**
 * Created by lluismontabes on 30/3/17.
 */

public class MatchmakingActivity extends AppCompatActivity {

    private boolean connectionEstablished;
    TextView textViewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_matchmaking);

        textViewStatus = (TextView) findViewById(R.id.textViewStatus);

        new NetworkTask().execute();

    }

    private boolean isServerReachable(String url) {

        ConnectivityManager connMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {

            try {
                URL urlServer = new URL(url);
                HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                urlConn.setConnectTimeout(3000); // 3 second timeout
                urlConn.connect();

                System.out.println(urlConn.getResponseCode());

                if (urlConn.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }

        }

        return false;

    }

    private class NetworkTask extends AsyncTask<String, String, Void>{

        @Override
        protected Void doInBackground(String... params) {

            while(!isServerReachable("https://pis04-ub.herokuapp.com/retrieve_remote_action.php")){
                System.out.println("Attempting to establish connection...");
            }

            System.out.println("Connection established succesfully.");
            connectionEstablished = true;

            runOnUiThread(new Runnable() {
                public void run() {
                    textViewStatus.setText("Connection established succesfully!");
                }
            });

            // Start GameActivity
            Intent i = new Intent(MatchmakingActivity.this, GameActivity.class);
            startActivity(i);

            return null;

        }
    }

}
