package com.gymsic.kara.gymsic;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.gymsic.kara.gymsic.Adapter.MyPlaylistRecyclerViewAdapter;
import com.gymsic.kara.gymsic.Interface.OnTaskComplete;
import com.gymsic.kara.gymsic.Listener.RecyclerItemClickListener;
import com.gymsic.kara.gymsic.Model.Song;
import com.gymsic.kara.gymsic.Plugin.Download;
import com.gymsic.kara.gymsic.Plugin.Player;
import com.gymsic.kara.gymsic.Plugin.Playlist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PlaylistFragment extends Fragment {

    // TODO: Customize parameter argument names
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    ArrayList<Song> songs;
    //Player player = new Player();
    MediaPlayer  mediaPlayer;

    private int getCurrentSong() {
        return ++currentSong;
    }

    private void setCurrentSong(int currentSong) {
        this.currentSong = currentSong;
    }

    int currentSong = 0;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlaylistFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PlaylistFragment newInstance(ArrayList<Song> songs) {
        PlaylistFragment fragment = new PlaylistFragment();
//        Bundle args = new Bundle();
//        args.putSerializable("songs", songs);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //songs = new ArrayList<Song>(); //dummy object
//        if (getArguments() != null) {
//            songs = (ArrayList<Song>)getArguments().getSerializable("songs");
//        }
    }

    public void setMediaPlayer(MediaPlayer  mediaPlayer){
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_list, container, false);
        Log.d("log on load ", "onCreateView");
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view,final int position) {
                            //Log.d("log position player ","position : "+position);
                            //String song = getActivity().getExternalCacheDir()+ "/gymsic/"+songs.get(position).getFilename();
                            setCurrentSong(position);
                            playSong(mediaPlayer,position);
                            //int songIndex = position;
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    int position = getCurrentSong();
                                    playSong(mp ,position);
                                }
                            });



                        }
                    })
            );

            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyPlaylistRecyclerViewAdapter(songs, mListener));
        }
        return view;
    }

    public void playSong(MediaPlayer mp, int position){
        Log.d("log position player ",songs.size()+ " : "+position);
        if(songs.size() > position) {
            String songFileName = songs.get(position).getFilename();
            File dir = getActivity().getExternalFilesDir("music");
            String song =  dir + songFileName;

            File file = new File(song);
            if (file.exists()) {
                mp.reset();
                try {
                    mp.setDataSource(song);
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {

                            mp.start();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.prepareAsync();

            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Song item);
    }
}
