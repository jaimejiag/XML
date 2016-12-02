package com.usuario.xml;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by usuario on 21/11/16.
 */

public class Partes extends AppCompatActivity{
    public static final String TEXTO = "<texto><uno>Hello World!</uno><dos>Goodbye</dos></texto>";
    TextView mTxvInformacion;
    ViewGroup mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partes);

        mLayout = (RelativeLayout) findViewById(R.id.activity_partes);
        mTxvInformacion = (TextView) findViewById(R.id.txv_partes_info);

        try {
            mTxvInformacion.setText(Analisis.analizar(TEXTO));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Snackbar.make(mLayout, "Error: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(mLayout, "Error: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
        }
    }
}
