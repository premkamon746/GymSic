package com.gymsic.kara.gymsic;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gymsic.kara.gymsic.adapter.SongAdapter;
import com.gymsic.kara.gymsic.data.Song;

import java.util.ArrayList;
import java.util.List;


public class SearchItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String song_detail = "song_detail";
    // TODO: Customize parameters
    ArrayList<Song> songs;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchItemFragment() {
    }


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchItemFragment newInstance(ArrayList<Song> songs) {
        SearchItemFragment fragment = new SearchItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(song_detail, songs);
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
        View view = inflater.inflate(R.layout.fragment_searchitem_list, container, false);
        SongAdapter itemsAdapter =
                new SongAdapter(getActivity(), R.layout.fragment_searchitem,songs);
        ListView listView = (ListView) view.findViewById(R.id.songListview);
        listView.setAdapter(itemsAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
