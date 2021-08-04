package com.sandboxcode.trackerappr2.utils;

import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.models.SearchModel;

import java.util.List;

public interface AsyncResponse {
    void processResults(List<ResultModel> results, SearchModel search);
}
