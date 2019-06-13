package com.example.learn_firebase;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Translate extends AppCompatActivity {

    StringBuilder data=new StringBuilder();
    StringBuilder lang=new StringBuilder();

    ArrayList<String> k_lang=new ArrayList<>();
    ArrayList<String> v_lang=new ArrayList<>();

    TextView action, text, dlang, slang, dresult, result, uslang;
    ProgressBar pbar;
    Button translate;
    Spinner tlang, ulang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        action=findViewById(R.id.action);
        dlang=findViewById(R.id.dlang);
        slang=findViewById(R.id.slang);
        result=findViewById(R.id.result);
        text=findViewById(R.id.text);
        dresult=findViewById(R.id.dresult);
        uslang=findViewById(R.id.uslang);

        pbar=findViewById(R.id.progressBar2);
        translate=findViewById(R.id.translate);
        tlang=findViewById(R.id.tlang);
        ulang=findViewById(R.id.ulang);

        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateText();
            }
        });

        createCodes();
        recognizeText();

    }

    private FirebaseVisionImage getImage() {
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        File file = new File(path);
        FirebaseVisionImage image = null;
        try {
            image = FirebaseVisionImage.fromFilePath(getApplicationContext(), Uri.fromFile(file));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return image;
    }

    private void translateText() {

        String scode=k_lang.get(v_lang.indexOf(ulang.getSelectedItem().toString()));
        String tcode=k_lang.get(v_lang.indexOf(tlang.getSelectedItem().toString()));
        try {
            FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                    .setSourceLanguage(FirebaseTranslateLanguage.languageForLanguageCode(scode))
                    .setTargetLanguage(FirebaseTranslateLanguage.languageForLanguageCode(tcode))
                    .build();

            final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

            FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
            translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            translator.translate(data.toString())
                                    .addOnSuccessListener(
                                            new OnSuccessListener<String>() {
                                                @Override
                                                public void onSuccess(@NonNull String translatedText) {
                                                    result.setText(translatedText);
                                                    result.setTextSize(16);
                                                    result.setVisibility(View.VISIBLE);
                                                }
                                            })
                                    .addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Translate.this, "Error in translation!!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                        }
                    });
        }
        catch (Exception e){
            Toast.makeText(this, "Translation Not Available", Toast.LENGTH_SHORT).show();
        }

    }

    private void processText(FirebaseVisionText result) {
        List<String> rLang_codes = new ArrayList<>();
        for (FirebaseVisionText.TextBlock block : result.getTextBlocks()) {
            List<RecognizedLanguage> languages = block.getRecognizedLanguages();
            data.append(block.getText().trim() + " ");
            for (FirebaseVisionText.Line line : block.getLines()) {
                for (FirebaseVisionText.Element element : line.getElements()) {
                }
            }
            for (RecognizedLanguage language : languages) {
                if (!rLang_codes.contains(language.getLanguageCode()))
                    rLang_codes.add(language.getLanguageCode());
            }
        }

        ArrayList<String> rdlang=new ArrayList<>();
        for (String code : rLang_codes) {
            if (k_lang.contains(code)) {
                rdlang.add(v_lang.get(k_lang.indexOf(code)));
                lang.append(v_lang.get(k_lang.indexOf(code)) + ", ");
            }
        }
        try {
            lang.deleteCharAt(lang.length() - 1);
            lang.deleteCharAt(lang.length() - 1);
        } catch (Exception e) {
        }

        dresult.setText(lang);
        dlang.setVisibility(View.VISIBLE);
        uslang.setVisibility(View.VISIBLE);
        dresult.setVisibility(View.VISIBLE);
        dresult.setTextSize(16);
        text.setTextSize(16);
        text.setText(data);
        text.setVisibility(View.VISIBLE);
        action.setText("Recognized Text In Image");

        ArrayAdapter t_adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, v_lang);
        tlang.setAdapter(t_adapter);

        ArrayAdapter s_adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, rdlang);
        ulang.setAdapter(s_adapter);

        tlang.setVisibility(View.VISIBLE);
        slang.setVisibility(View.VISIBLE);
        ulang.setVisibility(View.VISIBLE);
        translate.setVisibility(View.VISIBLE);

    }

    private void recognizeText(){
        Toast.makeText(this, "Processing...", Toast.LENGTH_SHORT).show();
        FirebaseVisionCloudDocumentRecognizerOptions options = new FirebaseVisionCloudDocumentRecognizerOptions.Builder()
                        .setLanguageHints(Arrays.asList("en", "hi", "bn", "gu", "ne", "mr", "pa", "ta", "te"))
                        .build();

        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getCloudTextRecognizer();
        Task<FirebaseVisionText> result = detector.processImage(getImage());
        result.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                pbar.setVisibility(View.GONE);
                processText(firebaseVisionText);
            }
        });
        result.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Translate.this, "Failed to recognize text in image!!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Translate.this, MainActivity.class));
            }
        });
    }

    private void createCodes(){
        k_lang.add("af");
        k_lang.add("sq");
        k_lang.add("ar");
        k_lang.add("hy");
        k_lang.add("be");
        k_lang.add("bn");
        k_lang.add("bg");
        k_lang.add("ca");
        k_lang.add("zh");
        k_lang.add("hr");
        k_lang.add("cs");
        k_lang.add("da");
        k_lang.add("nl");
        k_lang.add("en");
        k_lang.add("et");
        k_lang.add("fil");
        k_lang.add("tl");
        k_lang.add("fi");
        k_lang.add("fr");
        k_lang.add("de");
        k_lang.add("el");
        k_lang.add("gu");
        k_lang.add("iw");
        k_lang.add("hi");
        k_lang.add("hu");
        k_lang.add("is");
        k_lang.add("id");
        k_lang.add("it");
        k_lang.add("ja");
        k_lang.add("kn");
        k_lang.add("km");
        k_lang.add("ko");
        k_lang.add("lo");
        k_lang.add("lv");
        k_lang.add("lt");
        k_lang.add("mk");
        k_lang.add("ms");
        k_lang.add("ml");
        k_lang.add("mr");
        k_lang.add("ne");
        k_lang.add("no");
        k_lang.add("fa");
        k_lang.add("pl");
        k_lang.add("pt");
        k_lang.add("pa");
        k_lang.add("ro");
        k_lang.add("ru");
        k_lang.add("ru-PETR1708");
        k_lang.add("sr");
        k_lang.add("sr-Latn");
        k_lang.add("sk");
        k_lang.add("sl");
        k_lang.add("es");
        k_lang.add("sv");
        k_lang.add("ta");
        k_lang.add("te");
        k_lang.add("th");
        k_lang.add("tr");
        k_lang.add("uk");
        k_lang.add("vi");
        k_lang.add("yi");

        v_lang.add("Afrikaans");
        v_lang.add( "Albanian");
        v_lang.add("Arabic");
        v_lang.add( "Armenian");
        v_lang.add( "Belorussian");
        v_lang.add( "Bengali");
        v_lang.add( "Bulgarian");
        v_lang.add( "Catalan");
        v_lang.add( "Chinese");
        v_lang.add( "Croatian");
        v_lang.add( "Czech");
        v_lang.add( "Danish");
        v_lang.add( "Dutch");
        v_lang.add( "English");
        v_lang.add( "Estonian");
        v_lang.add( "Filipino");
        v_lang.add( "Filipino");
        v_lang.add( "Finnish");
        v_lang.add( "French");
        v_lang.add( "German");
        v_lang.add( "Greek");
        v_lang.add( "Gujarati");
        v_lang.add( "Hebrew");
        v_lang.add( "Hindi");
        v_lang.add( "Hungarian");
        v_lang.add( "Icelandic");
        v_lang.add( "Indonesian");
        v_lang.add( "Italian");
        v_lang.add( "Japanese");
        v_lang.add( "Kannada");
        v_lang.add( "Khmer");
        v_lang.add( "Korean");
        v_lang.add( "Lao");
        v_lang.add( "Latvian");
        v_lang.add( "Lithuanian");
        v_lang.add( "Macedonian");
        v_lang.add( "Malay");
        v_lang.add( "Malayalam");
        v_lang.add( "Marathi");
        v_lang.add( "Nepali");
        v_lang.add( "Norwegian");
        v_lang.add( "Persian");
        v_lang.add( "Polish");
        v_lang.add( "Portuguese");
        v_lang.add( "Punjabi");
        v_lang.add( "Romanian");
        v_lang.add( "Russian");
        v_lang.add( "Russian");
        v_lang.add( "Serbian");
        v_lang.add( "Serbian");
        v_lang.add( "Slovak");
        v_lang.add("Slovenian");
        v_lang.add( "Spanish");
        v_lang.add( "Swedish");
        v_lang.add( "Tamil");
        v_lang.add( "Telugu");
        v_lang.add( "Thai");
        v_lang.add("Turkish");
        v_lang.add( "Ukrainian");
        v_lang.add( "Vietnamese");
        v_lang.add( "Yiddish");
    }
}
