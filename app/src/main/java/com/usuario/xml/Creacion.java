package com.usuario.xml;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

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

public class Creacion extends AppCompatActivity implements View.OnClickListener {
    public static final String RSS = "http://www.alejandrosuarez.es/feed/";
    //public static final String RSS = "http://10.0.2.2/feed/alejandro.xml";
    public static final String TEMPORAL = "alejandro.xml";
    public static final String FICHERO_XML = "resultado.xml";

    Button boton;
    ArrayList<Noticia> noticias;
    ViewGroup mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creacion);
        boton = (Button) findViewById(R.id.btn_creacion_xml);
        boton.setOnClickListener(this);
        mLayout = (RelativeLayout) findViewById(R.id.activity_creacion);
    }
    @Override
    public void onClick(View v) {
        if (v == boton)
            descarga(RSS, TEMPORAL);
    }

    private void descarga(String rss, String temporal) {
        final ProgressDialog progreso = new ProgressDialog(this);
        File miFichero = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), temporal);

        RestClient.get(rss, new FileAsyncHttpResponseHandler(miFichero) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                progreso.dismiss();
                Snackbar.make(mLayout, "Fallo: " + statusCode + "\n" + throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                progreso.dismiss();
                Snackbar.make(mLayout, "Fichero descargador OK\n" + file.getPath(), Snackbar.LENGTH_SHORT).show();

                try {
                    noticias = Analisis.analizarNoticias(file);
                    Analisis.crearXML(noticias, FICHERO_XML);
                    Snackbar.make(mLayout, "Fichero XML creado OK\nEn: " + file.getPath(), Snackbar.LENGTH_SHORT).show();
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
}
