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
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.viewmodels.CreateViewModel;

import java.util.Collections;
import java.util.List;

public class CreateActivity extends AppCompatActivity {

    private static final String TAG = "CreateActivity";
    private EditText searchNameEditText;
    private Spinner modelSpinner;
    private EditText yearEditText;
    private EditText trimEditText;
    RangeSlider priceSlider;
    private CreateViewModel createViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        instantiateUI();

        createViewModel = new ViewModelProvider(this).get(CreateViewModel.class);

        createViewModel.getCreateCancelled().observe(this, isCancelled -> finish());

        createViewModel.getToastMessage().observe(this, message ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());

    }

    public void createNewSearch(View v) {

        String searchName = searchNameEditText.getText().toString();
        String model = modelSpinner.getSelectedItem().toString();
        String trim = trimEditText.getText().toString();
        String year = yearEditText.getText().toString();

        List<Float> priceValues = priceSlider.getValues();
        String minPrice = Collections.min(priceValues).toString();
        String maxPrice = Collections.max(priceValues).toString();

        createViewModel.create(searchName, model, trim, year, minPrice, maxPrice);
        finish();

    }

    private void instantiateUI() {
        searchNameEditText = findViewById(R.id.et_search_name);
        modelSpinner = findViewById(R.id.spinner_model);
        trimEditText = findViewById(R.id.et_trim);
        yearEditText = findViewById(R.id.et_year);
        priceSlider = findViewById(R.id.slider_price);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.models__array, R.layout.create_list_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinner.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        createViewModel.handleOnOptionsItemSelected(item.getItemId());
        return super.onOptionsItemSelected(item);
    }
}