package com.gymsic.kara.gymsic.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gymsic.kara.gymsic.Model.Song;
import com.gymsic.kara.gymsic.PlaylistFragment;
import com.gymsic.kara.gymsic.R;
import com.gymsic.kara.gymsic.SongFragment;

import java.util.ArrayList;
import java.util.List;


public class MyPlaylistRecyclerViewAdapter extends RecyclerView.Adapter<MyPlaylistRecyclerViewAdapter.ViewHolder> {


    private final List<Song> mSongs;
    private final PlaylistFragment.OnListFragmentInteractionListener mListener;

    public MyPlaylistRecyclerViewAdapter(List<Song> items, PlaylistFragment.OnListFragmentInteractionListener listener) {
        mSongs = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyPlaylistRecyclerViewAdapter.ViewHolder holder, int position) {
        //holder.mView.setClickable(true);
       // holder.mView.setSelected(!holder.mView.isSelected());

        holder.mItem = mSongs.get(position);
        holder.mNameView.setText(mSongs.get(position).getTitle()+ " - " + mSongs.get(position).getArtist() );

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("xxxxxxx","xxxxxxx");
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {

        return mSongs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public Song mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.song);
            //view.setClickable(true);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
