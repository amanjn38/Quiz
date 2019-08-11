package com.example.quiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String guesses="settings_numberOfGuesses";
    public static final String animalsTpye = "settings_animalsType";
    public static final String quizBackgroundColor = "setting_quiz_background_color";
    public static final String quizFont = "settings_quiz_font";

    private boolean isSettingsChanged = false;

    static Typeface chunkfive;
    static Typeface fontleroybrown;
    static Typeface wonderbarDemo;

    MainActivityFragment myAnimalQuizFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        chunkfive = Typeface.createFromAsset(getAssets(),"fonts/Chunkfive.otf");
        fontleroybrown = Typeface.createFromAsset(getAssets(),"fonts/FontleroyBrown.ttf");
        wonderbarDemo = Typeface.createFromAsset(getAssets(),"fonts/Wonderbar Demo.otf");

        PreferenceManager.setDefaultValues(MainActivity.this,R.xml.quiz_preferences,false);

        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).registerOnSharedPreferenceChangeListener(settingsChangeListener);


        myAnimalQuizFragment =(MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.quizFragment);

        myAnimalQuizFragment.modifyAnimalGuessRows(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        myAnimalQuizFragment.modifyTypeOfAnimlaInQuiz(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        myAnimalQuizFragment.modifyQuizFont(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        myAnimalQuizFragment.modifyBackgroundColor(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        myAnimalQuizFragment.resetAnimalQuiz();
        isSettingsChanged = false;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener settingsChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            isSettingsChanged= true;
            if(key.equals(guesses)) {

                myAnimalQuizFragment.modifyAnimalGuessRows(sharedPreferences);
                myAnimalQuizFragment.resetAnimalQuiz();
            }
            else if( key.equals(animalsTpye)) {

                Set<String> animalTypes = sharedPreferences.getStringSet(animalsTpye,null);

                if(animalTypes!=null && animalTypes.size()>0) {

                    myAnimalQuizFragment.modifyTypeOfAnimlaInQuiz(sharedPreferences);
                    myAnimalQuizFragment.resetAnimalQuiz();
                } else {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    animalTypes.add(getString(R.string.default_animal_type));
                    editor.putStringSet(animalsTpye,animalTypes);
                    editor.apply();

                    Toast.makeText(MainActivity.this, R.string.default_animalsType_message, Toast.LENGTH_LONG).show();
                }
            } else if(key.equals(quizFont)) {

                myAnimalQuizFragment.modifyQuizFont(sharedPreferences);
                myAnimalQuizFragment.resetAnimalQuiz();
            }else if( key.equals(quizBackgroundColor)) {

                myAnimalQuizFragment.modifyBackgroundColor(sharedPreferences);
                myAnimalQuizFragment.resetAnimalQuiz();
            }

            Toast.makeText(MainActivity.this, R.string.change_message, Toast.LENGTH_LONG).show();
        }
    };
}
