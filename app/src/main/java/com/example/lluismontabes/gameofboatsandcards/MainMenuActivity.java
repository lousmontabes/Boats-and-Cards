package com.example.lluismontabes.gameofboatsandcards;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;



public class MainMenuActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button buto = (Button) findViewById(R.id.buttonSP);
        buto.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                // create an Intent using the current Activity
                // and the Class to be created
                Intent i = new Intent(MainMenuActivity.this,MainActivity.class);
                // pass the Intent to the Activity,
                // using the specified action defined in StartPage
                startActivity(i);
            }
        });
    }



}
