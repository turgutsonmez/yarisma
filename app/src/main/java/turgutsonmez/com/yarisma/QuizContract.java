package turgutsonmez.com.yarisma;

import android.provider.BaseColumns;

public final class QuizContract {

  private QuizContract(){ }

  public static class QuestionsTable implements BaseColumns {
    public static final String TABLE_NAME = "yarisma";
    public static final String COLUMN_QUESTION = "soru";
    public static final String COLUMN_OPTION1 = "dogrucevap";
    public static final String COLUMN_OPTION2 = "yanlis1";
    public static final String COLUMN_OPTION3 = "yanlis2";
    public static final String COLUMN_OPTION4 = "yanlis3";
    public static final String COLUMN_ANSWER_NR = "answer_nr";


  }
}
