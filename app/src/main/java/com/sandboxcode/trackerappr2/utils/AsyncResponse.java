package com.sandboxcode.trackerappr2.utils;

import com.sandboxcode.trackerappr2.models.ResultModel;

import java.util.ArrayList;

public interface AsyncResponse {
    void processResults(ArrayList<ResultModel> results, String searchId);
}
