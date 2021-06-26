package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button name0;
    Button name1;
    Button name2;
    Button name3;

    ArrayList<String> celebURLs =new ArrayList<String>();
    ArrayList<String> celebNames =new ArrayList<String>();

    int chosenCeleb=0;
    int locationOfCorrectAnswer=0;
    String[] answers= new String[4];


    public void celebChosen(View view){

        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {
            Toast.makeText(this, "Right Answer!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Wrong! It was "+celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }

        newQuestion();
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {

            String result="";
            URL url;
            HttpURLConnection urlConnection=null;

            try {
                url=new URL(urls[0]);
                urlConnection= (HttpURLConnection) url.openConnection();
                InputStream in= urlConnection.getInputStream();
                InputStreamReader reader= new InputStreamReader(in);
                int data= reader.read();

                while(data!= -1){
                    char ch=(char) data;
                    result+=ch;
                    data=reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url=new URL(urls[0]);
                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in=urlConnection.getInputStream();
                Bitmap celebBitmap= BitmapFactory.decodeStream(in);

                return celebBitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void newQuestion() {
        try {
            Random rand = new Random();
            chosenCeleb = rand.nextInt(100);  //printing 100 celebrity names, limit can be changed

            ImageDownloader task = new ImageDownloader();
            Bitmap celebImage;
            celebImage = task.execute(celebURLs.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebImage);

            locationOfCorrectAnswer = rand.nextInt(4);     //assigning correct name to any of the 4 options using Random()
            int incorrectAnswerLocation;

            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] = celebNames.get(chosenCeleb);
                } else {
                    incorrectAnswerLocation = rand.nextInt(100);

                    while (incorrectAnswerLocation == chosenCeleb) {
                        incorrectAnswerLocation = rand.nextInt(100);
                    }

                    answers[i] = celebNames.get(incorrectAnswerLocation);
                }
            }

            name0.setText(answers[0]);
            name1.setText(answers[1]);
            name2.setText(answers[2]);
            name3.setText(answers[3]);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=findViewById(R.id.imageView);
        name0=findViewById(R.id.name0);
        name1=findViewById(R.id.name1);
        name2=findViewById(R.id.name2);
        name3=findViewById(R.id.name3);

        DownloadTask webTask= new DownloadTask();
        String res="";

        try {
            //content source from the below website
            res=webTask.execute("https://www.imdb.com/list/ls068010962/").get();

            //unique keyword in source code to split and use accordingly
            String[] splitString2=res.split("200 names");

            Pattern p=Pattern.compile("alt=\"(.*?)\"");
            Matcher m=p.matcher(splitString2[1]);   //using the second half of splitted string

            while (m.find()){
                celebNames.add(m.group(1));
            }

            p = Pattern.compile("src=\"(.*?)\"");
            m=p.matcher(splitString2[1]);

            while(m.find()){
                celebURLs.add(m.group(1));
            }

            newQuestion();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





























