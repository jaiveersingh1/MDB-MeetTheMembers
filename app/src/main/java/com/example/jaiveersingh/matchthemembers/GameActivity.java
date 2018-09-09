package com.example.jaiveersingh.matchthemembers;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

public class GameActivity extends AppCompatActivity {

    GameButton btn1;
    GameButton btn2;
    GameButton btn3;
    GameButton btn4;

    ImageView profileImg;

    TextView scoreText;
    int currScore = 0;
    TextView streakText;
    int currStreak = 0;

    TextView timerText;

    Button endBtn;

    ArrayList<GameButton> buttonArray = new ArrayList<>();

    Random random;

    ArrayList<String> memberNames = new ArrayList<String>(asList("Aayush Tyagi", "Abhinav Koppu", "Aditya Yadav", "Ajay Merchia", "Alice Zhao", "Amy Shen", "Anand Chandra", "Andres Medrano", "Angela Dong", "Anika Bagga", "Anmol Parande", "Austin Davis", "Ayush Kumar", "Brandon David", "Candice Ye", "Carol Wang", "Cody Hsieh", "Daniel Andrews", "Daniel Jing", "Eric Kong", "Ethan Wong", "Fang Shuo", "Izzie Lau", "Jaiveer Singh", "Japjot Singh", "Jeffrey Zhang", "Joey Hejna", "Julie Deng", "Justin Kim", "Kaden Dippe", "Kanyes Thaker", "Kayli Jiang", "Kiana Go", "Leon Kwak", "Levi Walsh", "Louie Mcconnell", "Max Miranda", "Michelle Mao", "Mohit Katyal", "Mudabbir Khan", "Natasha Wong", "Nikhar Arora", "Noah Pepper", "Radhika Dhomse", "Sai Yandapalli", "Saman Virai", "Sarah Tang", "Sharie Wang", "Shiv Kushwah", "Shomil Jain", "Shreya Reddy", "Shubha Jagannatha", "Shubham Gupta", "Srujay Korlakunta", "Stephen Jayakar", "Suyash Gupta", "Tiger Chen", "Vaibhav Gattani", "Victor Sun", "Vidya Ravikumar", "Vineeth Yeevani", "Wilbur Shi", "William Lu", "Will Oakley", "Xin Yi Chen", "Young Lin"));
    ArrayList<Integer> allIndices = new ArrayList<>();
    ArrayList<Integer> chosenFourIndices = new ArrayList<>(4);
    int correctIndex;

    CountDownTimer timer;

    ColorStateList defaultColor;

    final int REQUEST_CODE = 127;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        profileImg = findViewById(R.id.profileImg);

        scoreText = findViewById(R.id.scoreText);
        scoreText.setText(String.format(Locale.US, "Score: %d", currScore));

        streakText = findViewById(R.id.streakText);
        streakText.setText(String.format(Locale.US, "Streak: %d", currStreak));

        timerText = findViewById(R.id.timerText);

        endBtn = findViewById(R.id.endBtn);

        btn1 = new GameButton(R.id.btn1, 0);
        btn2 = new GameButton(R.id.btn2, 1);
        btn3 = new GameButton(R.id.btn3, 2);
        btn4 = new GameButton(R.id.btn4, 3);

        buttonArray.add(btn1);
        buttonArray.add(btn2);
        buttonArray.add(btn3);
        buttonArray.add(btn4);

        random = new Random();

        defaultColor = streakText.getTextColors();

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.NAME, memberNames.get(chosenFourIndices.get(correctIndex)));
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                AlertDialog alertDialog = new AlertDialog.Builder(GameActivity.this).create();
                alertDialog.setTitle("Exit Game");
                alertDialog.setMessage("Are you sure you want to exit the game?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                nextQuestion();
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Exit Game",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                alertDialog.show();
            }
        });

        timer = new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerText.setText(String.format(Locale.US,"%d", millisUntilFinished / 1000 + 1));
            }

            public void onFinish() {
                updateStreak(0);
                nextQuestion();
            }
        };

        for(int i = 0; i < memberNames.size(); i++)
        {
            allIndices.add(i);
        }
        for(int i = 0; i < 4; i++)
        {
            chosenFourIndices.add(0);
        }
        nextQuestion();
    }

    private void nextQuestion()
    {
        Collections.shuffle(allIndices);
        for(int i = 0; i < 4; i++)
        {
            chosenFourIndices.set(i, allIndices.get(i));
        }
        correctIndex = random.nextInt(4);
        for(GameButton btn : buttonArray)
        {
            btn.update();
        }
        String fileName = memberNames.get(chosenFourIndices.get(correctIndex)).toLowerCase().replaceAll("\\s","");

        profileImg.setImageBitmap(
                decodeSampledBitmapFromResource(
                        getResources(),
                        getResources().getIdentifier(fileName, "drawable", getPackageName()),
                        500,
                        500));
        timer.start();

    }

    private void submit(int n)
    {
        timer.cancel();
        if(n == correctIndex)
        {
            currScore++;
            scoreText.setText(String.format(Locale.US, "Score: %d", currScore));
            updateStreak(1);
        }
        else {
            Toast.makeText(this, "Wrong answer!", Toast.LENGTH_SHORT).show();
            updateStreak(0);
        }
        nextQuestion();
    }

    private class GameButton  {
        Button btn;
        int number;

        private GameButton(int id, int n)
        {
            btn = findViewById(id);
            this.number = n;

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submit(number);
                }
            });
        }

        private void update()
        {
            btn.setText(memberNames.get(chosenFourIndices.get(number)));
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private void updateStreak(int input)
    {
        if(input == 0)
        {
            currStreak = 0;
        }
        else
        {
            currStreak++;
            if(currStreak == 5)
            {
                Toast.makeText(this, "On fire!", Toast.LENGTH_SHORT).show();
            }
        }
        if(currStreak >=5)
        {
            streakText.setTextColor(Color.RED);
        }
        else
        {
            streakText.setTextColor(defaultColor);
        }
        streakText.setText(String.format(Locale.US, "Streak: %d", currStreak));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_CODE:
                //you just got back from activity C - deal with resultCode
                nextQuestion();
                break;
        }
    }
}
