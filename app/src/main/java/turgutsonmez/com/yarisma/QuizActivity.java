package turgutsonmez.com.yarisma;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.SQLException;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import turgutsonmez.com.yarisma.Model.Question;
import turgutsonmez.com.yarisma.db.DBHelper;

public class QuizActivity extends AppCompatActivity {
  public static final String EXTRA_SCORE = "extraScore";
  private static final long COUNTDOWN_IN_MILLIS = 30000;

  DBHelper dbHelper;
  List<Question> questionList;

  private TextView textQuestion;
  private TextView textPuan;
  private TextView textSoruSayisi;
  private TextView textZaman;
  private RadioGroup radioGroup;
  private RadioButton rb1;
  private RadioButton rb2;
  private RadioButton rb3;
  private RadioButton rb4;
  private Button btnCevapla;

  private ColorStateList textColorDefaultRb;
  private ColorStateList textColorDefaultCd;

  private CountDownTimer countDownTimer;
  private long timeLeftInMillis;

  private Question currentQuestions;
  private int questionCounter;
  private int questionCountTotal;

  private int score;
  private boolean answered;
  private long backPressedTime;


  Context context = this;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_quiz);

    textQuestion = findViewById(R.id.question);
    textPuan = findViewById(R.id.textPuan);
    textSoruSayisi = findViewById(R.id.textSoruSayisi);
    textZaman = findViewById(R.id.textZaman);
    radioGroup = findViewById(R.id.radio_group);
    rb1 = findViewById(R.id.btnAnswer1);
    rb2 = findViewById(R.id.btnAnswer2);
    rb3 = findViewById(R.id.btnAnswer3);
    rb4 = findViewById(R.id.btnAnswer4);
    btnCevapla = findViewById(R.id.btnCevapla);

    textColorDefaultRb = rb1.getTextColors();
    textColorDefaultCd = textZaman.getTextColors();

    DBHelper dbHelper = new DBHelper(this);
    try {
      dbHelper.createDatabase();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      dbHelper.openDatabase();
    } catch (IOException e) {
      e.printStackTrace();
    }
    questionList = dbHelper.getQuestionSet();

    questionCountTotal = questionList.size();
    Collections.shuffle(questionList);


    //questionList = getQuestionSetFromDb();

    showNextQuestion();

    btnCevapla.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!answered) {
          if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
            checkAnswer();
          } else {
            Toast.makeText(QuizActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
          }
        } else {
          showNextQuestion();
        }
      }
    });

  }

  private void showNextQuestion() {
    rb1.setTextColor(textColorDefaultRb);
    rb2.setTextColor(textColorDefaultRb);
    rb3.setTextColor(textColorDefaultRb);
    rb4.setTextColor(textColorDefaultRb);
    radioGroup.clearCheck();

    if (questionCounter < questionCountTotal) {
      currentQuestions = questionList.get(questionCounter);

      textQuestion.setText(currentQuestions.getQuestion());
      rb1.setText(currentQuestions.getOption1());
      rb2.setText(currentQuestions.getOption2());
      rb3.setText(currentQuestions.getOption3());
      rb4.setText(currentQuestions.getOption4());

      questionCounter++;
      textSoruSayisi.setText("Question: " + questionCounter + "/" + questionCountTotal);
      answered = false;
      btnCevapla.setText("Cevapla");

      timeLeftInMillis = COUNTDOWN_IN_MILLIS;
      startCountDown();
    } else {
      finishQuiz();
    }
  }

  private void startCountDown() {
    countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
      @Override
      public void onTick(long millisUntilFinished) {
        timeLeftInMillis = millisUntilFinished;
        updateCountDownText();
      }

      @Override
      public void onFinish() {
        timeLeftInMillis = 0;
        updateCountDownText();
        checkAnswer();
      }
    }.start();
  }

  private void updateCountDownText() {
    int minutes = (int) (timeLeftInMillis / 1000) / 60;
    int seconds = (int) (timeLeftInMillis / 1000) % 60;

    String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

    textZaman.setText(timeFormatted);

    if (timeLeftInMillis < 10000) {
      textZaman.setTextColor(Color.RED);
    } else {
      textZaman.setTextColor(textColorDefaultCd);
    }
  }

  private void checkAnswer() {
    answered = true;

    countDownTimer.cancel();

    RadioButton rbSelected = findViewById(radioGroup.getCheckedRadioButtonId());
    int answerNr = radioGroup.indexOfChild(rbSelected) + 1;

    if (answerNr == currentQuestions.getAnswerNr()) {
      score++;
      textPuan.setText("Puan: " + score);
    }

    showSolution();
  }

  private void showSolution() {
    rb1.setTextColor(Color.RED);
    rb2.setTextColor(Color.RED);
    rb3.setTextColor(Color.RED);
    rb4.setTextColor(Color.RED);

    switch (currentQuestions.getAnswerNr()) {
      case 1:
        rb1.setTextColor(Color.GREEN);
        textPuan.setText("Cevap 1 doğru");
        break;
      case 2:
        rb2.setTextColor(Color.GREEN);
        textPuan.setText("Cevap 2 doğru");
        break;
      case 3:
        rb3.setTextColor(Color.GREEN);
        textPuan.setText("Cevap 3 doğru");
        break;
      case 4:
        rb4.setTextColor(Color.GREEN);
        textPuan.setText("Cevap 4 doğru");
        break;
    }

    if (questionCounter < questionCountTotal) {
      btnCevapla.setText("Sonraki");
    } else {
      btnCevapla.setText("Bitir");
    }
  }

  private void finishQuiz() {
    Intent resultIntent = new Intent();
    resultIntent.putExtra(EXTRA_SCORE, score);
    setResult(RESULT_OK, resultIntent);
    finish();
  }

  @Override
  public void onBackPressed() {
    if (backPressedTime + 2000 > System.currentTimeMillis()) {
      finishQuiz();
    } else {
      Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show();
    }

    backPressedTime = System.currentTimeMillis();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (countDownTimer != null) {
      countDownTimer.cancel();
    }
  }
}
