package com.sandboxcode.trackerappr2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    private static ArrayList<SearchModel> searchArrayList;
    private LayoutInflater mInflater;
    private Context mContext;
    private int lastPosition;

    /**
     * Holds elements in a view
     */
    static class ViewHolder {
        TextView make;
        TextView model;
        TextView stock;
    }

    public CustomAdapter(Context context, ArrayList<SearchModel> results) {
        searchArrayList = results;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return searchArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String make = searchArrayList.get(position).getMake();
        String model = searchArrayList.get(position).getModel();
        String stock = searchArrayList.get(position).getStock();

        SearchModel searchModel = new SearchModel(make, model, stock);

        final View result;

        ViewHolder holder;

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.search_list_item, parent, false);
            holder = new ViewHolder();
            holder.make = (TextView) convertView.findViewById(R.id.tv_list_item_make);
            holder.model = (TextView) convertView.findViewById(R.id.tv_list_item_model);
            holder.stock = (TextView) convertView.findViewById(R.id.tv_list_item_stock);

            result = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        holder.make.setText(make);
        holder.model.setText(model);
        holder.stock.setText(stock);

        return convertView;
    }
}
