package com.usuario.xml;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.usuario.xml.modelo.Noticia;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by usuario on 21/11/16.
 */

public class Noticias extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{
    public static final String CANAL = "http://www.europapress.es/rss/rss.aspx?ch=279";
    //public static final String CANAL = "http://10.0.2.2/feed/europapress.xml";
    public static final String TEMPORAL = "europapress.xml";

    ListView lista;
    ArrayList<Noticia> noticias;
    ArrayAdapter<Noticia> adapter;
    FloatingActionButton fab;
    ViewGroup mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticias);

        lista = (ListView) findViewById(R.id.lstv_noticias);
        lista.setOnItemClickListener(this);
        fab = (FloatingActionButton) findViewById(R.id.fab_noticias);
        fab.setOnClickListener(this);
        mLayout = (CoordinatorLayout) findViewById(R.id.activity_noticias);
    }

    @Override
    public void onClick(View v) {
        if (v == fab)
            descarga(CANAL, TEMPORAL);
    }

    private void descarga(String canal, String temporal) {
        final ProgressDialog progreso = new ProgressDialog(this);
        File miFichero = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), temporal);
        RestClient.get(canal, new FileAsyncHttpResponseHandler(miFichero) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                progreso.dismiss();
                Snackbar.make(mLayout, "Fallo: " + throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                progreso.dismiss();
                Snackbar.make(mLayout, "Fichero descargador OK\n" + file.getPath(), Snackbar.LENGTH_SHORT).show();
                try {
                    noticias = Analisis.analizarNoticias(file);
                    mostrar();
                } catch (XmlPullParserException e) {
                    Snackbar.make(mLayout, "Excepción XML: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Snackbar.make(mLayout, "Excepción I/O: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Conectando . . .");
                progreso.setCancelable(false);
                progreso.show();
            }
        });
    }

    private void mostrar() {
        if (noticias != null){
            if (adapter == null) {
                adapter = new ArrayAdapter<Noticia>(this, android.R.layout.simple_list_item_1, noticias);
                lista.setAdapter(adapter);
            } else {
                adapter.clear();
                adapter.addAll(noticias);
            }
        } else
            Snackbar.make(mLayout, "Error al crear la lista", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Uri uri = Uri.parse((String) noticias.get(position).getLink());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else
            Snackbar.make(mLayout, "No hay un navegador", Snackbar.LENGTH_SHORT).show();
    }
}
