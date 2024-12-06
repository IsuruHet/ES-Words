package com.isuru.hettiarachchi;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.isuru.hettiarachchi.adapters.WordAdapter;
import com.isuru.hettiarachchi.models.Word;
import com.isuru.hettiarachchi.utils.EncryptionUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private List<Word> wordList = new ArrayList<>();
    private WordAdapter wordAdapter;
    private SearchView searchView;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) throws Resources.NotFoundException {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load decrypted JSON data
        loadWordsFromAssets();

        // Initialize adapter and set it to RecyclerView
        wordAdapter = new WordAdapter(this, wordList);
        recyclerView.setAdapter(wordAdapter);

        // Initialize SearchView and set up the query listener
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                List<Word> filteredList = filterList(query);
                if (filteredList != null) {
                    wordAdapter.updateWordList(filteredList);
                } else {
                    Log.e("HomeActivity", "Filtered list is null");
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Clear focus and hide the keyboard when returning to this activity
        if (searchView != null) {
            searchView.clearFocus();
        }
        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }

    private List<Word> filterList(String query) {
        List<Word> filteredList = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            return wordList; // If the query is empty, return the original list
        }

        for (Word word : wordList) {
            if (word != null && word.getTerm() != null) {
                // Perform a case-insensitive match from left to right
                if (word.getTerm().toLowerCase().startsWith(query.toLowerCase())) {
                    filteredList.add(word);
                }
            }
        }
        return filteredList;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void loadWordsFromAssets() {
        String jsonPath = getIntent().getStringExtra("json_path");
        try {
            assert jsonPath != null;
            InputStream inputStream = getAssets().open(jsonPath);
            String encryptedJson = readInputStream(inputStream);

            String SECRET_KEY = "your_secret_key";
            String decryptedJson = EncryptionUtil.decrypt(encryptedJson, SECRET_KEY);

            if (decryptedJson.isEmpty()) {
                throw new IllegalStateException("Decrypted JSON is null or empty");
            }

            Gson gson = new Gson();
            Word[] words = gson.fromJson(decryptedJson, Word[].class);

            if (words == null || words.length == 0) {
                throw new IllegalStateException("Parsed JSON resulted in an empty or null Word array");
            }

            wordList = new ArrayList<>();
            Collections.addAll(wordList, words);

        } catch (Exception e) {
            Log.e("HomeActivity", "Error loading words", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private String readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return new String(byteArray,StandardCharsets.UTF_8);
    }
}
