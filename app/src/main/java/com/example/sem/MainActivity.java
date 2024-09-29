package com.example.sem;

import static android.content.Intent.*;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sem.ShowAllEvents;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //layout according to the xml file
        setContentView(R.layout.main_activity);
    }
    public void onShowAllEventsClicked(View view){
        Intent intent = new Intent(MainActivity.this, ShowAllEvents.class);
        startActivity(intent);
    }
}