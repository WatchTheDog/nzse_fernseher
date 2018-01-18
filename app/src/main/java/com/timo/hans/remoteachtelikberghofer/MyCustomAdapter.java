package com.timo.hans.remoteachtelikberghofer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.zip.Inflater;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by Hans on 17.01.2018.
 */

public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private HttpRequestHandler Requester;
    private ArrayList<String> Favorites;
    private ListView Favs;
    private ListView current;
    private GridLayout Expert;
    private RelativeLayout Loading;

    public MyCustomAdapter(ArrayList<String> list, Context context, HttpRequestHandler Requester, ArrayList<String> Favorites, ListView Favs, ListView current, GridLayout Expert, RelativeLayout Loading) {
        this.Loading = Loading;
        this.Expert = Expert;
        this.current = current;
        this.Favs = Favs;
        this.Favorites = Favorites;
        this.Requester = Requester;
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.leftlist, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        final ImageButton faveBtn = (ImageButton)view.findViewById(R.id.favorize_btn);

        if (position==1||position==0) {
            faveBtn.setVisibility(View.INVISIBLE);
            listItemText.setTextColor(Color.BLACK);
        }
        else {
            faveBtn.setVisibility(View.VISIBLE);
            listItemText.setTextColor(Color.parseColor("#454545"));
        }

        for (int i = 2; i < list.size(); i++) {
            if (position == i && Requester.getIsFav(i-2))
                faveBtn.setImageResource(R.drawable.ic_star_white_48dp);
            else if (position == i && !Requester.getIsFav(i-2))
                faveBtn.setImageResource(R.drawable.ic_star_border_white_48dp);
        }

        listItemText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0 && position != 1) {
                    Requester.setCurrentChannel(Requester.getArrNumber(position-2));
                    Requester.setCH(position-2);
                    if (!Requester.getOn())
                        Requester.setOn(true);
                }
                else if (position == 0) {
                    current.setVisibility(View.GONE);
                    current.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Expert.setVisibility(View.GONE);
                            Loading.setVisibility(View.VISIBLE);

                            current.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Requester.ChannelScan();

                                    current.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (Requester.getArr() != null) {
                                                String[] arrAll = Requester.getArrCh();
                                                ArrayList<String> arraylist = new ArrayList<>();
                                                arraylist.add("Kanalscan");
                                                arraylist.add("Modus ändern");

                                                for (int i = 0; i < arrAll.length; i++) {
                                                    arraylist.add(arrAll[i]);
                                                }

                                                MyCustomAdapter mAdapter = new MyCustomAdapter(arraylist, context, Requester, Favorites, Favs, current, Expert, (RelativeLayout) Loading);
                                                current.setAdapter(mAdapter);

                                                current.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Loading.setVisibility(View.GONE);
                                                        Expert.setVisibility(View.VISIBLE);
                                                        current.setVisibility(View.VISIBLE);
                                                    }
                                                }, 100);
                                            }
                                        }
                                    }, 100);
                                }
                            }, 100);
                        }
                    }, 100);
                }
                else if (position == 1){
                    showpickMode(v);
                }
            }
        });

        faveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (position != 0 && position != 1) {
                    if (!Requester.getIsFav(position-2)) {
                        faveBtn.setImageResource(R.drawable.ic_star_white_48dp);
                    } else {
                        faveBtn.setImageResource(R.drawable.ic_star_border_white_48dp);
                    }

                    Requester.ToggleIsFav(position-2);

                    Favorites = new ArrayList<>();
                    for (int i = 0; i < Requester.getArrQuality().length; i++) {
                        if (Requester.getIsFav(i))
                            Favorites.add(Requester.getArrCh()[i]);
                    }
                    ArrayAdapter<String> FavAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, Favorites);
                    Favs.setAdapter(FavAdapter);
                    Requester.SaveData();
                }
            }
        });

        return view;
    }

    public void showpickMode(View view){
        Intent myIntent = new Intent(context, SwitchActivity.class);
        context.startActivity(myIntent);
    }
}