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
        TextView model;
        TextView trim;
        TextView year;
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
        String model = searchArrayList.get(position).getModel();
        String trim = searchArrayList.get(position).getTrim();
        String year = searchArrayList.get(position).getYear();

        final View result;

        ViewHolder holder;

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.search_list_item, parent, false);
            holder = new ViewHolder();
            holder.model = (TextView) convertView.findViewById(R.id.tv_list_item_model);
            holder.trim = (TextView) convertView.findViewById(R.id.tv_list_item_trim);
            holder.year = (TextView) convertView.findViewById(R.id.tv_list_item_year);

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

        holder.model.setText(model);
        holder.trim.setText(trim);
        holder.year.setText(year);


        return convertView;
    }
}
