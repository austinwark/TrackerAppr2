package com.sandboxcode.trackerappr2.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sandboxcode.trackerappr2.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OnBoardingFragment extends Fragment {

    private static final String TAG = OnBoardingFragment.class.getSimpleName();
    public static final String ARG_IMAGE_ID = "image_id";

    private int imageId;
    private ImageView imageView;

    public OnBoardingFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static OnBoardingFragment newInstance(int imageId) {
        OnBoardingFragment fragment = new OnBoardingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_ID, imageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
            imageId = args.getInt(ARG_IMAGE_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_on_boarding, container, false);
    }

    @Override
    public void onViewCreated(@Nonnull View view, @Nullable Bundle savedInstanceState) {
        imageView = view.findViewById(R.id.on_boarding_image);
        imageView.setImageDrawable(getResources().getDrawable(imageId));
    }
}