package com.sandboxcode.trackerappr2.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateActivity extends AppCompatActivity {

    private static final String[] models = {
            "Camry", "Corolla", "Corolla Hatchback", "Supra", "Prius",
            "Prius Prime", "Sienna", "Yaris", "4Runner", "CHR", "Highlander",
            "RAV4", "RAV4 Prime", "Venza", "Tacoma", "Tundra"
    };

    private static final String TAG = "CreateActivity";
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private EditText searchNameEditText;
    private Spinner modelSpinner;
    private EditText yearEditText;
    private EditText trimEditText;
    RangeSlider priceSlider;
    private SearchViewModel searchViewModel;


    private static final ArrayList<String> modelList = new ArrayList<>(Arrays.asList(models));

    private void getDatabaseReferences() {
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getDatabaseReferences();
        instantiateUI();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.models__array, R.layout.create_list_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinner.setAdapter(adapter);

        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

    }

    public void createNewSearch(View v) {

        String searchName = searchNameEditText.getText().toString();
        String model = modelSpinner.getSelectedItem().toString();
        String trim = trimEditText.getText().toString();
        String year = yearEditText.getText().toString();

        List<Float> priceValues = priceSlider.getValues();
        String minPrice = Collections.min(priceValues).toString();
        String maxPrice = Collections.max(priceValues).toString();

//        final String KEY = databaseRef.child("queries").child(mAuth.getCurrentUser().getUid()).push().getKey();
//        SearchModel searchModel = new SearchModel(KEY, searchName, model, trim, year, minPrice, maxPrice);
//        WebScraper scraper = new WebScraper(this, searchModel, databaseRef, mAuth.getCurrentUser().getUid());
//        scraper.execute();
        searchViewModel.create(searchName, model, trim, year, minPrice, maxPrice);
        finish();
//
//
//
//        databaseRef.child("queries").child(mAuth.getCurrentUser().getUid()).child(KEY)
//                .setValue(searchModel).addOnSuccessListener(this, new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//
//                databaseRef.child("results").child(KEY).setValue(results);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(CreateActivity.this,
//                        "Query failed to save", Toast.LENGTH_SHORT).show();
//            }
//        });
//        finish();
    }

    public void sendMessage(String text) {
        Toast.makeText(CreateActivity.this, text, Toast.LENGTH_SHORT).show();
    }


    private void instantiateUI() {
        searchNameEditText = (EditText) findViewById(R.id.et_search_name);
        modelSpinner = (Spinner) findViewById(R.id.spinner_model);
        trimEditText = (EditText) findViewById(R.id.et_trim);
        yearEditText = (EditText) findViewById(R.id.et_year);
        priceSlider = (RangeSlider) findViewById(R.id.slider_price);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}