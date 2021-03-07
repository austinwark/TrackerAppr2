package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.sandboxcode.trackerappr2.utils.SingleLiveEvent;

import java.util.ArrayList;

public class OnBoardingViewModel extends AndroidViewModel {

    private SingleLiveEvent<ArrayList<Integer>> imageIds = new SingleLiveEvent<>();

    public OnBoardingViewModel(Application application) {
        super(application);
    }

    public SingleLiveEvent<ArrayList<Integer>> getImageIds() {
        return imageIds;
    }

    public void saveImages(int... images) {
        ArrayList<Integer> imageList = new ArrayList<>();
        for (int image : images)
            imageList.add(image);
        imageIds.setValue(imageList);
    }

    public int getImageId(int position) {
        return imageIds.getValue().get(position);
    }

}
