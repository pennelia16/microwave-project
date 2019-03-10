package com.example.cmf_d;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    private Button FindMicrowave;
    private Button SettingsMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FindMicrowave =(Button) findViewById(R.id.FindMicrowave);
        SettingsMenu = (Button) findViewById(R.id.SettingsMenu);
        FindMicrowave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMapsActivity();
            }
        });
        SettingsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenuActivity();
            }
        });
    }
    public void openMapsActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    public void openMenuActivity(){
        Intent intent2 = new Intent(this, MenuActivity.class);
        startActivity(intent2);
    }
}
