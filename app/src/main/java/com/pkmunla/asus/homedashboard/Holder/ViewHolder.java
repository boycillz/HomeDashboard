package com.pkmunla.asus.homedashboard.Holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pkmunla.asus.homedashboard.R;
import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public ViewHolder (View itemView){
        super(itemView);

        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getAdapterPosition());
                return true;
            }
        });
    }

    //set details to recyclerview row
    public void setDetails(Context ctx, String title, String image, String location, String desctiption, String ustad, String masjid){

        //view
        TextView mTitleTv = mView.findViewById(R.id.rTitleTV);
        TextView mDescTV = mView.findViewById(R.id.rDescriptionTV);
        TextView mUstadTV = mView.findViewById(R.id.rUstadzTV);
        TextView mMasjidTV = mView.findViewById(R.id.rMasjidTV);
        TextView mLocationTv = mView.findViewById(R.id.rLocationTV);
        ImageView mImageTv = mView.findViewById(R.id.rImageView);

        //set data to view
        mTitleTv.setText(title);
        mDescTV.setText(desctiption);
        mUstadTV.setText(ustad);
        mMasjidTV.setText(masjid);
        mLocationTv.setText(location);
        Picasso.get().load(image).into(mImageTv);
    }

    private ViewHolder.ClickListener mClickListener;

    //interface to send callbacks
    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(ViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }
}
