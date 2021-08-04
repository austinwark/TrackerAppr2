package com.sandboxcode.trackerappr2.room_components;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.models.SearchModel;

import java.util.List;

@Dao
public interface ResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertResults(ResultModel... results);

    @Update
    public void updateResults(ResultModel... results);

    @Delete
    public void deleteResults(ResultModel... results);

    @Query("SELECT * FROM result_table")
    public List<ResultModel> loadAllResultsOnce();

    @Query("SELECT * FROM result_table")
    public LiveData<List<ResultModel>> loadAllResults();

    @Query("SELECT * FROM result_table WHERE vin = :vin")
    public ResultModel loadSingleResult(String vin);

    @Query("DELETE FROM result_table WHERE vin = :vin")
    public void deleteByVin(String vin);

    @Query("DELETE FROM result_table WHERE search_id = :searchId")
    public void deleteAll(String searchId);

    @Update
    public void updateResult(ResultModel result);

}
