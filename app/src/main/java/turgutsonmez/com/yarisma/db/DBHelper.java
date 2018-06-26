package turgutsonmez.com.yarisma.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import turgutsonmez.com.yarisma.Model.Question;
import turgutsonmez.com.yarisma.QuizContract;

public class DBHelper extends SQLiteOpenHelper {

  private SQLiteDatabase sqLiteDatabase;
  private final Context context;

  private static final int DATABASE_VERSION = 2;
  private static final String DB_PATH = "/data/data/turgutsonmez.com.yarisma/databases/";
  private static final String DB_NAME = "bilgiyarismasiDb";

  public DBHelper(Context mContext) {
    super(mContext, DB_NAME, null, DATABASE_VERSION);
    this.context = mContext;
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {

  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

  }


  //Eğer veritabanı yoksa kopyalayıp oluşturucak, varsa hiçbir şey yapmayacak
  public void createDatabase() throws IOException {
    boolean dbExist = checkDatabase();
    if (dbExist) {
      //database varsa hiçbir şey yapma
    } else {
      getReadableDatabase();
      //veritabanı daha önce oluşturulmamış burada veritabanını kopyalıyoruz.
      copyDatabase();
    }
  }

  //Veritabanı daha önce oluşturulmuşmu oluşturulmamamış mı bunu öğrenmek için
  //Oluşturulmuşsa true oluşturulmamışsa false değeri döndürür.

  public boolean checkDatabase() {
    SQLiteDatabase checkDb = null;
    try {
      String myPath = DB_PATH + DB_NAME;
      checkDb = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    } catch (SQLiteException e) {
      //e.printStackTrace();
    }
    if (checkDb != null) {
      checkDb.close();
    }
    return checkDb != null ? true : false;
  }

  public void copyDatabase() throws IOException {
    //assets --> DB_PATH + DB_NAME;
    try {
      final InputStream myInput = context.getAssets().open(DB_NAME);
      String outFileName = DB_PATH + DB_NAME;
      OutputStream myOutput = new FileOutputStream(outFileName);
      byte[] buffer = new byte[1024];
      int length;
      while ((length = myInput.read(buffer)) > 0) {
        myOutput.write(buffer, 0, length);
      }
      myOutput.flush();
      myOutput.close();
      myInput.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void openDatabase() throws IOException {
    String myPath = DB_PATH + DB_NAME;
    sqLiteDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
  }

  @Override
  public synchronized void close() {
    if (sqLiteDatabase != null) {
      sqLiteDatabase.close();
    }
    super.close();
  }

//  public SQLiteDatabase getDatabase() {
//    return sqLiteDatabase;
//  }

  public List<Question> getQuestionSet() {
    List<Question> questionSet = new ArrayList<Question>();
    sqLiteDatabase = getReadableDatabase();
    Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM yarisma", null);
    if (c.moveToFirst()) {
      do {
        Question question = new Question();
        question.setQuestion(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_QUESTION)));
        question.setOption1(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION1)));
        question.setOption2(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION2)));
        question.setOption3(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION3)));
        question.setOption4(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION4)));
        question.setAnswerNr(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_ANSWER_NR)));
        questionSet.add(question);
      } while (c.moveToNext());
    }
    c.close();
    return questionSet;
  }
}
