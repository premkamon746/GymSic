package com.gymsic.kara.gymsic.module;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by premkamon on 17/5/2560.
 */

public class Download extends AsyncTask<String, Integer, String> {

    /**
     * Before starting background thread
     * */

    Context context;
    ProgressBar pb;

    public Download(Context context,ProgressBar pb){
        this.context = context;
        this.pb = pb;
        pb.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.println("Starting download");
    }

    /**
     * Downloading file in background thread
     * */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            String root = Environment.getExternalStorageDirectory().toString();

            System.out.println("Downloading");
            URL url = new URL(f_url[0]);

            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file

            OutputStream output = new FileOutputStream(root+"/test.mp3");
            byte data[] = new byte[1024];

            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                //pb.setProgress((int) ((total * 100) / lenghtOfFile));
                publishProgress((int) ((total * 100) / lenghtOfFile));
                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }
    /**
     * After completing background task
     * **/
    @Override
    protected void onPostExecute(String file_url) {
        System.out.println("Downloaded");
        pb.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onProgressUpdate(Integer... values){
        pb.setProgress(values[0]);
        //Log.d("download...",values[0]+"");
    }


}