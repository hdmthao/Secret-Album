package com.bigocoding.secretalbum;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class GridviewAdapter extends ArrayAdapter {
    private Context _context ;
    private int _layoutid ;
    private ArrayList<Upload> _myGallery ;
    public GridviewAdapter(@NonNull Context context, int resource, ArrayList<Upload> objects) {
        super(context, resource);
        this._context = context  ;
        this._layoutid = resource  ;
        _myGallery = objects ;
    }

    @Override
    public int getCount() {
        return _myGallery.size() ;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Upload uploadCurrent = _myGallery.get(position);
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(_context) ;
            convertView = inflater.inflate(_layoutid, null);
        }
        ImageView imageView = convertView.findViewById(R.id.gridviewitem);
        Picasso.with(_context)
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(imageView);
        return convertView;

    }
}
