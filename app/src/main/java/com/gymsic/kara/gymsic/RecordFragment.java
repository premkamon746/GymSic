package com.gymsic.kara.gymsic;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.piasy.audioprocessor.AudioProcessor;
import com.github.piasy.rxandroidaudio.AudioRecorder;
import com.github.piasy.rxandroidaudio.StreamAudioPlayer;
import com.github.piasy.rxandroidaudio.StreamAudioRecorder;
import com.gymsic.kara.gymsic.Plugin.WaveFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ak.sh.ay.musicwave.MusicWave;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AudioRecorder mAudioRecorder;
    private File mOutputFile;
    private FileOutputStream mFileOutputStream;
    private StreamAudioRecorder mStreamAudioRecorder;
    private StreamAudioPlayer mStreamAudioPlayer;
    private AudioProcessor mAudioProcessor;
    private byte[] mBuffer;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION  = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean mIsRecording = false;
    private double timeRecord = 0;


    static  int BUFFER_SIZE = 2048;
    static  int SAMPLE_RATE = 44100;
    static  short NO_OF_CHANNEL_CHANNEL = 2;
    static  short BIT_DEPT = 16;
    static  short AUDIO_FORMAT_CHANNEL = AudioFormat.CHANNEL_IN_STEREO;
    static  short AUDIO_FORMAT_ENCODE = AudioFormat.ENCODING_PCM_16BIT;

    static final int CAL_TIME = SAMPLE_RATE * BIT_DEPT * NO_OF_CHANNEL_CHANNEL;

    Timer timer;
    TimerTask timerTask;
    Handler handler;
    private int seconds          = 0;
    private boolean timerRunning = false;
    Button recordButton;
    TextView textView;
    TextView statusText;

    SeekBar seekBar;
    private float mRatio = 1;

    Button playButton;
    private boolean playing = false;

    private MusicWave musicWave;
    private final String UPLOAD_URL = "http://gymsic.com/analysis.php";



    private OnFragmentInteractionListener mListener;
    public RecordFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        //if (!permissionToRecordAccepted )

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("mp3 directory :", Environment.getExternalStorageDirectory().getAbsolutePath().toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_record, container, false);

        setUpAudioFormat();//setup audio format

        mStreamAudioRecorder = StreamAudioRecorder.getInstance();
        mStreamAudioPlayer = StreamAudioPlayer.getInstance();
        mAudioProcessor = new AudioProcessor(BUFFER_SIZE);
        mBuffer = new byte[BUFFER_SIZE];

        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        recordButton = (Button)view.findViewById(R.id.recordButton);
        playButton = (Button)view.findViewById(R.id.playButton);
        textView = (TextView) view.findViewById(R.id.timeRecord);
        statusText = (TextView) view.findViewById(R.id.statusText);
        musicWave = (MusicWave) view.findViewById(R.id.musicWave);


        ActivityCompat.requestPermissions(this.getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsRecording)
                {
                    statusText.setText("");
                    stopRecord();
                    stoptimerRecord(view);

                    mIsRecording = false;
                    timerRunning = false;
                    seconds      = 0;
                } else {


                    statusText.setText(R.string.recording);
                    startRecord();
                    startTimer();

                    mIsRecording = true;
                    timerRunning = true;
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!playing) {
                    playing = true;
                    play();
                }else {
                    stopPlay();
                }
            }
        });




        seekBar.setMax(100);
        /*seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRatio = (float) progress / 100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });*/
        return view;
    }

    private void setUpAudioFormat(){
        SAMPLE_RATE = 44100;
        AUDIO_FORMAT_CHANNEL = AudioFormat.CHANNEL_IN_STEREO;
        AUDIO_FORMAT_ENCODE = AudioFormat.ENCODING_PCM_16BIT;
        BUFFER_SIZE = 2048;
    }


    private void startTimer() {
        //set a new Timer
        timer = new Timer();
        handler = new Handler();

        //initialize the TimerTask's job
        initializeTimerRecord();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 0, 1000); //
    }

    private void stoptimerRecord(View v) {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;

            uploadFile( UPLOAD_URL, mOutputFile );
        }
    }

    private void initializeTimerRecord() {
        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        if(timerRunning) {

                            int minutes = (seconds % 3600) / 60;
                            int sec = seconds % 60;
                            String time = String.format("%01d:%02d", minutes, sec);
                            textView.setText(time);
                            seconds++;
                        }
                    }
                });
            }
        };
    }

    private void startRecord() {
        playButton.setVisibility(View.INVISIBLE);
        seekBar.setVisibility(View.INVISIBLE);

        try {
            mOutputFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    File.separator + "gymsic" + ".wav");
            mOutputFile.createNewFile();
            mFileOutputStream = new FileOutputStream(mOutputFile);

            WaveFile.writeWavHeader(mFileOutputStream, NO_OF_CHANNEL_CHANNEL, SAMPLE_RATE, BIT_DEPT);

            mStreamAudioRecorder.start(SAMPLE_RATE, AUDIO_FORMAT_CHANNEL, AUDIO_FORMAT_ENCODE, BUFFER_SIZE, new StreamAudioRecorder.AudioDataCallback() {
                //mStreamAudioRecorder.start(new StreamAudioRecorder.AudioDataCallback() {
                int count = 0;
                @Override
                public void onAudioData(byte[] data, int size) {
                    if (mFileOutputStream != null) {
                        try {
                            //Log.d("AMP", "amp " + calcAmp(data, size));
                            //writeGraph(data, size);
                            //Log.d("AMP", "amp " + amp);
                            mFileOutputStream.write(data, 0, size);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    musicWave.updateVisualizer(data);
                                }//public void run() {
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch(NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError() {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecord() {
        mStreamAudioRecorder.stop();
        //playButton.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.VISIBLE);
        try {
            mFileOutputStream.close();
            mFileOutputStream = null;


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeGraph(byte[] data, int size){



        int[] result = null;
        int dataLength = size / 2;
        result = new int[dataLength];
        for (int i = 0; i < dataLength; i += 2) {
            byte LSB = data[i * 2];
            byte MSB = data[i * 2 + 1];
            result[i / 2] = MSB << 8 | (255 & LSB);
            Log.d("Reslt : ", " LSB: " + LSB + " MSB: " + MSB +" result: " + result[i / 2]);
        }



    }


    private void play() {
        Observable.just(mOutputFile)
                .subscribeOn(Schedulers.io())
                .subscribe(file -> {
                    try {
                        mStreamAudioPlayer. init(SAMPLE_RATE, AUDIO_FORMAT_CHANNEL, AUDIO_FORMAT_ENCODE,
                        BUFFER_SIZE) ;
                        FileInputStream inputStream = new FileInputStream(file);
                        int read;
                        int i = 1;
                        while ((read = inputStream.read(mBuffer)) > 0 && playing) {
                            mStreamAudioPlayer.play(mBuffer, read);
                            int seconds = (read * 8 * i)/CAL_TIME;
                            int minutes = (seconds % 3600) / 60;
                            int sec = seconds % 60;
                            String time = String.format("%01d:%02d", minutes, sec);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    statusText.setText(R.string.playing);
                                    textView.setText(time);
                                    int total = (int)(file.length() * 8)/CAL_TIME;
                                    float retio = (float)seconds / (float)total;
                                    int seek = (int)(100 * retio);
                                    seekBar.setProgress(seek);/**/
                                }//public void run() {
                            });

                            i++;
                        }
                        inputStream.close();
                        mStreamAudioPlayer.release();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
    }

    private void stopPlay(){
        playing = false;
    }

    public static Boolean uploadFile(String serverURL, File file) {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody =  RequestBody.create(MediaType.parse("audio/wav"), file);


            Request request = new Request.Builder()
                    .url(serverURL)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });

            return true;
        } catch (Exception ex) {
            // Handle the error
        }
        return false;
    }


    private int calcAmp(byte[] data, int size) {
        int amplitude = 0;
        for (int i = 0; i + 1 < size; i += 2) {
            short value = (short) (((data[i + 1] & 0x000000FF) << 8) + (data[i + 1] & 0x000000FF));
            amplitude += Math.abs(value);
        }
        amplitude /= size / 2;
        return amplitude / 2048;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
