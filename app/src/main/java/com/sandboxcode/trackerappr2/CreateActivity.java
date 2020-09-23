package com.sandboxcode.trackerappr2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        databaseRef = FirebaseDatabase.getInstance().getReference()
                .child("queries").child(mAuth.getCurrentUser().getUid());
    }


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
        String trim = trimEditText.getText().toString();
        String year = yearEditText.getText().toString();

        Log.d("CreateActivity:", (model + " " + trim + " " + year));

        SearchModel searchModel = new SearchModel(model, trim, year);
        String key = databaseRef.push().getKey();
        databaseRef.child(key).setValue(searchModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CreateActivity.this,
                        "Query saved successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateActivity.this,
                        "Query failed to save", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void instantiateUI() {
        modelSpinner = (Spinner) findViewById(R.id.spinner_model);
        trimEditText = (EditText) findViewById(R.id.et_trim);
        yearEditText = (EditText) findViewById(R.id.et_year);
    }
}