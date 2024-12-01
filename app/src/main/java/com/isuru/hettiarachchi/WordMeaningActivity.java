package com.isuru.hettiarachchi;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.nl.translate.Translation;

import java.util.Locale;

public class WordMeaningActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private Translator englishToOtherTranslator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_meaning);

        TextView wordTextView = findViewById(R.id.wordTextView);
        TextView meaningTextView = findViewById(R.id.meaningTextView);
        ImageView speakerIcon = findViewById(R.id.speakerIcon);
        Button translateButton = findViewById(R.id.translateButton);  // Get the Translate button

        // Get the word and meaning from the intent
        String word = getIntent().getStringExtra("word");
        String meaning = getIntent().getStringExtra("meaning");

        // Handle null values to avoid crashes
        if (word == null) {
            word = "Unknown Word";
        }
        if (meaning == null) {
            meaning = "No meaning available.";
        }

        // Set the text in the TextViews
        wordTextView.setText(word);
        meaningTextView.setText(meaning);

        // Extract only the first word (wordOnly)
        String wordOnly = word.split(" ")[0]; // Fallback to the full word if no second word exists

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
            }
        });

        // Set up the speaker icon click listener
        speakerIcon.setOnClickListener(v -> {
            if (!wordOnly.isEmpty()) {
                textToSpeech.speak(wordOnly, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        // Initialize Translator (English to Tamil in this case)
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.SPANISH)  // Change this as needed
                .build();

        englishToOtherTranslator = Translation.getClient(options);

        // Download the language model if needed
        DownloadConditions conditions = new DownloadConditions.Builder()
                .build();

        String finalWord = word;
        englishToOtherTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(unused -> {
                    // Model downloaded successfully
                    Log.d("Translation", "Language models downloaded.");
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Log.e("Translation", "Model download failed: " + e.getMessage());
                    retryModelDownload();
                });

        // Set Translate Button click listener
        translateButton.setOnClickListener(v -> {
            // Translate the word when the button is clicked
            translateWord(finalWord);
        });
    }

    private void retryModelDownload() {
        // Retry model download on failure
        Log.d("Translation", "Retrying model download...");
        Toast.makeText(this, "Retrying model download...", Toast.LENGTH_SHORT).show();
        // Retry downloading the language model
        englishToOtherTranslator.downloadModelIfNeeded()
                .addOnSuccessListener(unused -> Log.d("Translation", "Model download succeeded"))
                .addOnFailureListener(e -> {
                    // Final failure notification to user
                    Log.e("Translation", "Model download failed again: " + e.getMessage());
                    Toast.makeText(this, "Failed to download language model. Please check your connection.", Toast.LENGTH_LONG).show();
                });
    }

    @SuppressLint("SetTextI18n")
    private void translateWord(String word) {
        // Translate the word
        englishToOtherTranslator.translate(word)
                .addOnSuccessListener(translatedText -> {
                    // Ensure UI update happens on the main thread
                    runOnUiThread(() -> {
                        // Set the translated word in the TextView
                        TextView translatedWordTextView = findViewById(R.id.translateWordTextView);
                        translatedWordTextView.setText(translatedText);
                        //Log.d("Translation", "Translated Text: " + translatedText);

                    });
                })
                .addOnFailureListener(e -> {
                    // Handle translation failure
                    Log.e("Translation", "Translation failed: " + e.getMessage());
                    Toast.makeText(this, "Translation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    protected void onDestroy() {
        // Release TTS resources when the activity is destroyed
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        // Release translator when done
        if (englishToOtherTranslator != null) {
            englishToOtherTranslator.close();
        }

        super.onDestroy();
    }
}
