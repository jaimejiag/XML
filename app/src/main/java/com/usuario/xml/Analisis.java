package com.usuario.xml;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.util.Xml;

import com.usuario.xml.modelo.Noticia;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by usuario on 21/11/16.
 */

public class Analisis {

    //Primer Botón.
    public static String analizar(String texto) throws XmlPullParserException, IOException {
        StringBuilder cadena = new StringBuilder();
        XmlPullParser xpp = Xml.newPullParser();

        xpp.setInput(new StringReader(texto));
        int eventType = xpp.getEventType();
        cadena.append("Inicio . . . \n");

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                //System.out.println("Start document");
                cadena.append("Start document" + "\n");
            } else if (eventType == XmlPullParser.START_TAG) {
                //stem.out.println("Start tag "+xpp.getName());
                cadena.append("Start tag " + xpp.getName() + "\n");
            } else if (eventType == XmlPullParser.END_TAG) {
                //System.out.println("End tag "+xpp.getName());
                cadena.append("End tag " + xpp.getName() + "\n");
            } else if (eventType == XmlPullParser.TEXT) {
                //System.out.println("Text "+xpp.getText());
                cadena.append("Text " + xpp.getText() + "\n");
            }

            eventType = xpp.next();
        }

        //System.out.println("End document");
        cadena.append("End document" + "\n" + "Fin");
        return cadena.toString();
    }

    //Segundo botón.
    public static String analizarNombres(Context c) throws XmlPullParserException, IOException {
        boolean esNombre = false;
        boolean esNota = false;
        StringBuilder cadena = new StringBuilder();
        Double suma = 0.0;
        int contador = 0;

        XmlResourceParser xrp = c.getResources().getXml(R.xml.alumnos);
        int eventType = xrp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            /* El bloque comentado muestra sólo la nota media de todos los alumnos.
            if (eventType == XmlPullParser.START_TAG) {
                if (xrp.getName().equals("nota")) {
                    suma += Double.parseDouble(xrp.nextText());
                    contador++;
                }
            }*/

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xrp.getName().equals("nombre"))
                        esNombre = true;
                    if (xrp.getName().equals("nota")) {
                        esNota = true;

                        for (int i = 0; i < xrp.getAttributeCount(); i++)
                            cadena.append(xrp.getAttributeName(i) + ": " + xrp.getAttributeValue(i) + "\n");
                    }

                    break;

                case XmlPullParser.TEXT:
                    if (esNombre)
                        cadena.append("Alumno: " + xrp.getText() + "\n");

                    if (esNota) {
                        cadena.append("Nota: " + xrp.getText() + "\n");
                        suma += Double.parseDouble(xrp.getText());
                        contador++;
                    }

                    break;

                case XmlPullParser.END_TAG:
                    esNota = false;
                    break;
            }

            eventType = xrp.next();
        }

        cadena.append("Nota media: " + String.format("%.2f", suma / contador));
        xrp.close();

        return cadena.toString();
    }

    //Tercer botón.
    public static String analizarRSS(File file) throws NullPointerException, XmlPullParserException, IOException {
        boolean dentroItem = false;
        boolean dentroTitle = false;
        StringBuilder builder = new StringBuilder();
        XmlPullParser xpp = Xml.newPullParser();

        xpp.setInput(new FileReader(file));
        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xpp.getName().equals("item"))
                        dentroItem = true;

                    if (xpp.getName().equals("title") && dentroItem)
                        //dentroTitle = true;
                        builder.append(xpp.nextText() + "\n");
                    break;

                /*case XmlPullParser.TEXT:
                    if (dentroTitle)
                        builder.append(xpp.getText() +  "\n");
                    break;*/

                case XmlPullParser.END_TAG:
                    if (xpp.getName().equals("item"))
                        dentroItem = false;
                    /*if (xpp.getName().equals("title") && dentroItem)
                        dentroTitle = false;*/
                    break;
            }

            eventType = xpp.next();
        }

        return builder.toString();
    }

    public static ArrayList<Noticia> analizarNoticias(File file) throws XmlPullParserException, IOException {
        int eventType;
        ArrayList<Noticia> noticias = null;
        Noticia actual = null;
        boolean dentroItem = false;

        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(new FileReader(file));
        eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    noticias = new ArrayList<Noticia>();
                    break;

                case XmlPullParser.START_TAG:
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        dentroItem = true;
                        actual = new Noticia();
                    } else if (xpp.getName().equalsIgnoreCase("title") && dentroItem)
                        actual.setTitle(xpp.nextText());
                    else if (xpp.getName().equalsIgnoreCase("link") && dentroItem)
                        actual.setLink(xpp.nextText());
                    else if (xpp.getName().equalsIgnoreCase("description") && dentroItem)
                        actual.setDescription(xpp.nextText());
                    else if (xpp.getName().equalsIgnoreCase("pubDate") && dentroItem)
                        actual.setPubDate(xpp.nextText());
                    break;

                case XmlPullParser.END_TAG:
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        dentroItem = false;
                        noticias.add(actual);
                    }
                    break;
            }

            eventType = xpp.next();
        }

        //devolver el array de noticias
        return noticias;
    }

    public static void crearXML(ArrayList<Noticia> noticias, String fichero) throws IOException {
        FileOutputStream fout;
        fout = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fichero));
        XmlSerializer serializer = Xml.newSerializer();

        serializer.setOutput(fout, "UTF-8");
        serializer.startDocument(null, true);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true); //poner tabulación
        serializer.startTag(null, "titulares");

        for (int i = 0; i < noticias.size(); i++) {
            serializer.startTag(null, "item");
            serializer.startTag(null, "titulo");
            serializer.attribute(null, "fecha", noticias.get(i).getPubDate().toString());
            serializer.text(noticias.get(i).getTitle().toString());
            serializer.endTag(null, "titulo");
            serializer.startTag(null, "enlace");
            serializer.text(noticias.get(i).getLink().toString());
            serializer.endTag(null, "enlace");
            serializer.startTag(null, "descripcion");
            serializer.text(noticias.get(i).getDescription().toString());
            serializer.endTag(null, "descripcion");
            serializer.endTag(null, "item");
        }

        serializer.endTag(null, "titulares");
        serializer.endDocument();
        serializer.flush();
        fout.close();
    }
}
