package com.example.avimo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements Regex {

    // AUDIO
    public static final int RECORD_AUDIO_REQUEST_CODE = 1;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_READ_CALENDAR = 123;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech tts;
    private Boolean audio_activo=false;

    // OBJETOS DE SALIDA Y ENTRADA DE TEXTO EN LA PANTALLA
    private EditText input;
    private EditText output;

    // SALIDA Y ENTRADA DE TEXTO
    private String respuesta;
    private String data;

    // OTROS OBJETOS
    private ImageView micButton;
    private ImageView img_avimo;
    private TextView textView;

    // Variables de modificarEvento
    private String id_evento;
    private Boolean modificando;

    // Variables de eliminarEvento
    private Boolean eliminando;

    //Gestor de calendario
    GestorEventos gev;




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermissionRecord();
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED){
            checkPermissionWriteCalendar();
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED){
            checkPermissionReadCalendar();
        }

        input = findViewById(R.id.text);
        output = findViewById(R.id.avimo_text);

        micButton = findViewById(R.id.button);
        micButton.setImageResource(R.drawable.microfono);

        img_avimo = findViewById(R.id.img_avimo);
        img_avimo.setImageResource(R.drawable.normal);


        textView = findViewById(R.id.textView);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        // Variables de modificarEvento
        id_evento = "";
        modificando = false;

        // Variables de modificarEvento
        eliminando = false;

        gev = new GestorEventos();


        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                textView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float v) {}

            @Override
            public void onBufferReceived(byte[] bytes) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int i) {
                tts.speak(getString(R.string.error_escucha).trim(), TextToSpeech.QUEUE_ADD, null);
                textView.setVisibility(View.INVISIBLE);
                audio_activo=false;
            }

            @Override
            public void onResults(Bundle bundle) {
                data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).toString().toLowerCase();
                input.setText(data);
                textView.setVisibility(View.INVISIBLE);
                audio_activo=false;
                if(data != "")
                    main();
            }

            @Override
            public void onPartialResults(Bundle bundle) {}

            @Override
            public void onEvent(int i, Bundle bundle) {}
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (audio_activo){
                    audio_activo=false;
                    speechRecognizer.stopListening();
                }else{
                    audio_activo=true;
                    speechRecognizer.startListening(speechRecognizerIntent);
                }

                return false;
            }
        });

        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status)
            {
                tts.setLanguage(Locale.ROOT);
            }
        });

        tts.setPitch(1.0f);
        tts.setSpeechRate(0.9f);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void checkPermissionRecord() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RECORD_AUDIO_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RECORD_AUDIO_REQUEST_CODE && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permiso de micr??fono aceptado",Toast.LENGTH_SHORT).show();
        }else
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_READ_CALENDAR && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permiso de calendario aceptado",Toast.LENGTH_SHORT).show();
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermissionWriteCalendar(){

        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) MainActivity.this, Manifest.permission.WRITE_CALENDAR)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write calendar permission is necessary to write event!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity)MainActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_READ_CALENDAR);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity)MainActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_READ_CALENDAR);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermissionReadCalendar(){

        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) MainActivity.this, Manifest.permission.READ_CALENDAR)) {

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Read calendar permission is necessary to read events!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity)MainActivity.this, new String[]{Manifest.permission.READ_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_READ_CALENDAR);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {

                    ActivityCompat.requestPermissions((Activity)MainActivity.this, new String[]{Manifest.permission.READ_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_READ_CALENDAR);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }



    public void main(){

        if(checkPermissionWriteCalendar() && checkPermissionReadCalendar()) {
            input.setText("Tu has dicho: " + data);
            respuesta = "";

            img_avimo.setImageResource(R.drawable.normal);

            Pattern pattern_fecha = Pattern.compile(regex_eliminar_evento);
            Matcher m = pattern_fecha.matcher(data);

            if ((m.find() && !modificando) || eliminando) {      // ELIMINAR EVENTO

                eliminarEvento();

            } else {

                m = Pattern.compile(regex_crear_evento).matcher(data);

                if (m.find() && !modificando) {      //CREAR EVENTO

                    crearEvento();

                } else {

                    m = Pattern.compile(regex_listar_evento).matcher(data);
                    if (m.find() && !modificando) {     //LISTAR EVENTO

                        listarEventos();

                    } else {
                        m = Pattern.compile(regex_modificar_evento).matcher(data);
                        if (m.find() || modificando) {     //MODIFICAR EVENTO

                            modificarEvento();

                        } else {

                            m = Pattern.compile(regex_buscar_evento).matcher(data);
                            if (m.find()) {     //BUSCAR EVENTO

                                String tit = recogerTitulo(m.group(), false);
                                buscarEvento(tit);
                                output.setText(respuesta);

                            } else {

                                m = Pattern.compile("ayuda").matcher(data);
                                if (m.find()) {             //COMANDO AYUDA EVENTO
                                    comandoAyuda(data);
                                } else {       //COMANDO MAS AYUDA EVENTO
                                    m = Pattern.compile("m??s ayuda").matcher(data);
                                    if (m.find()) {
                                        comandoMasAyuda(data);
                                    } else {      //COMANDO MAS EJEMPLOS EVENTO
                                        m = Pattern.compile("m??s ejemplos").matcher(data);
                                        if (m.find()) {
                                            comandoEjemplos(data);
                                        } else{
                                            tts.speak("No entiendo".trim(), TextToSpeech.QUEUE_ADD, null);
                                            img_avimo.setImageResource(R.drawable.error);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            /*
            Pattern pattern_fecha = Pattern.compile(regex_fecha);

            Matcher m;

            m = pattern_fecha.matcher(data);

            if(m.find()){
                tts.speak("Se ha encontrado una fecha".trim(), TextToSpeech.QUEUE_ADD, null);
                output.setText("Se ha encontrado una fecha -> " + m.group());
            }
            else{
                tts.speak("No se ha encontrado una fecha".trim(), TextToSpeech.QUEUE_ADD, null);
                output.setText("No se ha encontrado una fecha ");
            }
            */
        }else
            output.setText(getString(R.string.error_permisos));

    }

    public void crearEvento(){

        Boolean exito=false;

        ArrayList<Calendar> fechas = recogerFechaCrearEvento(data, true);
        String titulo = recogerTitulo(data, true);
        String tags = recogerTags(data);
        String localizacion = recogerLocalizacion(data);

        if(!fechas.isEmpty() && titulo != "" ){
            exito=false;

            int all_day = 0;

            //Si al fecha de inicio es a las 0:00 y la de fin a las 23:59 ponemos el flag ALL_DAY a 1
            /*
            if (fechas.get(0).get(Calendar.HOUR_OF_DAY)==0 && fechas.get(0).get(Calendar.MINUTE)==0 &&
                    fechas.get(1).get(Calendar.HOUR_OF_DAY)==23 && fechas.get(1).get(Calendar.MINUTE)==59) {
                all_day = 1;
                fechas.get(0).set(Calendar.DAY_OF_MONTH,Calendar.DAY_OF_MONTH+1);   //Porque por alguna extra??a raz??n al poner el flag all_day a 1
                fechas.get(1).set(Calendar.DAY_OF_MONTH,Calendar.DAY_OF_MONTH+1);   // retrasa un dia los eventos
            }
            */

            long start_date = fechas.get(0).getTimeInMillis();
            long finish_date = fechas.get(1).getTimeInMillis();
            exito = gev.crearEvento(this,titulo, start_date, finish_date, localizacion, tags, all_day, 1);

            if(exito){
                String min_ini = ""+fechas.get(0).get(Calendar.MINUTE);
                String min_fin = ""+fechas.get(1).get(Calendar.MINUTE);
                int mes_ini = fechas.get(0).get(Calendar.MONTH)+1;
                int mes_fin = fechas.get(1).get(Calendar.MONTH)+1;

                if(fechas.get(0).get(Calendar.MINUTE) < 10)
                    min_ini = ""+0+fechas.get(0).get(Calendar.MINUTE);
                if(fechas.get(1).get(Calendar.MINUTE) < 10)
                    min_fin = ""+0+fechas.get(1).get(Calendar.MINUTE);

                respuesta += "Fecha inicial: "+fechas.get(0).get(Calendar.DAY_OF_MONTH)+"/"+ mes_ini+
                        "/"+ fechas.get(0).get(Calendar.YEAR)+" a las "+fechas.get(0).get(Calendar.HOUR_OF_DAY)+":"+min_ini+
                        " Fecha final: "+ fechas.get(1).get(Calendar.DAY_OF_MONTH)+"/"+ mes_fin+
                        "/"+ fechas.get(1).get(Calendar.YEAR)+" a las "+fechas.get(1).get(Calendar.HOUR_OF_DAY)+":"+min_fin+".\n";
                respuesta += getString(R.string.evento_creado);

                img_avimo.setImageResource(R.drawable.exito);
            }else{
                respuesta += getString(R.string.evento_no_creado);
                img_avimo.setImageResource(R.drawable.error);
            }
        }else
            img_avimo.setImageResource(R.drawable.error);

        tts.speak(respuesta.trim(), TextToSpeech.QUEUE_ADD, null);
        output.setText(respuesta);
    }


    public void listarEventos(){

        ArrayList<Calendar> fechas = recogerFechaListarEvento(data);

        ArrayList<String> fechas_ini = new ArrayList<>();
        ArrayList<String> nombres_eventos = new ArrayList<>();

        gev.listarEventos(this, fechas, nombres_eventos, fechas_ini);

        if(!nombres_eventos.isEmpty()) {

            Calendar calendar;
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            String minutos;
            Calendar dia = Calendar.getInstance();
            dia.set(Calendar.DAY_OF_YEAR, dia.get(Calendar.DAY_OF_YEAR)-1);


            for (int i = 0; i < nombres_eventos.size(); i++) {

                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(fechas_ini.get(i)));


                if (dia.get(Calendar.DAY_OF_YEAR) != calendar.get(Calendar.DAY_OF_YEAR)) {
                    tts.speak("\nEl " + formatter.format(calendar.getTime()) + " tiene los eventos:\n".trim(), TextToSpeech.QUEUE_ADD, null);
                    respuesta += "\nEl " + formatter.format(calendar.getTime()) + " tiene los eventos:\n";
                    dia.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR));
                }

                minutos = "" + calendar.get(Calendar.MINUTE);
                if (calendar.get(Calendar.MINUTE) < 10)
                    minutos = "0" + calendar.get(Calendar.MINUTE);

                tts.speak(nombres_eventos.get(i) + " a las " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + minutos + "\n".trim(), TextToSpeech.QUEUE_ADD, null);
                respuesta += nombres_eventos.get(i) + " a las " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + minutos + "\n";

            }

            img_avimo.setImageResource(R.drawable.exito);

        }else{

            img_avimo.setImageResource(R.drawable.error);

            tts.speak(getString(R.string.no_hay_eventos).trim(), TextToSpeech.QUEUE_ADD, null);
            respuesta += getString(R.string.no_hay_eventos);

        }

        output.setText(respuesta);

    }


    public void modificarEvento(){

        if(!data.equals("[si]")) {

            if(!modificando){

                String tit_ev = recogerTitulo(data, false);

                String identificador = buscarEvento(tit_ev);  //notificar desde dentro de la funcion el nombre y la fecha de inicio del evento

                if (identificador != ""){


                    //Notificar de que ya se puede especificar que atributos del evento cambiar
                    tts.speak(getString(R.string.modificar_evento_encontrado).trim(), TextToSpeech.QUEUE_ADD, null);
                    respuesta += ".\n"+getString(R.string.modificar_evento_encontrado);

                    modificando = true;
                    id_evento = identificador;

                }

            }else {

                ArrayList<Calendar> fechas = recogerFechaCrearEvento(data, false);
                String titulo = recogerTitulo(data, false);
                String tags = recogerTags(data);
                String localizacion = recogerLocalizacion(data);

                if (fechas.isEmpty() && titulo == "" && tags == "" && localizacion == "") {

                    img_avimo.setImageResource(R.drawable.error);

                    tts.speak(getString(R.string.modificar_evento_ningun_cambio).trim(), TextToSpeech.QUEUE_ADD, null);
                    respuesta += getString(R.string.modificar_evento_ningun_cambio);

                }else {

                    // realizar los cambios

                    long startMillis = -1;
                    long endMillis = -1;

                    if(fechas.size() == 2){
                        startMillis = fechas.get(0).getTimeInMillis();
                        endMillis = fechas.get(1).getTimeInMillis();
                    }

                    gev.modificarEvento(this,id_evento,titulo,startMillis,
                            endMillis, localizacion, tags);

                    //Notificar de los cambios
                    if(titulo !=""){
                        tts.speak(getString(R.string.cambio_titulo)+" \""+titulo+"\".".trim(), TextToSpeech.QUEUE_ADD, null);
                        respuesta += getString(R.string.cambio_titulo)+" \""+titulo+"\".";
                    }

                    if(fechas.size()==2){

                        String minutos = "" + fechas.get(0).get(Calendar.MINUTE);
                        if (fechas.get(0).get(Calendar.MINUTE) < 10)
                            minutos = "0" + fechas.get(0).get(Calendar.MINUTE);

                        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                        tts.speak(getString(R.string.cambio_fecha)+" "+formatter.format(fechas.get(0).getTime())+
                                " a las " + fechas.get(0).get(Calendar.HOUR_OF_DAY)
                                + ":" + minutos+".".trim(), TextToSpeech.QUEUE_ADD, null);

                        respuesta += getString(R.string.cambio_fecha)+" "+formatter.format(fechas.get(0).getTime())+
                                " a las " + fechas.get(0).get(Calendar.HOUR_OF_DAY) + ":" + minutos+".";
                    }

                    if(localizacion !=""){
                        tts.speak(getString(R.string.cambio_localizacion)+" \""+localizacion+"\".".trim(), TextToSpeech.QUEUE_ADD, null);
                        respuesta += getString(R.string.cambio_localizacion)+" \""+localizacion+"\".";
                    }

                    if(tags !=""){
                        tts.speak(getString(R.string.cambio_tag)+" \""+tags+"\".".trim(), TextToSpeech.QUEUE_ADD, null);
                        respuesta += getString(R.string.cambio_tag)+" \""+tags+"\".";
                    }



                    //Preguntar si qui quiere modificar algo m??s, en caso contrario decir no

                    tts.speak(getString(R.string.modificar_evento_pregunta_final).trim(), TextToSpeech.QUEUE_ADD, null);
                    respuesta += getString(R.string.modificar_evento_pregunta_final);

                    img_avimo.setImageResource(R.drawable.exito);

                }


            }

        }else{
            //Notificar fin de modificar
            tts.speak(getString(R.string.modificar_evento_fin).trim(), TextToSpeech.QUEUE_ADD, null);
            respuesta += getString(R.string.modificar_evento_fin);
            modificando = false;
            id_evento = "";
        }

        output.setText(respuesta);
    }


    public void eliminarEvento(){

        if (!eliminando) {
            String tit_ev = recogerTitulo(data, false);

            id_evento = buscarEvento(tit_ev);  //notificar desde dentro de la funcion el nombre y la fecha de inicio del evento
            if(id_evento != "") {

                eliminando = true;

                //Notificar si se debe borrar el evento
                tts.speak(getString(R.string.eliminar_evento_encontrado).trim(), TextToSpeech.QUEUE_ADD, null);
                respuesta += ".\n"+getString(R.string.eliminar_evento_encontrado);
            }
        }
        else{

            if(data.equals("[si]")){

                img_avimo.setImageResource(R.drawable.exito);


                gev.eliminarEvento(this,id_evento);

                tts.speak(getString(R.string.evento_eliminado).trim(), TextToSpeech.QUEUE_ADD, null);
                respuesta += getString(R.string.evento_eliminado);

                id_evento = "";
                eliminando = false;

            }else if(data.equals("[no]")){

                img_avimo.setImageResource(R.drawable.exito);

                tts.speak(getString(R.string.evento_no_eliminado).trim(), TextToSpeech.QUEUE_ADD, null);
                respuesta += getString(R.string.evento_no_eliminado);

                id_evento = "";
                eliminando = false;

            }else{

                img_avimo.setImageResource(R.drawable.error);

                tts.speak(getString(R.string.eliminar_evento_no_respondido).trim(), TextToSpeech.QUEUE_ADD, null);
                respuesta += getString(R.string.eliminar_evento_no_respondido);

            }

        }

        output.setText(respuesta);
    }


    public String buscarEvento(String titulo){

        Calendar cal_ini = Calendar.getInstance();

        String id = gev.buscarEvento(this, titulo, cal_ini);

        if(id != ""){

            String minutos = "" + cal_ini.get(Calendar.MINUTE);
            if (cal_ini.get(Calendar.MINUTE) < 10)
                minutos = "0" + cal_ini.get(Calendar.MINUTE);

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            tts.speak(getString(R.string.evento_encontrado)+" "+titulo+
                    " el "+ formatter.format(cal_ini.getTime())+
                    " a las " + cal_ini.get(Calendar.HOUR_OF_DAY) + ":" + minutos.trim(), TextToSpeech.QUEUE_ADD, null);

            respuesta += getString(R.string.evento_encontrado)+" "+titulo+
                    " el "+formatter.format(cal_ini.getTime())+
                    " a las " + cal_ini.get(Calendar.HOUR_OF_DAY) + ":" + minutos;

            img_avimo.setImageResource(R.drawable.exito);


        }else{

            tts.speak(getString(R.string.evento_no_encontrado)+" "+titulo.trim(), TextToSpeech.QUEUE_ADD, null);
            respuesta += getString(R.string.evento_no_encontrado)+" "+titulo;
            img_avimo.setImageResource(R.drawable.error);
        }

        return id;

    }


    // Nota: Dependiente de la gram??tica
    public ArrayList<Calendar> recogerFechaCrearEvento(String dat, Boolean verbose){

        ArrayList <Calendar> result= new ArrayList<>();

        Pattern pattern_fecha = Pattern.compile(fecha_franja);
        Matcher m;
        m = pattern_fecha.matcher(dat);

        String fecha;   //Contendr?? la parte de dat que sea una fecha

        int dia_ini,mes_ini,anio_ini,h_i,min_i;
        dia_ini=mes_ini=anio_ini=h_i=min_i=-1;

        int dia_fin,mes_fin,anio_fin,h_f,min_f;
        dia_fin=mes_fin=anio_fin=h_f=min_f=-1;

        if(m.find()){       //Se ha proporcionado una fecha franja

            //Separar las 2 fechas

            fecha = m.group();

  //          String [] separador = fecha.split(" (hasta|al)( el)? ");
            //Nueva forma de obtener las fecha_franja_aux
            m = Pattern.compile(fecha_franja_aux).matcher(fecha);
            m.find();
            String fecha_ini = m.group();
            m.find();
            String fecha_fin = m.group();

            int [] fini = procesarFechaCrearEvento(fecha_ini);
            int [] h_ini= recogerHora(fecha_ini);

            int [] ffin = procesarFechaCrearEvento(fecha_fin);
            int [] h_fin= recogerHora(fecha_fin);

            Boolean valido=true;


            //----- Combinar info fecha ------//

            //Establecer dia
            dia_ini = fini[0];
            dia_fin = ffin[0];

            //Establecer mes
            if(ffin[1] ==-2 )   //mes incorrecto en la fecha final
                valido = false;
            else if (fini[1] == -1 && ffin[1] == -1)
                mes_fin = mes_ini = Calendar.getInstance().get(Calendar.MONTH);
            else if(fini[1] ==-1) //ausencia del mes en la fecha inicio
                mes_fin = mes_ini = ffin[1];
            else if(ffin[1] ==-1) //ausencia del mes en la fecha final
                mes_fin = mes_ini = fini[1];
            else{
                mes_ini = fini[1];
                mes_fin = ffin[1];
            }

            if(valido) {

                //Establecer a??o
                if (fini[2] == -1 && ffin[2] == -1) {   //No se a proporcionado a??o en ninguna fecha
                    anio_fin = anio_ini = Calendar.getInstance().get(Calendar.YEAR);
                } else if (fini[2] == -1)
                    anio_fin = anio_ini = ffin[2];
                else if (ffin[2] == -1)
                    anio_fin = anio_ini = fini[2];
                else {
                    anio_ini = fini[2];
                    anio_fin = ffin[2];
                }


                //----- Combinar info hora ------//


                if(h_ini != null){
                    h_i = h_ini[0];
                    min_i = h_ini[1];

                    if(h_i == -1)   //Si la hora no esta especificada
                        h_i = 0;
                    if(min_i == -1)
                        min_i = 0;
                }
                else {
                    h_i = 0;
                    min_i = 0;
                }

                if(h_fin != null){
                    h_f = h_fin[0];
                    min_f = h_fin[1];

                    if (h_f == -1 && min_f == -1)
                        min_f = 59;

                    if(h_f == -1)   //Si la hora no esta especificada
                        h_f = 23;
                    else if(min_f == -1)
                        min_f = 0;


                }else {
                    h_f = 23;
                    min_f = 59;
                }


                //----- Comprobar que la fecha inicial es anterior a la fecha final ------//

                if(anio_ini > anio_fin)
                    valido = false;
                else if (anio_ini == anio_fin)
                    if (mes_ini > mes_fin)
                        valido = false;
                    else if (mes_ini == mes_fin)
                        if (dia_ini > dia_fin)
                            valido = false;
                        else if (dia_ini == dia_fin)
                            if(h_i > h_f)
                                valido = false;
                            else if(h_i == h_f)
                                if (min_i > min_f)
                                    valido = false;

            }

            if(valido) {
                Calendar cal_ini = Calendar.getInstance();
                cal_ini.set(anio_ini, mes_ini, dia_ini, h_i, min_i);
                result.add(cal_ini);

                Calendar cal_fin = Calendar.getInstance();
                cal_fin.set(anio_fin, mes_fin, dia_fin, h_f, min_f);
                result.add(cal_fin);

            }
            else
                respuesta += getString(R.string.fini_mayor_ffin);


        }else{
            m = Pattern.compile(regex_titulo).matcher(dat); //Para evitar que tome como fechas posibles numeros en el titulo
            if(m.find())
                dat.replace(m.group(),"");

            m = Pattern.compile(regex_localizacion).matcher(dat);   //Para evitar que tome como fechas posibles numeros en la loc
            if(m.find())
                dat.replace(m.group(),"");

            m = Pattern.compile(fecha_unica).matcher(dat);
            if(m.find()){   //Se ha proporcionado una fecha ??nica

                fecha = m.group();

                int [] fec = procesarFechaCrearEvento(fecha);
                int [] time= recogerHora(fecha);

                Boolean valido=true;


                //----- Combinar info fecha ------//
                dia_ini = fec[0];
                mes_ini = fec[1];
                anio_ini = fec[2];

                if(mes_ini == -2)
                    valido = false;
                else if(mes_ini == -1)
                    mes_ini = Calendar.getInstance().get(Calendar.MONTH);

                if(valido){

                    if(anio_ini == -1)
                        anio_ini = Calendar.getInstance().get(Calendar.YEAR);

                    //----- Combinar info hora ------//
                    if(time != null){
                        if(time[0] > time[2])
                            valido = false;
                        else if (time[0] == time[2]){
                            if(time[1] > time[3])
                                valido = false;
                        }

                        h_i = time[0];
                        min_i = time[1];

                        h_f = time[2];
                        min_f = time[3];

                        if(min_i == -1)
                            min_i = 0;
                        if(min_f == -1)
                            min_f = 0;

                    }else{

                        h_i = 0;
                        min_i = 0;

                        h_f = 23;
                        min_f = 59;

                    }

                    if(valido){

                        Calendar cal_ini = Calendar.getInstance();
                        cal_ini.set(anio_ini, mes_ini, dia_ini, h_i, min_i);
                        result.add(cal_ini);

                        Calendar cal_fin = Calendar.getInstance();
                        cal_fin.set(anio_ini, mes_ini, dia_ini, h_f, min_f);
                        result.add(cal_fin);

                    }

                }
                if(!valido)
                    respuesta += getString(R.string.fini_mayor_ffin);

            }else if(verbose)
                respuesta += getString(R.string.fecha_no_encontrada);

        }

        return result;
    }


    public ArrayList<Calendar> recogerFechaListarEvento(String dat) {
        ArrayList <Calendar> result= new ArrayList<>();

        result = recogerFechaCrearEvento(dat,false);

        if(result.isEmpty()){

            Pattern pattern_fecha = Pattern.compile(info_adicional3);
            Matcher m;
            m = pattern_fecha.matcher(dat);


            if(m.find()){   // info_adicional3

                int [] fecha = procesarFechaListarEvento(m.group());


                Calendar cal_ini = Calendar.getInstance();
                cal_ini.set(fecha[2], fecha[1], fecha[0], 0, 0);
                result.add(cal_ini);

                Calendar cal_fin = Calendar.getInstance();
                cal_fin.set(fecha[5], fecha[4], fecha[3], 23, 59);
                result.add(cal_fin);


            }else{

                m = Pattern.compile(info_adicional4).matcher(dat);

                if(m.find()){       // info_adicional4

                    int [] fecha = procesarFechaListarEvento(m.group());

                    Calendar cal_ini = Calendar.getInstance();
                    cal_ini.set(fecha[2], fecha[1], fecha[0], 0, 0);
                    result.add(cal_ini);

                    Calendar cal_fin = Calendar.getInstance();
                    cal_fin.set(fecha[5], fecha[4], fecha[3], 23, 59);
                    result.add(cal_fin);

                }

            }

        }

        return result;
    }


    /*
    return resultado[0]=hora inicio | inicio resultado[1]=min inicio | inicio resultado[2]=hora fin | resultado[3]=min fin
    En caso de no haber hora devuelve null
     */
    private int [] recogerHora(String fecha){

        int [] resultado = null;

        Matcher m;
        m = Pattern.compile(hora_franja).matcher(fecha);

        String hora_inicio="";
        String hora_fin="";

        int [] h_ini=null;
        int [] h_fin=null;

        if(m.find()) {       //Se ha proporcionado una hora franja

            resultado = new int[4];

            //Quedarme con las 2 horas
            fecha = m.group();

            String[] separador = fecha.split(" (hasta|a)( las| la)? ");

            hora_fin = separador[1];    //hora2
            hora_inicio = separador[0];    //"(desde|de)( las| la)? "+hora1

            separador = null;

            m = Pattern.compile(hora).matcher(hora_inicio);
            m.find();

            hora_inicio = m.group();      //hora1

            h_ini = procesarHora(hora_inicio);
            h_fin = procesarHora(hora_fin);

            resultado[0] = h_ini[0];
            resultado[1] = h_ini[1];
            resultado[2] = h_fin[0];
            resultado[3] = h_fin[1];


        }   // FIN -> Se ha proporcionado una hora franja
        else{

            m = Pattern.compile(hora_ini).matcher(fecha);
            if(m.find()){
                resultado = new int[4];

                //Me quedo con la hora_ini
                fecha = m.group();

                m = Pattern.compile(hora).matcher(fecha);
                m.find();

                //Quedarme solo la hora
                hora_inicio = m.group();

                h_ini = procesarHora(hora_inicio);
                h_fin = new int [2];
                h_fin[0] = 23;
                h_fin[1] = 59;

                resultado[0] = h_ini[0];
                resultado[1] = h_ini[1];
                resultado[2] = h_fin[0];
                resultado[3] = h_fin[1];

            } // FIN -> Se ha proporcionado una hora unica
        }

        return resultado;
    }


    /*
    return h=[0,23] min=[0,59]
     */
    private int [] procesarHora(String hora){

        int h = -1;
        int min = -1;

        Matcher m;

        //Obteniendo hora
        m = Pattern.compile("\\d{1,2}").matcher(hora);
        m.find();

        h = Integer.parseInt(m.group());

        if(h<0 || h>23) {
            h = 0;
            respuesta += getString(R.string.hora_no_valida);
        }

        //Obteniendo minutos
        m = Pattern.compile(info_adicional_minutos).matcher(hora);
        if(m.find()){   // Se ha proporcionado una "info_adicional_minutos" (y media, y cuarto, y 20...)

            switch (m.group()) {
                case "y media": min = 30; break;
                case "y cuarto": min = 15; break;
                case "menoscuarto": min = 45; h--; break;
                case "menos cuarto": min = 45; h--; break;
                default:
                    m = Pattern.compile("\\d{1,2}").matcher(hora);
                    m.find();
                    min = Integer.parseInt(m.group());
                    if(min<0 || min>59) {
                        min = 0;
                        respuesta += getString(R.string.min_no_valido);
                    }
                    break;
            }
        }       // FIN -> Se ha proporcionado una "info_adicional_minutos"
        else{
            m = Pattern.compile(":\\d\\d").matcher(hora);
            if(m.find()){   // FIN -> Se ha proporcionado una "info_adicional5" (:20, :43...)
                min = Integer.parseInt(m.group().split(":")[1]);
                if(min<0 || min>59) {
                    min = 0;
                    respuesta += getString(R.string.min_no_valido);
                }
            }
        }

        //AQUI falta meter el si se pide que sea de la tarde o noche
        m = Pattern.compile("de la (tarde|noche)").matcher(hora);
        if(m.find()) {
            if(h<12)
                h += 12;
        }


        int [] resultado = new int[2];
        resultado[0] = h;
        resultado[1] = min;

        return resultado;
    }


    /*
    Devuelve la fecha contenida en el par??metro fecha con formato
    resultado[0]=dia | inicio resultado[1]=mes | inicio resultado[2]=anio
    return dia=[1,99] mes[-2,11] anio=[-1,9999]
        * si el dia introducido es 0 se modificar?? a 1 del mes que corresponda
        * si mes o anio es -1 es porque no se ha especificado
        * si mes o dia es -2 el dia o mes no es correcto
     */
    private int [] procesarFechaCrearEvento(String fecha){

        int[] resultado = new int[3];
        int mes = -1;
        int dia = -1;
        int anio = -1;

        Matcher m;
        m = Pattern.compile("("+adv_tiempo+"|("+dia_semana+"( "+info_adicional1+"| "+info_adicional2+")?)|"+dia_mes_anio+")").matcher(fecha);
        m.find();
        fecha = m.group(); //Se elimina hora_ini si la tuviera

        m =  Pattern.compile(adv_tiempo).matcher(fecha);
        if(m.find()) {       //Se ha proporcionado un advervio de tiempo (hoy, ma??ana o pasado ma??ana)

            mes = Calendar.getInstance().get(Calendar.MONTH);
            dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            anio = Calendar.getInstance().get(Calendar.YEAR);

            if (fecha.equals("ma??ana")) {
                dia++;
            } else if (fecha.equals("pasado ma??ana")) {
                dia += 2;
            }
        }
        else {

            m = Pattern.compile(dia_semana).matcher(fecha);
            if (m.find()) {      //Se ha proporcionado un dia_semana (lunes, martes, etc.)

                int dia_semana_introducido = -1;

                switch (m.group()) {
                    case "lunes": dia_semana_introducido = 2; break;
                    case "martes": dia_semana_introducido = 3; break;
                    case "mi??rcoles": dia_semana_introducido = 4; break;
                    case "jueves": dia_semana_introducido = 5; break;
                    case "viernes": dia_semana_introducido = 6; break;
                    case "s??bado": dia_semana_introducido = 7; break;
                    case "domingo": dia_semana_introducido = 1; break;
                }

                int dia_semana_actual = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

                mes = Calendar.getInstance().get(Calendar.MONTH);
                dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                anio = Calendar.getInstance().get(Calendar.YEAR);

                m = Pattern.compile("("+info_adicional1+"|"+info_adicional2+")").matcher(fecha);
                //AQUI revisar
                if ((dia_semana_introducido < dia_semana_actual)) {    //Si el dia_semana_introducido ha pasado , cogemos el siguiente (semana siguiente)
                    dia = dia + dia_semana_introducido - dia_semana_actual + 7;  //Cogemos el proximo dia_semana_introducido
                    if(m.find() && dia_semana_introducido == 1)         //Si se especifica el domingo de la semana que viene siendo domingo
                        dia += 7;
                }else if(dia_semana_actual == 1){
                    dia = dia + dia_semana_introducido - dia_semana_actual;
                    if(m.find() && dia_semana_introducido == 1)         //Si se especifica el domingo de la semana que viene siendo domingo
                        dia += 7;
                }
                else {
                    dia = dia + dia_semana_introducido - dia_semana_actual;
                    if(m.find())
                        dia += 7;
                }

            } // FIN -> Se ha proporcionado un dia_semana (lunes, martes, etc.)
            else {

                m = Pattern.compile(dia_mes_anio).matcher(fecha);
                if (m.find()) {      //Se ha proporcionado un dia_mes_anio (el 5 de enero...)

                    String [] valores = fecha.split("(del|de)");

                    m = Pattern.compile("\\d{1,2}").matcher(valores[0]);
                    m.find();

                    dia = Integer.parseInt(m.group());

                    if(dia==0)dia++;

                    mes = -1;
                    anio = -1;

                    if(valores.length>=2){  //mes
                        m = Pattern.compile(numero_mes).matcher(valores[1]);
                        if(m.find()){

                            if(Integer.parseInt(m.group()) > 12 || Integer.parseInt(m.group()) < 1) {
                                respuesta += getString(R.string.mes_no_valido);
                                mes = -2;
                            }else
                                mes = Integer.parseInt(m.group()) - 1;
                        }
                        else{
                            m = Pattern.compile(nombre_mes).matcher(valores[1]);
                            if(m.find()){

                                switch (m.group()) {
                                    case "enero": mes = 0; break;
                                    case "febrero": mes = 1; break;
                                    case "marzo": mes = 2; break;
                                    case "abril": mes = 3; break;
                                    case "mayo": mes = 4; break;
                                    case "junio": mes = 5; break;
                                    case "julio": mes = 6; break;
                                    case "agosto": mes = 7; break;
                                    case "septiembre": mes = 8; break;
                                    case "octubre": mes = 9; break;
                                    case "noviembre": mes = 10; break;
                                    case "diciebre": mes = 11; break;
                                }
                            }
                        }
                    }
                    if (valores.length==3) {    //anio

                        m = Pattern.compile(anio_4).matcher(valores[2]);
                        if(m.find())
                            anio = Integer.parseInt(m.group());
                        else{
                            m = Pattern.compile(anio_2).matcher(valores[2]);
                            if(m.find())
                                anio = 2000 + Integer.parseInt(m.group());
                        }

                    }
                } // FIN -> Se ha proporcionado un dia_mes_anio (el 5 de enero...)
            }
        }
        //fuera de los if
        resultado[0] = dia;
        resultado[1] = mes;
        resultado[2] = anio;

        return resultado;
    }


    private int [] procesarFechaListarEvento(String fecha){

        int[] resultado = new int[6];
        int mes = -1;
        int dia_ini = -1;
        int dia_fin = -1;
        int anio = -1;

        Matcher m;
        m =  Pattern.compile(info_adicional3).matcher(fecha);

        if(m.find()) {       // esta semana


            int dias_hasta_domingo = 8 - Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 1)
                dias_hasta_domingo = 0;

            mes = Calendar.getInstance().get(Calendar.MONTH);
            dia_ini = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            anio = Calendar.getInstance().get(Calendar.YEAR);

            dia_fin = dia_ini + dias_hasta_domingo;


        }
        else {

            m =  Pattern.compile(info_adicional4).matcher(fecha);
            if(m.find()){       // siguiente semana

                int dias_hasta_fin_semana = 9 - Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 1)
                    dias_hasta_fin_semana = 1;


                mes = Calendar.getInstance().get(Calendar.MONTH);
                dia_ini = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + dias_hasta_fin_semana;    //lunes
                anio = Calendar.getInstance().get(Calendar.YEAR);

                dia_fin = dia_ini + 6;  //domingo


            }

        }


        //fuera de los if
        resultado[0] = dia_ini;
        resultado[1] = mes;
        resultado[2] = anio;
        resultado[3] = dia_fin;
        resultado[4] = mes;
        resultado[5] = anio;

        return resultado;
    }


    /*
    return "" en caso de no haber titulo, si lo hay, lo devuelve
     */
    public String recogerTitulo(String dat, Boolean verbose){

        String resultado = "";

        Matcher m;
        m = Pattern.compile(regex_titulo).matcher(dat);

        if(m.find()){

            //Nos quedamos con toda la parte del t??tulo
            dat = m.group();

            String [] tit = dat.split("(de|con) (t??tulo|nombre) ");

            dat = tit[1];   //(("+palabra+"|"+numero+") )+(fin|film)"

            tit = dat.split(" (fin|film)");

            dat = tit[0];   //(("+palabra+"|"+numero+") )

            resultado = dat;


        }else if (verbose)
            respuesta += getString(R.string.titulo_no_encontrado);

        return resultado;
    }


    /*
    return "" en caso de no haber tags, si lo hay, lo devuelve
     */
    public String recogerTags(String dat){

        String resultado = "";

        Matcher m;
        m = Pattern.compile(regex_tags).matcher(dat);

        if(m.find()) {

            //Nos quedamos con toda la parte del tag
            dat = m.group();

            String[] tag = dat.split("(tags|tag) ");

            dat = tag[1];   //(("+palabra+" )+y "+palabra+"|("+palabra+") (fin|film))

            tag = dat.split(" (fin|film)");

            dat = tag[0];   //(("+palabra+" )+y "+palabra+"|("+palabra+")

            resultado = dat;
        }

        return resultado;
    }


    /*
    return "" en caso de no haber tags, si lo hay, lo devuelve
     */
    public String recogerLocalizacion(String dat){

        String resultado = "";

        Matcher m;
        m = Pattern.compile(regex_localizacion).matcher(dat);

        if(m.find()) {

            //Nos quedamos con toda la parte del tag
            dat = m.group();

            String[] loc = dat.split("en ");

            dat = loc[1];   //(("+palabra+" )+y "+palabra+"|("+palabra+") (fin|film))

            loc = dat.split(" (fin|film)");

            dat = loc[0];   //(("+palabra+" )+y "+palabra+"|("+palabra+")

            resultado = dat;
        }

        return resultado;
    }


    public void comandoAyuda(String dat) {

        Matcher m;
        m = Pattern.compile(regex_ayuda_crear_evento).matcher(dat);

        if (m.find()) {   //AYUDA CREAR EVENTO

            tts.speak(getString(R.string.comando_ayuda_crear_evento).trim(), TextToSpeech.QUEUE_ADD, null);
            output.setText(getString(R.string.comando_ayuda_crear_evento));

        } else {
            m = Pattern.compile(regex_ayuda_listar_evento).matcher(dat);
            if(m.find()){   //AYUDA LISTAR EVENTO

                tts.speak(getString(R.string.comando_ayuda_listar_evento).trim(), TextToSpeech.QUEUE_ADD, null);
                output.setText(getString(R.string.comando_ayuda_listar_evento));

            } else {
                m = Pattern.compile(regex_ayuda_buscar_evento).matcher(dat);
                if (m.find()) {   //AYUDA BUSCAR EVENTO

                    tts.speak(getString(R.string.comando_ayuda_buscar_evento).trim(), TextToSpeech.QUEUE_ADD, null);
                    output.setText(getString(R.string.comando_ayuda_buscar_evento));

                } else {
                    m = Pattern.compile(regex_ayuda_eliminar_evento).matcher(dat);
                    if (m.find()) {   //AYUDA ELIMINAR EVENTO

                        tts.speak(getString(R.string.comando_ayuda_eliminar_evento).trim(), TextToSpeech.QUEUE_ADD, null);
                        output.setText(getString(R.string.comando_ayuda_eliminar_evento));
                    } else {
                        m = Pattern.compile(regex_ayuda_modificar_evento).matcher(dat);
                        if (m.find()) {   //AYUDA MODIFICAR EVENTO

                            tts.speak(getString(R.string.comando_ayuda_modificar_evento).trim(), TextToSpeech.QUEUE_ADD, null);
                            output.setText(getString(R.string.comando_ayuda_modificar_evento));
                        } else {

                            tts.speak(getString(R.string.comando_ayuda_general).trim(), TextToSpeech.QUEUE_ADD, null);
                            output.setText(getString(R.string.comando_ayuda_general));
                        }
                    }
                }
            }
        }

    }


    public void comandoMasAyuda(String dat){

        Matcher m;
        m = Pattern.compile(regex_mas_ayuda_crear_evento).matcher(dat);

        if(m.find()){   //MAS AYUDA CREAR EVENTO

            tts.speak(getString(R.string.comando_mas_ayuda_crear_evento_intro).trim(), TextToSpeech.QUEUE_ADD, null);
            tts.speak(getString(R.string.comando_mas_ayuda_crear_evento_fecha).trim(), TextToSpeech.QUEUE_ADD, null);
            tts.speak(getString(R.string.comando_mas_ayuda_crear_evento_titulo).trim(), TextToSpeech.QUEUE_ADD, null);
            tts.speak(getString(R.string.comando_mas_ayuda_crear_evento_localizacion).trim(), TextToSpeech.QUEUE_ADD, null);
            output.setText(getString(R.string.comando_mas_ayuda_crear_evento_intro)+getString(R.string.comando_mas_ayuda_crear_evento_fecha)+
                    getString(R.string.comando_mas_ayuda_crear_evento_titulo)+getString(R.string.comando_mas_ayuda_crear_evento_localizacion));

        }
        else{

            tts.speak(getString(R.string.comando_ayuda_general).trim(), TextToSpeech.QUEUE_ADD, null);
            output.setText(getString(R.string.comando_ayuda_general));

        }

    }


    public void comandoEjemplos(String dat){

        Matcher m;
        m = Pattern.compile(regex_mas_ejemplos_crear_evento).matcher(dat);

        if(m.find()){   //EJEMLOS CREAR EVENTO

            tts.speak(getString(R.string.comando_ejemplos_crear_evento_1).trim(), TextToSpeech.QUEUE_ADD, null);
            tts.speak(getString(R.string.comando_ejemplos_crear_evento_2).trim(), TextToSpeech.QUEUE_ADD, null);
            tts.speak(getString(R.string.comando_ejemplos_crear_evento_3).trim(), TextToSpeech.QUEUE_ADD, null);
            tts.speak(getString(R.string.comando_ejemplos_crear_evento_4).trim(), TextToSpeech.QUEUE_ADD, null);
            tts.speak(getString(R.string.comando_ejemplos_crear_evento_5).trim(), TextToSpeech.QUEUE_ADD, null);
            tts.speak(getString(R.string.comando_ejemplos_crear_evento_6).trim(), TextToSpeech.QUEUE_ADD, null);
            output.setText(getString(R.string.comando_ejemplos_crear_evento_1)+getString(R.string.comando_ejemplos_crear_evento_2)+
                    getString(R.string.comando_ejemplos_crear_evento_3)+getString(R.string.comando_ejemplos_crear_evento_4)+
                    getString(R.string.comando_ejemplos_crear_evento_5)+getString(R.string.comando_ejemplos_crear_evento_6));

        }
        else{

            //tts.speak(getString(R.string.comando_ayuda_general).trim(), TextToSpeech.QUEUE_ADD, null);
            //output.setText(getString(R.string.comando_ayuda_general));

        }

    }



}