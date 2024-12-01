package com.isuru.hettiarachchi;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.nl.translate.Translation;

import java.util.Locale;

public class TranslateActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private Translator englishToSpanishTranslator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        // Bind views
        EditText editTextEnglish = findViewById(R.id.editTextEnglish);
        TextView textViewTranslation = findViewById(R.id.textViewTranslation);
        ImageView speakerIconEnglish = findViewById(R.id.speakerIconEnglish);
        ImageView speakerIconTranslation = findViewById(R.id.speakerIconTranslation);
        Button buttonTranslate = findViewById(R.id.buttonTranslate);

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US); // Set to English
            }
        });

        // Initialize Translator (English to Spanish)
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.SPANISH)
                .build();

        englishToSpanishTranslator = Translation.getClient(options);

        // Download the language model if needed
        DownloadConditions conditions = new DownloadConditions.Builder().build();
        englishToSpanishTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(unused -> Log.d("TranslateActivity", "Model downloaded successfully."))
                .addOnFailureListener(e -> Log.e("TranslateActivity", "Model download failed: " + e.getMessage()));

        // Set up the speaker icon click listeners
        speakerIconEnglish.setOnClickListener(v -> {
            String englishText = editTextEnglish.getText().toString();
            if (!englishText.isEmpty()) {
                textToSpeech.speak(englishText, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        speakerIconTranslation.setOnClickListener(v -> {
            String translatedText = textViewTranslation.getText().toString();
            if (!translatedText.isEmpty()) {
                textToSpeech.speak(translatedText, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        // Set the Translate button click listener
        buttonTranslate.setOnClickListener(v -> {
            String englishText = editTextEnglish.getText().toString();
            if (!englishText.isEmpty()) {
                translateWord(englishText, textViewTranslation);
            } else {
                Toast.makeText(TranslateActivity.this, "Please enter text to translate", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void translateWord(String word, TextView textViewTranslation) {
        // Set the translated text into the TextView
        englishToSpanishTranslator.translate(word)
                .addOnSuccessListener(textViewTranslation::setText)
                .addOnFailureListener(e -> {
                    // Handle translation failure
                    Log.e("TranslateActivity", "Translation failed: " + e.getMessage());
                    Toast.makeText(TranslateActivity.this, "Translation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (englishToSpanishTranslator != null) {
            englishToSpanishTranslator.close();
        }

        super.onDestroy();
    }
}
