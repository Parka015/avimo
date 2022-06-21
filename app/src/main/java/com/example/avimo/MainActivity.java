package com.example.avimo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements Regex {
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText input;
    private EditText output;
    private String respuesta;
    private ImageView micButton;
    private String data;
    private TextView textView;

    private TextToSpeech tts;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        input = findViewById(R.id.text);
        output = findViewById(R.id.avimo_text);
        micButton = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);


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
            }

            @Override
            public void onResults(Bundle bundle) {
                data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).toString().toLowerCase();
                input.setText(data);
                textView.setVisibility(View.INVISIBLE);
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
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    micButton.setImageResource(R.drawable.microfono);
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

        tts.setPitch(.8f);
        tts.setSpeechRate(.8f);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
    }

//AQUI retocar main que lo he comentado para solo ejecutar crear evento
    public void main(){
        input.setText("Tu has dicho: "+data);
        respuesta="";

        /*
        if (data.toString().toUpperCase().equals("[CREAR EVENTO]"))
        {
            //exito=crearEvento(...)
            //if(exito == true)
            tts.speak(getString(R.string.evento_creado).trim(), TextToSpeech.QUEUE_ADD, null);
            //else tts.speak(getString(R.string.evento_no_creado).trim(), TextToSpeech.QUEUE_ADD, null);
        }
        else if (data.toString().toUpperCase().equals("[LISTAR EVENTO]")){

            //exito=Evento(...)
            //if(exito == true)
            tts.speak("Listar evento".trim(), TextToSpeech.QUEUE_ADD, null);
            //else tts.speak(getString(R.string.evento_no_creado).trim(), TextToSpeech.QUEUE_ADD, null);

        }
        else tts.speak("No entiendo".trim(), TextToSpeech.QUEUE_ADD, null);
        */


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
        crearEvento();

    }

    public Boolean crearEvento(){

        Boolean exito=false;

        ArrayList<Calendar> fechas = recogerFecha(data);
        //String titulo = recogerTitulo(data);
        //ArrayList<String> tags = recogerTags(data);
        //String localizacion = regogerLocalizacion(data);
    /*
        if(respuesta.isEmpty()){
            Evento ev = new Evento();
            exito=true;
            //tengo que pensar como darme cuenta de si tengo que porne 1 a ALL_DAY
            //exito = ev.crearEvento("String title", 1, 1, "String loc", "String tags", 0, 0);

            if(exito){
                respuesta += getString(R.string.evento_creado);
            }else{
                respuesta += getString(R.string.evento_no_creado);
            }
        }
        */

        tts.speak(respuesta.trim(), TextToSpeech.QUEUE_ADD, null);
        output.setText(respuesta);

        return exito;
    }


    // Nota: Dependiente de la gramática
    public ArrayList<Calendar> recogerFecha(String dat){

        ArrayList <Calendar> result= new ArrayList<>();

        Pattern pattern_fecha = Pattern.compile(fecha_franja);
        Matcher m;
        m = pattern_fecha.matcher(dat);

        String fecha;   //Contendrá la parte de dat que sea una fecha

        if(m.find()){       //Se ha proporcionado una fecha franja

            //Separar las 2 fechas

            fecha = m.group();

            String [] separador = fecha.split(" (hasta|al)( el)? ");

            String fecha_fin = separador[1];    //fecha_franja_aux2
            String fecha_ini = separador[0];    //"(de|desde|del)( el)? "+fecha_franja_aux1

            separador = null;

            m = Pattern.compile(fecha_franja_aux1).matcher(fecha_ini);
            m.find();

            fecha_ini = m.group();      //fecha_franja_aux1

            //Formato <dia> del <numero_mes> del <año>
            String fini = procesarFecha(fecha_ini);
            //String = procesarHora(fecha_ini)

            String ffin = procesarFecha(fecha_fin);
            //String = procesarHora(fecha_fin)

            //AQUI BORRAR
            respuesta += "Fecha inicial: "+fini+" Fecha final: "+ffin;

        }else{
            m = Pattern.compile(fecha_unica).matcher(dat);
            if(m.find()){   //Se ha proporcionado una fecha única

                fecha = m.group();

                String fini = procesarFecha(fecha);
                //String = procesarHora(fecha_ini)
                //AQUI por desarrollar

                //AQUI BORRAR
                respuesta += "Fecha: "+fini;
            }
            else
                respuesta += getString(R.string.fecha_no_encontrada);
        }

        return result;
    }


    /*
    Devuelve la fecha contenida en el parámetro fecha con formato "<dia> del <numero_mes> del <año>"
    return dia=[1,99] mes[-2,11] anio=[-1,9999]
        * si el dia introducido es 0 se modificará a 1 del mes que corresponda
        * si mes o anio es -1 es porque no se ha especificado
        * si mes o dia es -2 el dia o mes no es correcto
     */
    private String procesarFecha(String fecha){

        String resultado = "";
        int mes = -1;
        int dia = -1;
        int anio = -1;

        Matcher m;
        m = Pattern.compile("("+adv_tiempo+"|("+dia_semana+"( "+info_adicional1+"| "+info_adicional2+")?)|"+dia_mes_anio_aux+")").matcher(fecha);
        m.find();
        fecha = m.group(); //Se elimina hora_ini si la tuviera

        m =  Pattern.compile(adv_tiempo).matcher(fecha);
        if(m.find()) {       //Se ha proporcionado un advervio de tiempo (hoy, mañana o pasado mañana)

            mes = Calendar.getInstance().get(Calendar.MONTH);
            dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            anio = Calendar.getInstance().get(Calendar.YEAR);

            if (fecha.equals("mañana")) {
                dia++;
            } else if (fecha.equals("pasado mañana")) {
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
                    case "miércoles": dia_semana_introducido = 4; break;
                    case "jueves": dia_semana_introducido = 5; break;
                    case "viernes": dia_semana_introducido = 6; break;
                    case "sábado": dia_semana_introducido = 7; break;
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

                m = Pattern.compile(dia_mes_anio_aux).matcher(fecha);
                if (m.find()) {      //Se ha proporcionado un dia_mes_anio_aux (el 5 de enero...)

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
                } // FIN -> Se ha proporcionado un dia_mes_anio_aux (el 5 de enero...)
            }
        }

        resultado = dia + " del " + mes + " del " + anio;
        //fuera de los if
        return resultado;
    }




}