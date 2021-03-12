package com.sandboxcode.trackerappr2.room_components;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sandboxcode.trackerappr2.models.SearchModel;

import java.util.List;

@Dao
public interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SearchModel search);

    @Query("SELECT * FROM search_table")
    LiveData<List<SearchModel>> getAll();

    @Query("SELECT * FROM search_table WHERE id = :searchId")
    LiveData<SearchModel> findById(String searchId);

    @Query("DELETE FROM search_table WHERE id = :searchId")
    void deleteById(String searchId);

}
