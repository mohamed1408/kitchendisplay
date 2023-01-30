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

public class KOTGroupAdapter extends ArrayAdapter<KOTGroup> {
    ArrayList<KOTGroup> _kotGroups;
    public KOTGroupAdapter(@NonNull Context context, @NonNull ArrayList<KOTGroup> kotGroups) {
        super(context, 0, kotGroups);
        _kotGroups = kotGroups;
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
        KOTGroup kotGroup = getItem(position);

        // // then according to the position of the view assign the desired image for the same
        // ImageView numbersImage = currentItemView.findViewById(R.id.imageView);
        // assert currentNumberPosition != null;
        // numbersImage.setImageResource(currentNumberPosition.getNumbersImageId());

        TextView store_name = currentItemView.findViewById(R.id.store_name);
        store_name.setText(kotGroup.description);
        return currentItemView;
    }
}