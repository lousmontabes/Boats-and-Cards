package com.example.lluismontabes.gameofboatsandcards.Interface;

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

import com.example.lluismontabes.gameofboatsandcards.R;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by lluismontabes on 30/3/17.
 */

public class MatchmakingActivity extends AppCompatActivity {

    private boolean connectionEstablished;

    private int queueIndex = 0;
    private int opponentIndex = 0;

    private int assignedPlayer;
    private int assignedMatch = -1;

    private String requestStatus = null;

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

    private void changeConnectionMessage(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewStatus.setText(msg);
            }
        });
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

    private String getServerResponse(String url, int timeout){

        HttpURLConnection c = null;

        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return null;

    }

    private void requestMatch(){

        String json = null;

        while (json == null){
            json = getServerResponse("https://pis04-ub.herokuapp.com/check_match_requests.php", 2000);
        }

        ActionIndexPair response = new Gson().fromJson(json, ActionIndexPair.class);

        switch (response.action) {

            case REQUEST_CREATED:

                // No active match requests. The server created a match request.
                // Wait for another player to join.

                assignedPlayer = 1; // The user will be player1 during the match.
                waitForRequestResponse(response.index);

                break;

            case MATCH_JOINED:

                // Joined an active match request.
                // The user has been placed in a match with id response.index

                assignedPlayer = 2; // The user will be player2 during the match.
                joinMatch(response.index);

                break;

            case ERROR:
            default:

                // There was a problem creating or looking for a request.
                changeConnectionMessage(getString(R.string.problemFindingMatchMessage));

                break;

        }

    }

    private void waitForRequestResponse(int requestId){

        changeConnectionMessage(getString(R.string.searchingForOpponentMessage));

        // Wait for someone to answer the user's match request.
        while (assignedMatch == -1 || requestStatus == null){

            // -1 means no one has answered the match request.
            // It's the default value of the variable assignedMatch.

            // A null requestStatus means the data couldn't be retrieved.
            // It's the default value of the variable requestStatus.

            requestStatus = getServerResponse(
                    "https://pis04-ub.herokuapp.com/check_request_status.php?requestId=" + requestId,
                    2000);

            // Do not attempt to parse if the data couldn't be retrieved.
            if (requestStatus != null){
                assignedMatch = Integer.parseInt(requestStatus);
            }

        }

        // At this point someone has answered the user's match request and response contains
        // the match to which the user has been assigned.

        joinMatch(assignedMatch);

    }

    private boolean confirmMatchRequest(int requestId){

        // TODO: Implement this method.
        return true;

    }

    private void cancelMatchRequest(int requestId){

        // TODO: Implement this method.

    }

    private void joinMatch(int matchId){

        // TODO: Add comprovation that the user is still active.
        // Only join the match if both users are active.

        changeConnectionMessage(getString(R.string.joiningMatchMessage) + matchId);

        // Start GameActivity
        Intent i = new Intent(MatchmakingActivity.this, GameActivity.class);
        i.putExtra("matchId", matchId);
        i.putExtra("assignedPlayer", assignedPlayer);
        startActivity(i);

    }

    private enum Action {

        // IMPORTANT: @SerializedName("n") makes it possible for Gson to parse the incoming
        // JSON into an ActionIndexPair properly, as the action is received as an integer.

        @SerializedName("0")
        REQUEST_CREATED,
        @SerializedName("1")
        MATCH_JOINED,
        @SerializedName("2")
        ERROR
    };

    private class ActionIndexPair{

        private Action action;
        private int index;

    }

    private class NetworkTask extends AsyncTask<String, String, Void>{

        @Override
        protected Void doInBackground(String... params) {

            while(!isServerReachable("https://pis04-ub.herokuapp.com/check_connection.php")){
                System.out.println("Attempting to establish connection...");
            }

            System.out.println("Connection established succesfully.");
            connectionEstablished = true;

            requestMatch();

            return null;

        }

    }

}
