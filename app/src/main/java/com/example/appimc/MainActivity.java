package com.example.appimc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    EditText taille;
    EditText pois;
    TextView reponce;
    MyFiles file;
    String cont = "";
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taille = findViewById(R.id.editTextT);
        pois = findViewById(R.id.editTextP);
        reponce = findViewById(R.id.textViewR);
        imageView = findViewById(R.id.imageView);



        file = new MyFiles(this);

        if(file.existe("save")) {
            cont = file.lireSimple("save");
        }else{
            file.ecrireFile(file.ouvrireFichier("save",false),"Ajouter un Pois et une taille");
            cont="Ajouter un pois et une taille";
        }

        if(cont.contains(";")){
            String tab[] = cont.split(";");
            if(tab.length==2) {
                taille.setText(tab[0]);
                pois.setText(tab[1]);
                controle();
            }else{
                reponce.setText("ERR");
            }
        }else{
            reponce.setText(cont);
        }

        taille.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controle();
            }
        });

        pois.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controle();
            }
        });


    }


    private void controle(){
        String ss = taille.getText().toString();
        if(ss.length()==0){
            ss = "0.0";
        }
        Float t = Float.parseFloat(ss);
        ss="";
        ss = pois.getText().toString();
        if(ss.length()==0){
            ss = "0.0";
        }
        Float p = Float.parseFloat(ss);
        if(t>0 && p>0){
            calculeIMC(t,p);
        }else if(t>0 && p==0){
            reponce.setText("Il manque le pois.");
        }else if(p>0 && t==0){
            reponce.setText("Il manque la taille.");
        }else{
            reponce.setText("Ajouter un pois et une taille");
        }
    }


    private void calculeIMC(Float t,Float p){
        Float r = p/(t*t);
        NumberFormat nf = new DecimalFormat("0.##");
        String s = nf.format(r);
        reponce.setText("IMC:\n"+s);

        //reponce.setTextColor(Color.rgb(255, 152, 0)); // orange
        String ss="";
        if(r<18.5){
            imageView.setImageResource(R.drawable.pmaigre);
            ss="maigre";
            reponce.setTextColor(Color.rgb(0, 255, 200));
        }else if(r>=18.5 && r<25){
            imageView.setImageResource(R.drawable.pnormal);
            reponce.setTextColor(Color.GREEN);
            ss="normal";
        }else if(r>=25 && r<30){
            imageView.setImageResource(R.drawable.psurpoids);
            reponce.setTextColor(Color.rgb(255, 208, 0));
            ss="surpoids";
        }else if(r>=30 && r<35){
            imageView.setImageResource(R.drawable.pomodere);
            reponce.setTextColor(Color.rgb(255, 152, 0));
            ss="obésité modérée";
        }else if(r>=35 && r<40){
            imageView.setImageResource(R.drawable.posevere);
            reponce.setTextColor(Color.rgb(156, 128, 84));
            ss="obésité sévère";
        }else if(r>=40){
            imageView.setImageResource(R.drawable.pomorbide);
            reponce.setTextColor(Color.RED);
            ss="obésité morbide";
        }

        reponce.setText("IMC:"+s+"\n"+ss);

        if(!cont.contains(t+";"+p)){
            file.ecrireFile(file.ouvrireFichier("save",false),t+";"+p);
        }



    }


}
