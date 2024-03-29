package com.sandboxcode.trackerappr2.room_components;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sandboxcode.trackerappr2.models.SearchModel;

import java.util.List;

@Dao
public interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertSearches(SearchModel... searches);

    /* Returns an integer representing the number of rows that were updated successfully */
    @Update
    public int updateSearch(SearchModel search);

    @Update
    public void updateSearches(SearchModel... searches);

    @Delete
    public void deleteSearches(SearchModel... searches);

    @Query("SELECT * FROM search_table WHERE user_id = :userId")
    public List<SearchModel> loadAllSearchesOnce(String userId);

    @Query("SELECT * FROM search_table WHERE user_id = :userId")
    public LiveData<List<SearchModel>> loadAllSearches(String userId);

    @Query("SELECT * FROM search_table WHERE id = :searchId")
    public LiveData<SearchModel> loadSingleSearch(String searchId);

    @Query("SELECT * FROM search_table WHERE id = :searchId")
    public SearchModel getSingleSearch(String searchId);

    @Query("DELETE FROM search_table WHERE id = :searchId")
    public void deleteById(String searchId);

}
