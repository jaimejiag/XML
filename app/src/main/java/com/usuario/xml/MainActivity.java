package com.usuario.xml;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnPartes;
    private Button btnNotas;
    private Button btnRSS;
    private Button btnNoticias;
    private Button btnCreacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPartes = (Button) findViewById(R.id.btn_partes);
        btnNotas = (Button) findViewById(R.id.btn_notas);
        btnRSS = (Button) findViewById(R.id.btn_RSS);
        btnNoticias = (Button) findViewById(R.id.btn_noticias);
        btnCreacion = (Button) findViewById(R.id.btn_creacion);

        btnPartes.setOnClickListener(this);
        btnNotas.setOnClickListener(this);
        btnRSS.setOnClickListener(this);
        btnNoticias.setOnClickListener(this);
        btnCreacion.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        if (v == btnPartes)
            intent = new Intent(this, Partes.class);
        if (v == btnNotas)
            intent = new Intent(this, Notas.class);
        if (v == btnRSS)
            intent = new Intent(this, RSS.class);
        if (v == btnNoticias)
            intent = new Intent(this, Noticias.class);
        if (v == btnCreacion)
            intent = new Intent(this, Creacion.class);

        if (intent != null)
            startActivity(intent);
    }
}
