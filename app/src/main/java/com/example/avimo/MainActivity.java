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
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText input;
    private EditText output;
    private ImageView micButton;
    private ArrayList<String> data;
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
                data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                input.setText(data.get(0));
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

    public void main(){

        input.setText("Tu has dicho: "+data.toString());

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



    }
}