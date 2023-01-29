package com.bizone.kitchendisplay;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class StoreVIewAdapter extends ArrayAdapter<Store> {
    public StoreVIewAdapter(@NonNull Context context, @NonNull ArrayList<Store> stores) {
        super(context, 0, stores);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.store_view, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        Store store = getItem(position);

        // // then according to the position of the view assign the desired image for the same
        // ImageView numbersImage = currentItemView.findViewById(R.id.imageView);
        // assert currentNumberPosition != null;
        // numbersImage.setImageResource(currentNumberPosition.getNumbersImageId());

        TextView store_name = currentItemView.findViewById(R.id.store_name);
//        Log.i("STOREVIEW_ADAPTER", store.name + " " + store_name);
        store_name.setText(store.name);

        return currentItemView;
    }
}
