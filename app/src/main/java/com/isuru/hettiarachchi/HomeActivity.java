package com.isuru.hettiarachchi;


import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) throws Resources.NotFoundException {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Load the decrypted JSON data
        loadWordsFromAssets();

        wordAdapter = new WordAdapter(this,wordList);
        recyclerView.setAdapter(wordAdapter);


        //Set up SearchView for filtering words
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
                                              @Override
                                              public boolean onQueryTextChange(String query) {
                                                  List<Word> filteredList = filterList(query);
                                                  // Always check if filteredList is not null before passing to the adapter
                                                  if (filteredList != null) {
                                                      wordAdapter.updateWordList(filteredList); // Update the adapter's data

                                                  } else {
                                                      Log.e("MainActivity", "Filtered list is null");
                                                  }
                                                  return true;
                                              }

                                              @Override
                                              public boolean onQueryTextSubmit(String query) {

                                                  return false;

                                              }

                                          }
        );



    }

    private List<Word> filterList(String query) {
        List<Word> filteredList = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            return wordList;  // If the query is empty, return the original list
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
        // Retrieve json_path from the Intent
        String jsonPath = getIntent().getStringExtra("json_path");
        try {
            // Read encrypted data from the assets folder
            assert jsonPath != null;
            InputStream inputStream = getAssets().open(jsonPath);

            // Use an alternative method to read the InputStream
            String encryptedJson = null;

                encryptedJson = readInputStream(inputStream);


            // Decrypt the encrypted data
            String SECRET_KEY = "your_secret_key";
            String decryptedJson = EncryptionUtil.decrypt(encryptedJson, SECRET_KEY);

            if (decryptedJson.isEmpty()) {
                throw new IllegalStateException("Decrypted JSON is null or empty");
            }

            // Parse the decrypted JSON into Word objects using Gson
            Gson gson = new Gson();
            Word[] words = gson.fromJson(decryptedJson, Word[].class);

            if (words == null || words.length == 0) {
                throw new IllegalStateException("Parsed JSON resulted in an empty or null Word array");
            }

            // Initialize wordList and add the words to it
            wordList = new ArrayList<>();
            Collections.addAll(wordList, words);

        } catch (IllegalStateException e) {
            Log.e("MainActivity", "Error processing the decrypted JSON", e);
        } catch (JsonSyntaxException e) {
            Log.e("MainActivity", "Error parsing JSON with Gson", e);
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading the encrypted file", e);
        } catch (Exception e) {
            Log.e("MainActivity", "Unexpected error", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private String readInputStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];


        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }

        // Use InputStreamReader to convert byte array to String with proper encoding
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return new String(byteArray, StandardCharsets.UTF_8);
    }

}
