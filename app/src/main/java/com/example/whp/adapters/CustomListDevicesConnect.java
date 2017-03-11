package com.example.whp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.whp.R;
import com.example.whp.models.DevicesConnect;

import java.util.ArrayList;

/**
 * Created by hieuho on 10/03/2017.
 */

public class CustomListDevicesConnect extends BaseAdapter {
    private Context context;
    private ArrayList<DevicesConnect> list;

    public CustomListDevicesConnect(Context context,ArrayList<DevicesConnect> list){
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.customview,null);
        TextView ipAdd = (TextView) convertView.findViewById(R.id.textView);
        DevicesConnect device = list.get(position);
        ipAdd.setText("IP Address: "+device.getIpAddr());
        return convertView;
    }
}
