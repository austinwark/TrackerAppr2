package com.sandboxcode.trackerappr2.utils;

import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.models.SearchModel;

import java.util.ArrayList;
import java.util.Map;

public interface DailyAsyncResponse {
    void processResults(Map<String, ArrayList<ResultModel>> resultsMap);
}
