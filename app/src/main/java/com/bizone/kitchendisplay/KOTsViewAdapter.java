package com.bizone.kitchendisplay;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import java.sql.Date;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class KOTsViewAdapter extends ArrayAdapter<Kot> {
    ArrayList<Kot> _kots;
    Context _context;
    OkHttpClient client = new OkHttpClient();
    Date today;
    private HashMap<TextView,CountDownTimer> counters;
    public KOTsViewAdapter(@NonNull Context context, ArrayList<Kot> kots) {
        super(context, 0, kots);
        _kots = kots;
        _context = context;
        today = new Date();
        this.counters = new HashMap<TextView, CountDownTimer>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        Kot kot = getItem(position);

        // // then according to the position of the view assign the desired image for the same
        // ImageView numbersImage = currentItemView.findViewById(R.id.imageView);
        // assert currentNumberPosition != null;
        // numbersImage.setImageResource(currentNumberPosition.getNumbersImageId());

        // then according to the position of the view assign the desired TextView 1 for the same
        TextView invoicetv = currentItemView.findViewById(R.id.invoiceno);
        TextView ordernametv = currentItemView.findViewById(R.id.ordername);
        TextView deldttimtv = currentItemView.findViewById(R.id.deldttm);
        TextView timertv = currentItemView.findViewById(R.id.timer);

        Button status_btn = currentItemView.findViewById(R.id.status_btn);
        Button undo_btn = currentItemView.findViewById(R.id.undo_btn);

        LinearLayout addedLO = currentItemView.findViewById(R.id.addlayout);
        LinearLayout removLO = currentItemView.findViewById(R.id.remLayout);

        TextView added_tv = (TextView) currentItemView.findViewById(R.id.added_list);
        TextView removed_tv = (TextView) currentItemView.findViewById(R.id.removed_list);
        TextView cname_tv = (TextView) currentItemView.findViewById(R.id.customername);
        TextView note_tv = (TextView) currentItemView.findViewById(R.id.note);
        // ListView removed_lv = (ListView) currentItemView.findViewById(R.id.removed_list);

        ArrayList<String> added_prods = new ArrayList<>();
        ArrayList<String> removed_prods = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat fancyFormat = new SimpleDateFormat("EEE MMM dd hh:mm aa");
        SimpleDateFormat justDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date delDate;
        try {
            delDate = dateFormat.parse(kot.deliverydatetime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Log.i("KOT_ITEMS_LOG", kot.isitemrendered + " " + kot.invoiceno);
//        if(!kot.isitemrendered) {
        if(kot.added.size() == 0) {
            addedLO.setVisibility(View.GONE);
        }
        if(kot.removed.size() == 0) {
            removLO.setVisibility(View.GONE);
        }

        cname_tv.setText(kot.customername);
        note_tv.setText(kot.note);

        String itemText = "";
        for(int i=0; i<kot.added.size(); i++){
            itemText += kot.added.get(i).quantity + " x " + kot.added.get(i).name + "\n";
            added_prods.add(kot.added.get(i).quantity + " x " + kot.added.get(i).name);
        }
        added_tv.setText(itemText);
        itemText = "";
        for(int i=0; i<kot.removed.size(); i++){
            itemText += kot.removed.get(i).quantity + " x " + kot.removed.get(i).name + "\n";
            added_prods.add(kot.removed.get(i).quantity + " x " + kot.removed.get(i).name);
        }
        removed_tv.setText(itemText);
        removed_tv.setPaintFlags(removed_tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            for(int i=0; i<kot.removed.size(); i++){
//                // Log.i("KOT_ITEMS_LOG", kot.added.get(i).quantity + " x " + kot.added.get(i).name);
//                removed_prods.add(kot.removed.get(i).quantity + " x " + kot.removed.get(i).name);
//            }
//            kot.isitemrendered = true;
//        }

        // ArrayAdapter adapter = new ArrayAdapter<String>(getContext(), R.layout.addedproduct_listview, added_prods);
        // ArrayAdapter rdapter = new ArrayAdapter<String>(getContext(), R.layout.remoproduct_listview, removed_prods);

        // added_lv.setAdapter(adapter);
        // removed_lv.setAdapter(rdapter);

        invoicetv.setText(kot.invoiceno + "#" + kot.kotno);
        ordernametv.setText(kot.ordername);
        deldttimtv.setText(fancyFormat.format(delDate));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate localDate = LocalDate.parse(justDateFormat.format(delDate));
        }


        // then according to the position of the view assign the desired TextView 2 for the same
        String btn_text = null;
        if (kot.isloading == true) {
            btn_text = "...";
        } else {
            switch (kot.statusid){
                case 0:
                    btn_text = "Accept";
                    status_btn.setEnabled(true);
                    status_btn.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.yellow)));
                    break;
                case 1:
                    btn_text = "Start";
                    status_btn.setEnabled(true);
                    status_btn.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.yellow)));
                    break;
                case 2:
                    btn_text = "Complete";
                    status_btn.setEnabled(true);
                    status_btn.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.green)));
                    break;
                case 3:
                    btn_text = "Serve";
                    status_btn.setEnabled(true);
                    status_btn.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.purple)));
                    break;
                case 4:
                    btn_text = "Served";
                    status_btn.setEnabled(false);
                    status_btn.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.white)));
                    break;
            }
        }

        status_btn.setText(btn_text);
        status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(kot.statusid < 4 && kot.isloading != true) {
                    if(kot.statusid == 0) {
                        ((MainActivity) _context).stopNotificationSound();
                    }
                    String url = "https://biz1pos.azurewebsites.net/api/KOT/KOTStatusChange?kotid=" + kot.kotid + "&statusid=" + (kot.statusid+1);
                    Request req = new Request.Builder()
                            .url(url)
                            .build();
                    kot.isloading = true;
                    ((MainActivity) _context).updateList();
                    client.newCall(req).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.i("KOT_SATUS_CHANGE", String.valueOf(e));
                            kot.isloading = false;
                            ((MainActivity) _context).updateList();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // Log.i("KOT_SATUS_CHANGE", response.body().string());
                            try {
                                JSONObject resBody = new JSONObject(response.body().string());
                                int status = resBody.getInt("status");
                                if(status == 200){
                                    kot.statusid += 1;
                                    kot.isloading = false;
                                    ((MainActivity) _context).updateList();
                                }
                            } catch (JSONException e) {
                                Log.i("KOT_SATUS_CHANGE", String.valueOf(e));
                                kot.isloading = false;
                                ((MainActivity) _context).updateList();
                            }
                        }
                    });
                }
            }
        });
        undo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(kot.statusid > 1 && kot.isloading != true) {
                    String url = "https://biz1pos.azurewebsites.net/api/KOT/KOTStatusChange?kotid=" + kot.kotid + "&statusid=" + (kot.statusid-1);
                    Request req = new Request.Builder()
                            .url(url)
                            .build();
                    kot.isloading = true;
                    ((MainActivity) _context).updateList();
                    client.newCall(req).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.i("KOT_SATUS_CHANGE", String.valueOf(e));
                            kot.isloading = false;
                            ((MainActivity) _context).updateList();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // Log.i("KOT_SATUS_CHANGE", response.body().string());
                            try {
                                JSONObject resBody = new JSONObject(response.body().string());
                                int status = resBody.getInt("status");
                                if(status == 200){
                                    kot.statusid -= 1;
                                    kot.isloading = false;
                                    ((MainActivity) _context).updateList();
                                }
                            } catch (JSONException e) {
                                Log.i("KOT_SATUS_CHANGE", String.valueOf(e));
                                kot.isloading = false;
                                ((MainActivity) _context).updateList();
                            }
                        }
                    });
                }
            }
        });
        // then return the recyclable view
        long milliseconds = 0;
        if((delDate.getTime() - today.getTime()) > 0) {
            milliseconds = delDate.getTime() - today.getTime();
        }

        CountDownTimer cdt = counters.get(timertv);
        if(cdt!=null)
        {
            cdt.cancel();
            cdt=null;
        }
        cdt = new CountDownTimer(milliseconds, 1000) {

            public void onTick(long millisUntilFinished) {

                // Used for formatting digit to be in 2 digits only

                NumberFormat f = new DecimalFormat("00");

                long hour = (millisUntilFinished / 3600000) % 24;

                long min = (millisUntilFinished / 60000) % 60;

                long sec = (millisUntilFinished / 1000) % 60;

                timertv.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));

            }

            // When the task is over it will print 00:00:00 there

            public void onFinish() {
                timertv.setText("00:00:00");
            }
        };
        counters.put(timertv, cdt);
        cdt.start();
        return currentItemView;
    }

    public void cancelAllTimers()
    {
        Set<Map.Entry<TextView, CountDownTimer>> s = counters.entrySet();
        Iterator it = s.iterator();
        while(it.hasNext())
        {
            try
            {
                Map.Entry pairs = (Map.Entry)it.next();
                CountDownTimer cdt = (CountDownTimer)pairs.getValue();

                cdt.cancel();
                cdt = null;
            }
            catch(Exception e){}
        }

        it=null;
        s=null;
        counters.clear();
    }
}
