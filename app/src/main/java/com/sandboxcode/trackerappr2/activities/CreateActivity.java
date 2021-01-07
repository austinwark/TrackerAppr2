package com.sandboxcode.trackerappr2.activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.viewmodels.CreateViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

public class CreateActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String TAG = "CreateActivity";
    private CreateViewModel createViewModel;

    private AutoCompleteTextView modelSpinner;
    private TextInputEditText searchNameEditText;
    private TextInputEditText trimEditText;
    private RangeSlider yearSlider;
    private RangeSlider priceSlider;
    private SwitchMaterial dealerSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        instantiateUI();

        createViewModel = new ViewModelProvider(this).get(CreateViewModel.class);

        createViewModel.getCreateCancelled().observe(this, isCancelled -> finish());

        createViewModel.getToastMessage().observe(this, message ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show());

    }

    public void createNewSearch(View v) {

        String searchName = searchNameEditText.getText().toString();
        String model = modelSpinner.getText().toString();
        String trim = trimEditText.getText().toString();
        List<Float> yearValues = yearSlider.getValues();
        String minYear = Collections.min(yearValues).toString();
        String maxYear = Collections.max(yearValues).toString();
        List<Float> priceValues = priceSlider.getValues();
        String minPrice = Collections.min(priceValues).toString();
        String maxPrice = Collections.max(priceValues).toString();
        String allDealerships = String.valueOf(dealerSwitch.isChecked());
        createViewModel.create(searchName, model, trim, minYear, maxYear,
                minPrice, maxPrice, allDealerships);
        finish();
    }

    private void instantiateUI() {

        searchNameEditText = findViewById(R.id.et_search_name);
        trimEditText = findViewById(R.id.et_trim);
        modelSpinner = findViewById(R.id.spinner_models);
        dealerSwitch = findViewById(R.id.create_switch_dealerships);
        yearSlider = findViewById(R.id.slider_year);
        priceSlider = findViewById(R.id.slider_price);

        priceSlider.setLabelFormatter(value -> {
            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setMaximumFractionDigits(0);
            format.setCurrency(Currency.getInstance("USD"));
            return format.format(value);
        });

        Resources res = getResources();
        ArrayList<CharSequence> models =
                new ArrayList<>(Arrays.asList(res.getStringArray(R.array.models_array)));
        ArrayAdapter<CharSequence> modelAdapter = new ArrayAdapter<>(this, R.layout.models_list_item, models);
        modelSpinner.setAdapter(modelAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        createViewModel.handleOnOptionsItemSelected(item.getItemId());
        return super.onOptionsItemSelected(item);
    }
}