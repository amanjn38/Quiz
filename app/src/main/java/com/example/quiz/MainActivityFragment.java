package com.example.quiz;


import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainActivityFragment extends Fragment {

    private static final int numberOfAnimalsInQuiz = 10;
    private List<String> allAnimalsNamesList;
    private List<String> animalNamesQuizList;
    private Set<String> animalTypesInQuiz;
    private String correctAnimaslAnswer;
    private int numberOfAllGuesses;
    private int numberOfRightAnswers;
    private int numberOfAnimalsGuessRows;
    private SecureRandom secureRandomNumber;
    private Handler handler;
    private Animation wrongAnswerAnimation;

    private LinearLayout animalQuizLinearLayout;
    private TextView txtQuestionNumber;
    private ImageView imgAnimal;
    private LinearLayout[] rowsOfGuessButtonsInAnimalQuiz;
    private TextView txtAnswer;


    public MainActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_activity, container, false);
        allAnimalsNamesList = new ArrayList<>();
        animalNamesQuizList = new ArrayList<>();
        secureRandomNumber = new SecureRandom();
        handler = new Handler();

        wrongAnswerAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.wrong_answer_animation);
        wrongAnswerAnimation.setRepeatCount(1);

        animalQuizLinearLayout = (LinearLayout) view.findViewById(R.id.animalQuizlinearlayout);
        txtQuestionNumber = (TextView) view.findViewById(R.id.tvQuestionNumber);
        imgAnimal = (ImageView) view.findViewById(R.id.ivAnimal);
        rowsOfGuessButtonsInAnimalQuiz = new LinearLayout[3];
        rowsOfGuessButtonsInAnimalQuiz[0] = (LinearLayout) view.findViewById(R.id.firstRowLinearLayout);
        rowsOfGuessButtonsInAnimalQuiz[1] = (LinearLayout) view.findViewById(R.id.secondRowLinearLayout);
        rowsOfGuessButtonsInAnimalQuiz[2] = (LinearLayout) view.findViewById(R.id.thirdRowLinearLayout);
        txtAnswer = (TextView) view.findViewById(R.id.tvAnswer);

        for (LinearLayout row : rowsOfGuessButtonsInAnimalQuiz) {
            int i = row.getChildCount();
            for (int column = 0;column<i;column++)
            {
                Button btnGuess = (Button) row.getChildAt(column);
                btnGuess.setOnClickListener(btnGuessListener);
                btnGuess.setTextSize(24);
            }
        }

        txtQuestionNumber.setText("This is Animal {1} of {10}");

        return view;
    }

    private View.OnClickListener btnGuessListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Button btnGuess = ((Button) view);
            String guessValue = btnGuess.getText().toString();
            String answerValue = getTheExactAnimalName(correctAnimaslAnswer);
            ++numberOfAllGuesses;

            if (guessValue.equals(answerValue)) {
                ++numberOfRightAnswers;

                txtAnswer.setText(answerValue + "!" + "RIGHT");

                disableQuizGuessButton();

                if (numberOfRightAnswers == numberOfAnimalsInQuiz) {
                    DialogFragment animalQuizResults = new DialogFragment() {
                        @NonNull
                        @Override
                        public Dialog onCreateDialog(Bundle savedInstanceState) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(getString(R.string.results_string_value, numberOfAllGuesses, (1000 / (double) numberOfAllGuesses)));

                            builder.setPositiveButton(R.string.reset_animal_quiz, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    resetAnimalQuiz();
                                }
                            });

                            return builder.create();
                        }
                    };

                animalQuizResults.setCancelable(false);
                animalQuizResults.show(getFragmentManager(), "QuizResults");
            } else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animateAnimateQuiz(true);
                    }
                }, 1000); //2000 milliseconds for 2 second delay
            }
        }
        else

        {
            imgAnimal.startAnimation(wrongAnswerAnimation);
            txtAnswer.setText(R.string.wrong_answer_message);
            btnGuess.setEnabled(false);
        }
    }

    };

    private String getTheExactAnimalName(String animalName)
    {
        return animalName.substring(animalName.indexOf('-')+1).replace('-',' ');
    }

    private void disableQuizGuessButton()
    {
        for(int row = 0;row < numberOfAnimalsGuessRows; row++)
        {
            LinearLayout guessRowLinearLayout = rowsOfGuessButtonsInAnimalQuiz[row];

            for( int buttonIndex = 0; buttonIndex <guessRowLinearLayout.getChildCount(); buttonIndex++) {


                guessRowLinearLayout.getChildAt(buttonIndex).setEnabled(false);
            }
        }
    }

    public void resetAnimalQuiz()
    {

            AssetManager assets = getActivity().getAssets();
            allAnimalsNamesList.clear();

            try
            {
                for(String animalType : animalTypesInQuiz)
                {
                    String[] animalImagePathsInQuiz = assets.list(animalType);
                    for(String animalImagePathInQuiz : animalImagePathsInQuiz)
                    {
                        allAnimalsNamesList.add(animalImagePathInQuiz.replace(".png"," "));

                    }
                }
            }catch (IOException e){
                Log.e("AnimalQuiz","Error",e);
            }
            numberOfRightAnswers = 0;
            numberOfAllGuesses = 0;
            animalNamesQuizList.clear();

            int counter = 1;
            int numberOfAvailableAnimals = allAnimalsNamesList.size();

            while(counter <= numberOfAnimalsInQuiz){
                int randomIndex = secureRandomNumber.nextInt(numberOfAvailableAnimals);
                String animalImageName = allAnimalsNamesList.get(randomIndex);

                if(!animalNamesQuizList.contains(animalImageName)){

                    animalNamesQuizList.add(animalImageName);
                    ++counter;
                }
            }
            showNextAnimal();
    }

    private void animateAnimateQuiz( boolean animateOutAnimalImage){
        if (numberOfRightAnswers == 0){
            return;
        }

        int xTopLeft = 0;
        int yTopLeft = 0;

        int xBottomRight = animalQuizLinearLayout.getLeft() + animalQuizLinearLayout.getRight();
        int yBottomRight = animalQuizLinearLayout.getTop() + animalQuizLinearLayout.getBottom();

    //Here is the max value of the radius
        int radius = Math.max(animalQuizLinearLayout.getWidth(),animalQuizLinearLayout.getHeight());

        Animator animator;

        if(animateOutAnimalImage){
            animator = ViewAnimationUtils.createCircularReveal(animalQuizLinearLayout,xBottomRight,yBottomRight,radius,0);

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                    showNextAnimal();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
        else
        {
            animator = ViewAnimationUtils.createCircularReveal(animalQuizLinearLayout,xTopLeft,yTopLeft,0,radius);
        }

        animator.setDuration(700);
        animator.start();
    }

    private void showNextAnimal() {

        String nextAnimalImageName = animalNamesQuizList.remove(0);
        correctAnimaslAnswer = nextAnimalImageName;
        txtAnswer.setText("");

        txtQuestionNumber.setText(getString(R.string.question_text,(numberOfRightAnswers+1),numberOfAnimalsInQuiz));

        String animalType = nextAnimalImageName.substring(0,nextAnimalImageName.indexOf("-"));

        AssetManager assets = getActivity().getAssets();

        try (InputStream stream = assets.open(animalType + "/" + nextAnimalImageName + ".png")) {

            Drawable animalImage = Drawable.createFromStream(stream,nextAnimalImageName);

            imgAnimal.setImageDrawable(animalImage);

            animateAnimateQuiz(false);
        } catch (IOException e) {
            Log.e("AnimalQuiz","There is an Error Getting" + nextAnimalImageName,e);

        }
        Collections.shuffle(allAnimalsNamesList);

        int correctAnimalNameIndex = allAnimalsNamesList.indexOf(correctAnimaslAnswer);
        String correctAnimalName = allAnimalsNamesList.remove(correctAnimalNameIndex);
        allAnimalsNamesList.add(correctAnimalName);

        for ( int row=0;row < numberOfAnimalsGuessRows;row ++){
            for(int column = 0; column <rowsOfGuessButtonsInAnimalQuiz[row].getChildCount(); column++)
            {
                Button btnGuess = (Button) rowsOfGuessButtonsInAnimalQuiz[row].getChildAt(column);
                btnGuess.setEnabled(true);

                String animalImageName = allAnimalsNamesList.get((row*2) + column);
                btnGuess.setText(getTheExactAnimalName(animalImageName));
            }
        }

        int row = secureRandomNumber.nextInt(numberOfAnimalsGuessRows);
        int column = secureRandomNumber.nextInt(2);
        LinearLayout randomRow = rowsOfGuessButtonsInAnimalQuiz[row];
        String animalImageName = getTheExactAnimalName(correctAnimalName);
        ((Button)randomRow.getChildAt(column)).setText(animalImageName);
    }

    public void modifyAnimalGuessRows(SharedPreferences sharedPreferences){

        final String NUMBER_OF_GUESS_OPTIONS = sharedPreferences.getString(MainActivity.guesses,null);

        numberOfAnimalsGuessRows = Integer.parseInt(NUMBER_OF_GUESS_OPTIONS)/2;

        for( LinearLayout horizontalLinearLayout : rowsOfGuessButtonsInAnimalQuiz) {

            horizontalLinearLayout.setVisibility(View.GONE);
        }

        for(int row= 0;row < numberOfAnimalsGuessRows ; row++) {

            rowsOfGuessButtonsInAnimalQuiz[row].setVisibility(View.VISIBLE);
        }
    }

    public void modifyTypeOfAnimlaInQuiz(SharedPreferences sharedPreferences){
        animalTypesInQuiz = sharedPreferences.getStringSet(MainActivity.animalsTpye,null);
    }

    public void modifyQuizFont( SharedPreferences sharedPreferences){

        String fontStringValue = sharedPreferences.getString(MainActivity.quizFont,null);

        switch (fontStringValue){

            case "Chunkfive.otf":
                for (LinearLayout row: rowsOfGuessButtonsInAnimalQuiz){
                    for (int column =0; column<row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.chunkfive);
                    }
                }

                break;
            case "FontleroyBrown.ttf":
                for (LinearLayout row: rowsOfGuessButtonsInAnimalQuiz){
                    for (int column =0; column<row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.fontleroybrown);
                    }
                }
                break;
            case "Wonderbar Demo.otf":
                for (LinearLayout row: rowsOfGuessButtonsInAnimalQuiz){
                    for (int column =0; column<row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.wonderbarDemo);
                    }
                }
                break;
        }
    }

    public void modifyBackgroundColor ( SharedPreferences sharedPreferences){

        String backgroundColor = sharedPreferences.getString(MainActivity.quizBackgroundColor,null);


        switch (backgroundColor) {

            case "White":

                animalQuizLinearLayout.setBackgroundColor(Color.WHITE);

                for (LinearLayout row: rowsOfGuessButtonsInAnimalQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);
                    }
                }

                txtAnswer.setTextColor(Color.BLUE);
                txtQuestionNumber.setTextColor(Color.BLACK);

                break;

            case "Black":

                animalQuizLinearLayout.setBackgroundColor(Color.BLACK);

                for (LinearLayout row: rowsOfGuessButtonsInAnimalQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.YELLOW);
                        button.setTextColor(Color.BLACK);
                    }
                }

                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.WHITE);

                break;

            case "Green":

                    animalQuizLinearLayout.setBackgroundColor(Color.GREEN);

                for (LinearLayout row: rowsOfGuessButtonsInAnimalQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);
                    }
                }

                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.YELLOW);

                break;

            case "Blue":

                animalQuizLinearLayout.setBackgroundColor(Color.BLUE);

                for (LinearLayout row: rowsOfGuessButtonsInAnimalQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.RED);
                        button.setTextColor(Color.WHITE);
                    }
                }

                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.WHITE);

                break;

            case "Red":

                animalQuizLinearLayout.setBackgroundColor(Color.RED);

                for (LinearLayout row: rowsOfGuessButtonsInAnimalQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);
                    }
                }

                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.WHITE);

                break;

            case "Yellow":

                animalQuizLinearLayout.setBackgroundColor(Color.YELLOW);

                for (LinearLayout row: rowsOfGuessButtonsInAnimalQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLACK);
                        button.setTextColor(Color.WHITE);
                    }
                }

                txtAnswer.setTextColor(Color.BLACK);
                txtQuestionNumber.setTextColor(Color.BLACK);

                break;
        }
    }
}