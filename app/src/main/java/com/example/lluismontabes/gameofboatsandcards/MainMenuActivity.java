package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainMenuActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button buto = (Button) findViewById(R.id.buttonSP);
        final Context c = this.getApplicationContext();
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
        buto = (Button) findViewById(R.id.buttonMP);
        buto.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                Toast.makeText(c, getString(R.string.comingSoon), Toast.LENGTH_SHORT).show();
            }
        });
        buto = (Button) findViewById(R.id.buttonSet);
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
        buto = (Button) findViewById(R.id.buttonCred);
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
        buto = (Button) findViewById(R.id.buttonExit);
        buto.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                MainMenuActivity.this.finish();
            }
        });
    }

    public void openCreditsActivity(View view){
        Intent i = new Intent(MainMenuActivity.this,CreditsActivity.class);

    }

}




