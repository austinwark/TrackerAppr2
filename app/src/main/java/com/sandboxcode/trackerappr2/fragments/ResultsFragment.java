package com.sandboxcode.trackerappr2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.adapters.decorators.ShadowVerticalSpaceItemDecorator;
import com.sandboxcode.trackerappr2.adapters.decorators.VerticalSpaceItemDecorator;
import com.sandboxcode.trackerappr2.adapters.result.ResultsAdapter;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.viewmodels.MainSharedViewModel;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultsFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment {

    private static final String TAG = "ResultsFragment";
    private Context activityContext;

    private MainSharedViewModel viewModel;

    private RecyclerView resultListView;
    private List<ResultModel> resultList = new ArrayList<>();

    private String searchId;
    private ResultsAdapter adapter;

    public ResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ARRAY1", Parcels.wrap(resultList));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchId = getArguments().getString("ID");
        }

        activityContext = getActivity().getApplicationContext();
        FragmentManager fragmentManager = getParentFragmentManager();
        // TODO - add searchID
        adapter = new ResultsAdapter(activityContext, R.layout.result_list_item, fragmentManager, searchId);

        viewModel = new ViewModelProvider(requireActivity()).get(MainSharedViewModel.class);
        viewModel.getSearchResults(searchId)
                .observe(this, results -> adapter.setResults(results));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Results");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activityContext);

        int verticalSpacing = 20;
        VerticalSpaceItemDecorator itemDecorator = new VerticalSpaceItemDecorator(verticalSpacing);
        ShadowVerticalSpaceItemDecorator shadowItemDecorator = new ShadowVerticalSpaceItemDecorator(activityContext, R.drawable.drop_shadow);

        resultListView = view.findViewById(R.id.results_view);

        resultListView.setHasFixedSize(true);
        resultListView.setLayoutManager(layoutManager);
        resultListView.addItemDecoration(shadowItemDecorator);
        resultListView.addItemDecoration(itemDecorator);

        resultListView.setAdapter(adapter);
    }

    private View.OnClickListener detailClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            Log.d(TAG, String.valueOf(position));
        }
    };
}
