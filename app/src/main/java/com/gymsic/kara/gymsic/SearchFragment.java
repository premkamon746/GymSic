package com.gymsic.kara.gymsic;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.gymsic.kara.gymsic.Interface.OnRecycleViewClick;
import com.gymsic.kara.gymsic.Interface.OnServerDataComplete;
import com.gymsic.kara.gymsic.Interface.OnTaskComplete;
import com.gymsic.kara.gymsic.Model.Song;
import com.gymsic.kara.gymsic.Plugin.Download;
import com.gymsic.kara.gymsic.Plugin.Playlist;
import com.gymsic.kara.gymsic.Plugin.Server;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    SongFragment songFram;
    OnServerDataComplete servCompl;
    //public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public String server = "http://192.168.1.153:3000/";

    public SearchFragment() {
        // Required empty public constructor
        servCompl = setOnServerComplete();
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
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);

        final SearchView searchView = (SearchView)view.findViewById(R.id.search);
        searchView.setQueryHint(getString(R.string.find_song));
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                SongFragment songFram = new SongFragment();
                getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .remove(songFram)
                .commit();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                try {
                    if(newText.length() >=2 ) {

                        new Server(servCompl).post(server, newText.toString());

                    }else{
                        searchView.setIconified(false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if(newText.length() >=2 ) {

                        new Server(servCompl).post(server, newText.toString());

                    }else{
                        searchView.setIconified(false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        return view;
    }



    public OnServerDataComplete setOnServerComplete(){
        return new OnServerDataComplete(){
            @Override
            public void onServerDataComplete(ArrayList<Song> songs){
                SongFragment songFram = new SongFragment();
                songFram.setSong(songs);
                getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.auto_complete_place,songFram)
                .commit();
            }
        };

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
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
