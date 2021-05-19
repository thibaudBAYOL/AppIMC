package com.example.appimc;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText taille;
    EditText pois;
    TextView reponce;
    MyFiles file;
    String cont = "";
    ImageView imageView;


    Spinner spinnerF;

    ArrayList<String> future;
    ArrayAdapter<String> adaptfuture;

    Spinner spinner;
    EditText txtText;


    ArrayList<String> users;
    ArrayAdapter<String> adapt;

    Spinner spinnerH;
    ArrayList<String> hist;
    ArrayAdapter<String> adapthist;
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taille = findViewById(R.id.editTextT);
        pois = findViewById(R.id.editTextP);
        reponce = findViewById(R.id.textViewR);
        imageView = findViewById(R.id.imageView);

        //Le champ de saisie si NEW
        txtText = (EditText)findViewById(R.id.editText);
        txtText.setVisibility(View.INVISIBLE);
        file = new MyFiles(this);


        cont = "save";
        // lire userS
        if(file.existe("listUSERS") && !file.lireSimple("listUSERS").contains("File not exist")) {
            cont = file.lireSimple("listUSERS");
        }else{
            file.ecrireSimple("listUSERS","save");
        }

        String tab[] = cont.split("\n");

        users = new ArrayList<String>();
        users.add(tab[0]);
        for (String s: tab) {
            if(!s.contentEquals("") && !s.contentEquals(tab[0]) ) {
                users.add(s);
            }
        }
        users.add("NEW");

        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        //Initialiser l'adapteur avec la liste de données "users" déclarée plus haut (malgré vide dans un premier temps)
        adapt = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, users);
        //passer l'adapteur dans le Spinner
        spinner.setAdapter(adapt);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0);
        //System.out.println("spinner.getSelectedItem()="+spinner.getSelectedItem().toString());

        // lire last_user()
        last_user(tab[0]);


        taille.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controle();
                voir_le_future();
            }
        });

        pois.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controle();
            }
        });

        txtText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(users.contains(txtText.getText().toString())){
                    reponce.setText("exite déjà");
                }else{
                    reponce.setText("new");
                }
            }
        });






    }

    private String poit_pour_imc(Float imc,Float taille){
        return  ""+(imc*(taille*taille));
    }

    private void voir_le_future(){

        future = new ArrayList<String>();
        String ss = taille.getText().toString();
        Float t = Float.parseFloat(ss);
        //imc = p/t^2
        // p = imc*(t^2)


        //if(r<18.5){

            ss="maigre";
        String moin=poit_pour_imc((float)18.5,t);
        future.add("X<"+moin+"=>"+ss);

        //}else if(r>=18.5 && r<25){

            ss="normal";

        String plus=poit_pour_imc((float)25,t);
        future.add(moin+"=>X<"+plus+"=>"+ss);
        moin = plus;

        //}else if(r>=25 && r<30){

            ss="surpoids";
        plus=poit_pour_imc((float)30,t);
        future.add(moin+"=>X<"+plus+"=>"+ss);
        moin = plus;

        //}else if(r>=30 && r<35){
            ss="obésité modérée";
        plus=poit_pour_imc((float)35,t);
        future.add(moin+"=>X<"+plus+"=>"+ss);
        moin = plus;
        //}else if(r>=35 && r<40){
            ss="obésité sévère";
        plus=poit_pour_imc((float)40,t);
        future.add(moin+"=>X<"+plus+"=>"+ss);
        moin = plus;
        //}else if(r>=40){
            ss="obésité morbide";
        future.add(moin+">X=>"+ss);
        //}

        spinnerF = (Spinner) findViewById(R.id.spinnerF);
        adaptfuture = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, future);
        //passer l'adapteur dans le Spinner
        spinnerF.setAdapter(adaptfuture);
    }

    private void last_user(String name){

        cont = "";

        if(file.existe(name) && !file.lireSimple(name).contains("File not exist")) {

            ///Liste
            cont=name+"\n";
            for (String s: users) {
                if(!s.contentEquals(name) && !s.contentEquals("NEW")) cont+=s+"\n";
            }
            file.ecrireSimple("listUSERS",cont);

            /// COntenu
            cont = file.lireSimple(name);


            String tab2[] = cont.split("\n");

            if(cont.contains(";")){
                cont=tab2[0];

                String tab[] = cont.split(";");
                if(tab.length>=2) {
                    taille.setText(tab[0]);
                    pois.setText(tab[1]);
                    if(tab.length>=3){
                        reponce.setText("IMC:"+tab[2]);
                        voir_le_future();
                    }else {
                        controle();
                    }
                }else{
                    //reponce.setText("ERR");
                }

                //lire user last SELECT

                hist = new ArrayList<String>();
                hist.add("Historique");
                Float p,t;
                String imc="vide";
                String time="?";
                for (String s: tab2) {
                    tab = s.split(";");

                    if(tab.length>=3){
                        imc=tab[2];
                    }else if(tab.length==2) {
                        p = Float.valueOf(tab[0]);
                        t = Float.valueOf(tab[1]);
                        Float r = p / (t * t);
                        NumberFormat nf = new DecimalFormat("0.##");
                        imc = nf.format(r);
                    }
                    if(tab.length>=4){
                        time = tab[3];
                    }
                    //time?
                    if(tab.length>=2){
                        hist.add("t="+tab[0]+" p="+tab[1]+"imc="+imc);
                        hist.add("time="+time);

                    }else{
                        // hist.add(s);
                    }
                }
                spinnerH = (Spinner) findViewById(R.id.spinner2);
                adapthist = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, hist);
                //passer l'adapteur dans le Spinner
                spinnerH.setAdapter(adapthist);

            }else{
                cont="VIDE";
                reponce.setText(cont);
            }



        }else{
            file.ecrireSimple(name,"Ajouter un Pois et une taille");
            cont="Ajouter un pois et une taille";
            reponce.setText(cont);
        }


    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        System.out.println("pos="+pos+"kkk= "+parent.getItemAtPosition(pos).toString());
        if(parent.getItemAtPosition(pos).toString().contentEquals("NEW")){
            txtText.setVisibility(View.VISIBLE);
            int i=1;
            while(users.contains("user"+i)){
                i++;
            }
            txtText.setText("user"+i);
        }else {
            if (txtText.getVisibility() == View.VISIBLE) txtText.setVisibility(View.INVISIBLE);
            last_user(parent.getItemAtPosition(pos).toString());
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        spinner.setSelection(0);
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
        String name=spinner.getSelectedItem().toString();
        if(name=="NEW"){
            name = txtText.getText().toString();
            if(!users.contains(txtText.getText().toString())){
                users.add(name);
                last_user(name);
            }
        }



        Date today = Calendar.getInstance().getTime();
        String dfDate = df.format(today);
        if(!cont.contains(t+";"+p+";"+s+"/"+ss)){
            file.ecrireSimple(name,t+";"+p+";"+s+"/"+ss+";"+dfDate+"\n"+file.lireSimple(name));
        }

    }


}
