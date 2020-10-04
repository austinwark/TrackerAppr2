package com.sandboxcode.trackerappr2.adapters.Result;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.DetailFragment;
import com.sandboxcode.trackerappr2.models.ResultModel;

import org.parceler.Parcels;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ResultsAdapterOld extends BaseAdapter {

    private final String TAG = "ResultAdapter";
    private static ArrayList<ResultModel> resultArrayList;
    private ResultModel currentResult;
    private LayoutInflater mInflater;
    private Context mContext;
    FragmentManager fm;
    private String searchId;
    private int lastPosition;
    private NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

    /**
     * Holds elements in a view
     */
    static class ViewHolder {
        TextView title;
        TextView price;
        TextView stock;
        Button details;
    }

    public ResultsAdapterOld(Context context, ArrayList<ResultModel> results, FragmentManager fm, String searchId) {
        resultArrayList = results;
        mContext = context;
        this.fm = fm;
        this.searchId = searchId;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return resultArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return resultArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        currentResult = resultArrayList.get(position);
        String title = currentResult.getTitle();
        String price = numberFormat.format(Long.parseLong(currentResult.getPrice()));
        String stock = currentResult.getStock();
        final View result;

        ViewHolder holder;

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.result_list_item, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.tv_result_item_title);
            holder.price = (TextView) convertView.findViewById(R.id.tv_result_item_price);
            holder.stock = (TextView) convertView.findViewById(R.id.tv_result_item_stock);
            holder.details = (Button) convertView.findViewById(R.id.button_view_details);


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

//        detailButton.setOnClickListener(ResultsFragment.detailClickListener);
        holder.title.setText(title);
        holder.price.setText(price);
        holder.stock.setText(stock);
        holder.details.setOnClickListener(viewDetailsOnClick);

        return convertView;
    }

    private View.OnClickListener viewDetailsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent().getParent();
            final int position = listView.getPositionForView(parentRow);
            ResultModel result = resultArrayList.get(position);

            Bundle args = new Bundle();
            args.putParcelable("RESULT", Parcels.wrap(result));
            args.putString("SEARCH_ID", searchId);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.main_fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }
    };
}
