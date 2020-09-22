package com.sandboxcode.trackerappr2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateActivity extends AppCompatActivity {

    private static final String[] models = {
            "Camry", "Corolla", "Corolla Hatchback", "Supra", "Prius",
            "Prius Prime", "Sienna", "Yaris", "4Runner", "CHR", "Highlander",
            "RAV4", "RAV4 Prime", "Venza", "Tacoma", "Tundra"
    };

    private Spinner modelSpinner;
    private EditText minPriceEditText;
    private EditText maxPriceEditText;
    private EditText minYearEditText;
    private EditText maxYearEditText;

    private static final ArrayList<String> modelList = new ArrayList<>(Arrays.asList(models));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        instantiateUI();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.models__array, R.layout.create_list_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinner.setAdapter(adapter);

    }

    public void createNewSearch(View v) {

        String model = modelSpinner.getSelectedItem().toString();
        int minPrice = Integer.parseInt(minPriceEditText.getText().toString());
        int maxPrice = Integer.parseInt(maxPriceEditText.getText().toString());
        int minYear = Integer.parseInt(minYearEditText.getText().toString());
        int maxYear = Integer.parseInt(maxYearEditText.getText().toString());

        Log.d("CreateActivity:", (model +" " + minPrice + " " + maxPrice + " " + minYear + " " + maxYear));
    }

    private void instantiateUI() {
        modelSpinner = (Spinner) findViewById(R.id.spinner_model);
        minPriceEditText = (EditText) findViewById(R.id.et_min_price);
        maxPriceEditText = (EditText) findViewById(R.id.et_max_price);
        minYearEditText = (EditText) findViewById(R.id.et_min_year);
        maxYearEditText = (EditText) findViewById(R.id.et_max_year);
    }
}