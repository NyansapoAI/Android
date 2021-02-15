package com.example.edward.nyansapo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

public class Assessment implements Parcelable {

   // "Edward Idun Amoah"

    public String STUDENT_ID = "";
    public String ASSESSMENT_KEY = "";
    public String LETTERS_CORRECT = "";
    public String LETTERS_WRONG = "";
    public String WORDS_CORRECT = "";
    public String WORDS_WRONG = "";
    public String PARAGRAPH_WORDS_WRONG = "";
    //public String STORY_WORDS_WRONG = "";
    public String STORY_ANS_Q1 = "";
    public String STORY_ANS_Q2 = "";
    public String LEARNING_LEVEL = "";
    public String TIMESTAMP ;
    public String LOCAL_ID = "";
    public String CLOUD_ID = "";


    public Assessment(){
        TIMESTAMP = new Date(System.currentTimeMillis()).toString();
        LOCAL_ID = UUID.randomUUID().toString();
    }
    public Assessment(String STUDENT_ID, String ASSESSMENT_KEY, String LETTERS_CORRECT, String LETTERS_WRONG, String WORDS_CORRECT, String WORDS_WRONG, String PARAGRAPH_WORDS_WRONG, String STORY_WORDS_WRONG ,String STORY_ANS_Q1, String STORY_ANS_Q2, String LEARNING_LEVEL, String TIMESTAMP, String LOCAL_ID, String CLOUD_ID) {
        this.STUDENT_ID = STUDENT_ID;
        this.ASSESSMENT_KEY = ASSESSMENT_KEY;
        this.LETTERS_CORRECT = LETTERS_CORRECT;
        this.LETTERS_WRONG = LETTERS_WRONG;
        this.WORDS_CORRECT = WORDS_CORRECT;
        this.WORDS_WRONG = WORDS_WRONG;
        this.PARAGRAPH_WORDS_WRONG = PARAGRAPH_WORDS_WRONG;
        //this.STORY_WORDS_WRONG = STORY_WORDS_WRONG;
        this.STORY_ANS_Q1 = STORY_ANS_Q1;
        this.STORY_ANS_Q2 = STORY_ANS_Q2;
        this.LEARNING_LEVEL = LEARNING_LEVEL;
        this.TIMESTAMP = TIMESTAMP;
        this.LOCAL_ID = LOCAL_ID;
        this.CLOUD_ID = CLOUD_ID;
    }

    protected Assessment(Parcel in) {
        STUDENT_ID = in.readString();
        ASSESSMENT_KEY = in.readString();
        LETTERS_CORRECT = in.readString();
        LETTERS_WRONG = in.readString();
        WORDS_CORRECT = in.readString();
        WORDS_WRONG = in.readString();
        PARAGRAPH_WORDS_WRONG = in.readString();
        //STORY_WORDS_WRONG = in.readString();
        STORY_ANS_Q1 = in.readString();
        STORY_ANS_Q2 = in.readString();
        LEARNING_LEVEL = in.readString();
        LOCAL_ID = in.readString();
        CLOUD_ID = in.readString();
    }

    public static final Creator<Assessment> CREATOR = new Creator<Assessment>() {
        @Override
        public Assessment createFromParcel(Parcel in) {
            return new Assessment(in);
        }

        @Override
        public Assessment[] newArray(int size) {
            return new Assessment[size];
        }
    };

    public String getSTUDENT_ID() {
        return STUDENT_ID;
    }

    public void setSTUDENT_ID(String STUDENT_ID) {
        this.STUDENT_ID = STUDENT_ID;
    }

    public String getASSESSMENT_KEY() {
        return ASSESSMENT_KEY;
    }

    public void setASSESSMENT_KEY(String ASSESSMENT_KEY) {
        this.ASSESSMENT_KEY = ASSESSMENT_KEY;
    }

    public String getLETTERS_CORRECT() {
        return LETTERS_CORRECT;
    }

    public void setLETTERS_CORRECT(String LETTERS_CORRECT) {
        this.LETTERS_CORRECT = LETTERS_CORRECT;
    }

    public String getLETTERS_WRONG() {
        return LETTERS_WRONG;
    }

    public void setLETTERS_WRONG(String LETTERS_WRONG) {
        this.LETTERS_WRONG = LETTERS_WRONG;
    }

    public String getWORDS_CORRECT() {
        return WORDS_CORRECT;
    }

    public void setWORDS_CORRECT(String WORDS_CORRECT) {
        this.WORDS_CORRECT = WORDS_CORRECT;
    }

    public String getWORDS_WRONG() {
        return WORDS_WRONG;
    }

    public void setWORDS_WRONG(String WORDS_WRONG) {
        this.WORDS_WRONG = WORDS_WRONG;
    }

    public String getPARAGRAPH_WORDS_WRONG() {
        return PARAGRAPH_WORDS_WRONG;
    }

    public void setPARAGRAPH_WORDS_WRONG(String PARAGRAPH_WORDS_WRONG) {
        this.PARAGRAPH_WORDS_WRONG = PARAGRAPH_WORDS_WRONG;
    }

    /*public String getSTORY_WORDS_WRONG() {
        return STORY_WORDS_WRONG;
    }

    public void setSTORY_WORDS_WRONG(String STORY_WORDS_WRONG) {
        this.STORY_WORDS_WRONG = STORY_WORDS_WRONG;
    }
     */

    public String getSTORY_ANS_Q1() {
        return STORY_ANS_Q1;
    }

    public void setSTORY_ANS_Q1(String STORY_ANS_Q1) {
        this.STORY_ANS_Q1 = STORY_ANS_Q1;
    }

    public String getSTORY_ANS_Q2() {
        return STORY_ANS_Q2;
    }

    public void setSTORY_ANS_Q2(String STORY_ANS_Q2) {
        this.STORY_ANS_Q2 = STORY_ANS_Q2;
    }

    public String getLEARNING_LEVEL() {
        return LEARNING_LEVEL;
    }

    public void setLEARNING_LEVEL(String LEARNING_LEVEL) {
        this.LEARNING_LEVEL = LEARNING_LEVEL;
    }

    public String getTIMESTAMP() {
        return TIMESTAMP;
    }

    public void setTIMESTAMP(String TIMESTAMP) {
        this.TIMESTAMP = TIMESTAMP;
    }

    public String getLOCAL_ID() {
        return LOCAL_ID;
    }

    public void setLOCAL_ID(String LOCAL_ID) {
        this.LOCAL_ID = LOCAL_ID;
    }

    public String getCLOUD_ID() {
        return CLOUD_ID;
    }

    public void setCLOUD_ID(String CLOUD_ID) {
        this.CLOUD_ID = CLOUD_ID;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(STUDENT_ID);
        dest.writeString(ASSESSMENT_KEY);
        dest.writeString(LETTERS_CORRECT);
        dest.writeString(LETTERS_WRONG);
        dest.writeString(WORDS_CORRECT);
        dest.writeString(WORDS_WRONG);
        dest.writeString(PARAGRAPH_WORDS_WRONG);
        dest.writeString(STORY_ANS_Q1);
        dest.writeString(STORY_ANS_Q2);
        //dest.writeString(STORY_WORDS_WRONG);
        dest.writeString(LEARNING_LEVEL);
        dest.writeString(LOCAL_ID);
        dest.writeString(CLOUD_ID);
    }
}
