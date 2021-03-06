package com.example.appimc;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MyFiles {

    boolean inOut=false;
    Context leC;

    public MyFiles(Context tt){
        leC = tt;
    }


    String lireSimple(String n){
        return lireFile(ouvrireFichier(n, inOut));
    }
    void ecrireSimple(String n,String cont){
         ecrireFile(ouvrireFichier(n, inOut),cont);
    }

    boolean existe(String name){
        return ouvrireFichier(name,inOut) != null ;
    }

    File ouvrireFichier(String filename, Boolean sloc){


        File director = null ;
        if( sloc == true ) {

            director = new File("/storage/self/primary/IMC");
            if (!director.exists()) {
                director.mkdir();
                System.out.println("/////////////////////////////////MKDIR ");
            }else{
                System.out.println("/////////////////////////////////EXIST ");
            }

            if (!director.exists()) {
                System.out.println("/////////////////////////////////toujour pas ");
            }
            if( !(director.canRead() & director.canWrite())){
                System.out.println("/////////////////////////////////RW ");
                //println(" read  "+director.canRead() +" write" + director.canWrite());
            }
            System.out.println("/////////////////////////////////AA ");
        }else{
            director = leC.getFilesDir();
        }

        if ( director == null){
            return null;
        }
        return new File(director, filename);

    }

    // stocage interne. // stocage externe

    void ecrireFile(File file, String fileContents ){



        if (file == null) return;

        try (BufferedWriter writer =  new BufferedWriter(new FileWriter(file))) {
            writer.write(fileContents);
        } catch (IOException e) {

            //zoneOut.setText("MERDE");
        }

        MediaScannerConnection.scanFile(leC, new String[] {file.getPath()}, null, null);
        leC.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

    private String load(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        while(true) {
            String line = reader.readLine();
            if (line==null) break;
            if(!line.equals("")) {
                builder.append(line).append("\n");
            }
        }
        return builder.toString();
    }

     String lireFile(File file) {


        if (file == null) return "File not exist";

        if (!file.exists()){

            //setText("File not exist");
            System.out.println("File not exist");
            return "File not exist";
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return  load(reader);
        } catch (IOException e) {
            //zoneOut.setText("MERDE2 l.108");
            System.out.println("MERDE2 l.108");
            return "File not exist";
        }
    }
    // fin





}
