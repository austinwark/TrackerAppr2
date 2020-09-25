package com.sandboxcode.trackerappr2;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateActivity extends AppCompatActivity {

    private static final String[] models = {
            "Camry", "Corolla", "Corolla Hatchback", "Supra", "Prius",
            "Prius Prime", "Sienna", "Yaris", "4Runner", "CHR", "Highlander",
            "RAV4", "RAV4 Prime", "Venza", "Tacoma", "Tundra"
    };

    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private Spinner modelSpinner;
    private EditText yearEditText;
    private EditText trimEditText;


    private static final ArrayList<String> modelList = new ArrayList<>(Arrays.asList(models));

    private void getDatabaseReferences() {
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("queries")
                .child(mAuth.getCurrentUser().getUid());
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

    }

    public void createNewSearch(View v) throws IOException {

        String model = modelSpinner.getSelectedItem().toString();
        String trim = trimEditText.getText().toString();
        String year = yearEditText.getText().toString();


        SearchModel searchModel = new SearchModel(model, trim, year);
        Log.d("CREATEACTIVITY: ", "EXECUTE");
        WebScraper scraper = new WebScraper(model, trim, year);
        scraper.execute();


//        String key = databaseRef.push().getKey();
//        databaseRef.child(key).setValue(searchModel).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(CreateActivity.this,
//                        "Query saved successfully", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(CreateActivity.this,
//                        "Query failed to save", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void instantiateUI() {
        modelSpinner = (Spinner) findViewById(R.id.spinner_model);
        trimEditText = (EditText) findViewById(R.id.et_trim);
        yearEditText = (EditText) findViewById(R.id.et_year);
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