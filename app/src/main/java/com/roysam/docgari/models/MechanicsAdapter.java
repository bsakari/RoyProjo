package com.roysam.docgari.models;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.roysam.docgari.R;

import java.util.ArrayList;
import java.util.List;

public class MechanicsAdapter extends RecyclerView.Adapter<MechanicsAdapter.ImageViewHolder> {
    private Context mContext;
    private List<User> mUploads;
    private OnItemClickListener mListener;

    public MechanicsAdapter(Context mContext, List<User> mUploads) {
        this.mContext = mContext;
        this.mUploads = mUploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.mechanic_layout,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        User uploadCurrent = mUploads.get(position);
        holder.mTvName.setText(uploadCurrent.getName()+" | Ph. "+uploadCurrent.getPhone());
        holder.mTvDistance.setText("Date | "+uploadCurrent.getLat());
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView mTvName,mTvEmail, mTvDistance;
        public ImageView imageView;
        public ImageViewHolder(View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvDistance = itemView.findViewById(R.id.tv_distance);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mListener!=null){
                //Get the position of the clicked item
                int position = getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }
        // Handle Menu Items
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem doWhatever = menu.add(Menu.NONE, 1, 1,"Do Whatever");
            MenuItem delete = menu.add(Menu.NONE,2,2,"Delete");
            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener!=null){
                //Get the position of the clicked item
                int position = getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:
                            mListener.onWhatEverClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position);

        void onWhatEverClick(int position);

        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public void setSearchOperation(List<User> newList){
        mUploads = new ArrayList<>();
        mUploads.addAll(newList);
        notifyDataSetChanged();
    }
}