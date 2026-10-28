
package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText etCountry, etCapital, etCurrency;
    Button btnAdd, btnView, btnUpdate, btnDelete;
    ListView listCountries;

    DatabaseHelper dbHelper;
    ArrayList<Integer> countryIds;
    ArrayList<String> countryList;
    ArrayAdapter<String> adapter;

    int selectedId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCountry = findViewById(R.id.etCountry);
        etCapital = findViewById(R.id.etCapital);
        etCurrency = findViewById(R.id.etCurrency);

        btnAdd = findViewById(R.id.btnAdd);
        btnView = findViewById(R.id.btnView);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        listCountries = findViewById(R.id.listCountries);

        dbHelper = new DatabaseHelper(this);

        countryIds = new ArrayList<>();
        countryList = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                countryList
        );

        listCountries.setAdapter(adapter);

        // ADD COUNTRY
        btnAdd.setOnClickListener(v -> {
            String country = etCountry.getText().toString().trim();
            String capital = etCapital.getText().toString().trim();
            String currency = etCurrency.getText().toString().trim();

            if (!validate(country, capital, currency)) return;

            if (dbHelper.insertCountry(country, capital, currency)) {
                showMessage("Country added successfully");
                clearFields();
                selectedId = -1;
                loadCountries();
            } else {
                showMessage("Failed to add country");
            }
        });

        // VIEW COUNTRIES
        btnView.setOnClickListener(v -> loadCountries());

        // SELECT A COUNTRY
        listCountries.setOnItemClickListener(
                (parent, view, position, id) -> {

                    selectedId = countryIds.get(position);

                    Cursor cursor = dbHelper.getCountryById(selectedId);

                    try {
                        if (cursor.moveToFirst()) {
                            etCountry.setText(cursor.getString(
                                    cursor.getColumnIndexOrThrow("country")));

                            etCapital.setText(cursor.getString(
                                    cursor.getColumnIndexOrThrow("capital")));

                            etCurrency.setText(cursor.getString(
                                    cursor.getColumnIndexOrThrow("currency")));
                        }
                    } finally {
                        cursor.close();
                    }

                    showMessage("Country selected");
                });

        // UPDATE COUNTRY
        btnUpdate.setOnClickListener(v -> {
            if (selectedId == -1) {
                showMessage("Select a country first");
                return;
            }

            String country = etCountry.getText().toString().trim();
            String capital = etCapital.getText().toString().trim();
            String currency = etCurrency.getText().toString().trim();

            if (!validate(country, capital, currency)) return;

            if (dbHelper.updateCountry(
                    selectedId, country, capital, currency)) {

                showMessage("Country updated successfully");
                selectedId = -1;
                clearFields();
                loadCountries();
            } else {
                showMessage("Update failed");
            }
        });

        // DELETE COUNTRY
        btnDelete.setOnClickListener(v -> {
            if (selectedId == -1) {
                showMessage("Select a country first");
                return;
            }

            if (dbHelper.deleteCountry(selectedId)) {
                showMessage("Country deleted successfully");
                selectedId = -1;
                clearFields();
                loadCountries();
            } else {
                showMessage("Delete failed");
            }
        });

        loadCountries();
    }

    // READ ALL COUNTRIES
    private void loadCountries() {
        countryIds.clear();
        countryList.clear();

        Cursor cursor = dbHelper.getAllCountries();

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id"));

                String country = cursor.getString(
                        cursor.getColumnIndexOrThrow("country"));

                String capital = cursor.getString(
                        cursor.getColumnIndexOrThrow("capital"));

                String currency = cursor.getString(
                        cursor.getColumnIndexOrThrow("currency"));

                countryIds.add(id);

                countryList.add(
                        "Country: " + country +
                                "\nCapital: " + capital +
                                "\nCurrency: " + currency);
            }
        } finally {
            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }

    private boolean validate(String country,
                             String capital,
                             String currency) {
        if (country.isEmpty() ||
                capital.isEmpty() ||
                currency.isEmpty()) {
            showMessage("Please fill in all fields");
            return false;
        }
        return true;
    }

    private void clearFields() {
        etCountry.setText("");
        etCapital.setText("");
        etCurrency.setText("");
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
