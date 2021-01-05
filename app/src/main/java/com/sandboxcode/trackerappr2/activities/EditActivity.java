package com.sandboxcode.trackerappr2.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
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
import com.sandboxcode.trackerappr2.models.SearchModel;
import com.sandboxcode.trackerappr2.viewmodels.EditViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "EditActivity";
    private static final String RESULT_MESSAGE_TAG = "result_message";
    private EditViewModel editViewModel;
    private SearchModel search;

    private AutoCompleteTextView modelSpinner;
    private TextInputEditText searchNameEditText;
    private TextInputEditText trimEditText;
    private RangeSlider yearSlider;
    private RangeSlider priceSlider;
    private SwitchMaterial dealerSwitch;
    private ArrayAdapter<CharSequence> modelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Log.d(TAG, "Edit Activity Started");
        instantiateUI();

        editViewModel = new ViewModelProvider(this).get(EditViewModel.class);
        editViewModel.setSearchId(getIntent().getStringExtra("searchId"));

        editViewModel.getSearch().observe(this, searchModel -> {
            search = searchModel;
            insertCurrentSearchValues(search);
        });
        editViewModel.getToastMessage().observe(this,
                message -> Toast.makeText(this, message, Toast.LENGTH_SHORT));

        editViewModel.getChangesSaved().observe(this, changesSaved -> {
            Intent intent = new Intent();
            intent.putExtra(RESULT_MESSAGE_TAG, "Changes saved successfully.");
            setResult(RESULT_OK, intent);
            finish();
        });
        editViewModel.getErrorMessage().observe(this, message -> {
            Intent intent = new Intent();
            intent.putExtra(RESULT_MESSAGE_TAG, message);
            setResult(RESULT_CANCELED, intent);
            finish();
        });
    }

    public void cancelChanges(View view) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_MESSAGE_TAG, "Search edit cancelled successfully.");
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void saveChanges(View view) {
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

        editViewModel.saveChanges(searchName, model, trim, minYear, maxYear,
                minPrice, maxPrice, allDealerships, search.getCreatedDate());
    }

    public void insertCurrentSearchValues(SearchModel search) {
        searchNameEditText.setText(search.getSearchName());
        trimEditText.setText(search.getTrim());
        modelSpinner.setText(search.getModel(), false); // TODO - false or not?
        dealerSwitch.setChecked(Boolean.parseBoolean(search.getAllDealerships()));
        yearSlider.setValues(
                Float.parseFloat(search.getMinYear()), Float.parseFloat(search.getMaxYear()));
        priceSlider.setValues(
                Float.parseFloat(search.getMinPrice()), Float.parseFloat(search.getMaxPrice()));


    }

    public void instantiateUI() {
        searchNameEditText = findViewById(R.id.edit_edit_name);
        trimEditText = findViewById(R.id.edit_edit_trim);
        modelSpinner = findViewById(R.id.edit_spin_models);
        dealerSwitch = findViewById(R.id.edit_switch_dealerships);
        yearSlider = findViewById(R.id.edit_slider_year);
        priceSlider = findViewById(R.id.edit_slider_price);

        priceSlider.setLabelFormatter(value -> {
            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setMaximumFractionDigits(0);
            format.setCurrency(Currency.getInstance("USD"));
            return format.format(value);
        });

        Resources res = getResources();
        ArrayList<CharSequence> models =
                new ArrayList<>(Arrays.asList(res.getStringArray(R.array.models_array)));
        modelAdapter = new ArrayAdapter<>(this, R.layout.models_list_item, models);
        modelSpinner.setAdapter(modelAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(RESULT_MESSAGE_TAG, "Search edit cancelled successfully.");
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
