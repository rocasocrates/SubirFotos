package com.example.subirfotos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button galeria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        galeria = (Button)findViewById(R.id.galeria);

        galeria.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.galeria)
        {
            Intent intent = new Intent(MainActivity.this, Galeriados.class);

            startActivity(intent);
        }

    }
}
