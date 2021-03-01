package com.example.edward.nyansapo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class dataBaseHandler extends SQLiteOpenHelper {

    // Initialize Database Name and Table Names
    private static final String DATABASE_NAME = "nyansapo.sqlite";
    private static final String TABLE_NAME = "table_name";

    // used in multiple tables
    public static final String LOCAL_ID = "local_id";
    public static final String CLOUD_ID = "cloud_id";
    public static final String TIMESTAMP = "timestamp";

    // Instructor Table
    public static final String INSTRUCTOR_TABLE = "instructor";
    public static final String FIRSTNAME = "firstname"; // also for student_activity table
    public static final String LASTNAME = "lastname"; // also for student_activity table
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    // Student Table
    public static final String STUDENT_TABLE = "student";
    public static final String INSTRUCTOR_ID = "instructor_id"; // also for group table
    public static final String AGE = "age";
    public static final String GENDER = "gender";
    public static final String NOTES = "notes";
    public static final String LEARNING_LEVEL ="learning_level";
    public static final String STD_CLASS = "std_class";

    // Assessment Table
    public static final String ASSESSMENT_TABLE = "assessment";
    public static final String STUDENT_ID = "student_id";
    public static final String ASSESSMENT_KEY = "assessment_key";
    public static final String LETTERS_CORRECT = "letters_correct";
    public static final String LETTERS_WRONG = "letters_wrong";
    public static final String WORDS_CORRECT = "words_correct";
    public static final String WORDS_WRONG = "words_wrong";
    public static final String PARAGRAPH_WORDS_WRONG = "paragraph_words_wrong";
    //public static final String STORY_WORDS_WRONG = "story_words_wrong";
    public static final String STORY_ANS_Q1 = "story_ans_q1";
    public static final String STORY_ANS_Q2 = "story_ans_q2";


    // Attendance
    public static final String ATTENDANCE_TABLE = "attendance";
    public static final String PRESENT = "present";

    // Group Table
    public static final String GROUP_TABLE = "student_group";
    public static final String NAME = "name";
    public static final String STUDENTS_ID = "students_id";

    // cache user name and password
    public static final String USER_TABLE = "user_table";
    public static final String USEREMAIL = "useremail";
    public static final String USER_CLOUD_ID = "user_cloud_id";
    public static final String USERTOKEN = "usertoken";
    public static final String USERACTIVE = "useractive";

    public dataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //create Tables
        String createTable = "create table "+ TABLE_NAME +
                "(Id INTEGER PRIMARY KEY, txt TEXT)";

        //Create Instructor schema
        String instructorTable = "create table "+ INSTRUCTOR_TABLE + " ("+
                LOCAL_ID + " TEXT,"+
                CLOUD_ID + " TEXT," +
                FIRSTNAME + " TEXT," +
                LASTNAME + " TEXT," +
                EMAIL + " TEXT," +
                PASSWORD + " TEXT,"+
                TIMESTAMP + " TEXT)";

        //Create Student schema
        String studentTable = "create table " + STUDENT_TABLE + " (" +
                LOCAL_ID + " TEXT,"+
                CLOUD_ID + " TEXT," +
                TIMESTAMP + " TEXT," +
                FIRSTNAME + " TEXT," +
                LASTNAME + " TEXT," +
                AGE + " TEXT," +
                GENDER + " TEXT," +
                NOTES + " TEXT," +
                STD_CLASS + " TEXT," +
                INSTRUCTOR_ID + " TEXT," +
                LEARNING_LEVEL + " TEXT)";

        //Create Assessment schema
        String assessmentTable = "create table "+ ASSESSMENT_TABLE +" (" +
                LOCAL_ID + " TEXT,"+
                CLOUD_ID + " TEXT," +
                TIMESTAMP + " TEXT," +
                STUDENT_ID + " TEXT,"+
                ASSESSMENT_KEY + " TEXT," +
                LETTERS_CORRECT + " TEXT," +
                LETTERS_WRONG + " TEXT," +
                WORDS_CORRECT + " TEXT,"+
                WORDS_WRONG + " TEXT,"+
                PARAGRAPH_WORDS_WRONG + " TEXT,"+
                //STORY_WORDS_WRONG + " TEXT,"+
                STORY_ANS_Q1+ " TEXT,"+
                STORY_ANS_Q2+ " TEXT,"+
                LEARNING_LEVEL + " TEXT)";

        // Create Attendance schema
        String attendanceTable = "create table "+ ATTENDANCE_TABLE + " (" +
                LOCAL_ID + " INTEGER PRIMARY KEY,"+
                CLOUD_ID + " TEXT," +
                TIMESTAMP + " TEXT," +
                STUDENT_ID + " TEXT,"+
                PRESENT+ " INTEGER)"; // 0 FALSE 1 TRUE

        // Create Group schema
        String groupTable = "create table "+ GROUP_TABLE + " (" +
                LOCAL_ID + "INTEGER PRIMARY KEY,"+
                CLOUD_ID + "TEXT," +
                TIMESTAMP + "TEXT," +
                INSTRUCTOR_ID + "TEXT,"+
                NAME + "TEXT,"+
                STUDENTS_ID + "TEXT)";


        // create user table
        String userTable = "create table " + USER_TABLE + " ("+
                USEREMAIL + " TEXT," +
                USER_CLOUD_ID + " TEXT," +
                USERTOKEN + " TEXT,"+
                USERACTIVE + " TEXT)";



         sqLiteDatabase.execSQL(createTable);
         sqLiteDatabase.execSQL(instructorTable);
         sqLiteDatabase.execSQL(studentTable);
         sqLiteDatabase.execSQL(assessmentTable);
         sqLiteDatabase.execSQL(attendanceTable);
         sqLiteDatabase.execSQL(groupTable);
         sqLiteDatabase.execSQL(userTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        if (i1 > i) {
            sqLiteDatabase.execSQL("ALTER TABLE foo ADD COLUMN new_column INTEGER DEFAULT 0");
        }

        // Drop older tables if exist
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ INSTRUCTOR_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ STUDENT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ ASSESSMENT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ ATTENDANCE_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ GROUP_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ USER_TABLE);
        onCreate(sqLiteDatabase);
    }

    public boolean addText(String text){
        //get WriteAble Database
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        // create contentValues
        ContentValues contentValues = new ContentValues();
        contentValues.put("txt",text);
        //Add Values into Database
        sqLiteDatabase.insert(TABLE_NAME, null,contentValues);
        return true;
    }

    public ArrayList getAllText(){
        // Get Readable Database
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<String>();
        //Create Cursor to select All values
        Cursor cursor = sqLiteDatabase.rawQuery("select * from "+ TABLE_NAME, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            arrayList.add(cursor.getString(cursor.getColumnIndex("txt")));
            cursor.moveToNext();
        }
        return arrayList;

    }

    public String addUser(String email, String user_cloud_id, String token, String active){
        //get WriteAble Database
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        // create contentValues
        ContentValues contentValues = new ContentValues();
        contentValues.put(USEREMAIL,email);
        contentValues.put(USERTOKEN,token);
        //contentValues.put(USER_CLOUD_ID,user_cloud_id);
        contentValues.put(USERACTIVE,active);

        //Add Values into Database
        sqLiteDatabase.insert(USER_TABLE, null,contentValues);

        try{
            long r = sqLiteDatabase.insert(USER_TABLE, null,contentValues);
            return Long.toString(r);
            //return true;
        }catch (Error error){
        }

        return null;
    }


    public boolean deleteUser(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String ass_id = "1";
        sqLiteDatabase.execSQL("DELETE FROM "+USER_TABLE +" WHERE "+ USERACTIVE + " = '"+ ass_id+ "'");
        return true;
    }

    public boolean updateUserToken( String new_token){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String active = "1";
        // create contentValues
        //ContentValues contentValues = new ContentValues();
        //contentValues.put(LEARNING_LEVEL, new_level);
        //sqLiteDatabase.update(STUDENT_TABLE, contentValues, STUDENT_ID + " ? ", new String[]{ std_id });
        sqLiteDatabase.execSQL("UPDATE "+USER_TABLE+" SET "+ USERTOKEN +" = '"+ new_token + "' WHERE "+ USERACTIVE + " = '"+ active+ "'");
        //db.execSQL("UPDATE DB_TABLE SET YOUR_COLUMN='newValue' WHERE id=6 ");
        return true;

    }

    public String getUserToken() {
        String toString = "1";

        // Get Readable Database
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        try  {
            /* retrieve the data */
            cursor = sqLiteDatabase.rawQuery("select * from "+ USER_TABLE +" where "+ USERACTIVE + "='" + toString +"'", null); // get instructor by email
        } catch (SQLException e) {
            /* handle the exception properly */
            Log.i("MyActivity",e.toString());
        }
        //cursor = sqLiteDatabase.rawQuery("select * from "+ INSTRUCTOR_TABLE +" where "+ EMAIL + "='" + toString +"'", null); // get instructor by email
        if(cursor == null  || cursor.getCount() == 0){
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(USERTOKEN));

    }

    public String getUserID() {
        String toString = "1";
        String email = "edward@kijenzi.com";

        // Get Readable Database
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        try  {
            /* retrieve the data */
            //cursor = sqLiteDatabase.rawQuery("select * from "+ USER_TABLE +" where "+ USEREMAIL + "='" + email +"'", null); // get instructor by email
            cursor =  sqLiteDatabase.rawQuery("select * from "+ USER_TABLE , null);
        } catch (SQLException e) {
            /* handle the exception properly */
            Log.i("MyActivity",e.toString());
        }
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(USER_CLOUD_ID));

    }


    public String addStudent(Student student){
        //get WriteAble Database
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        // create contentValues
        String uuid = UUID.randomUUID().toString();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOCAL_ID, uuid );
        contentValues.put(CLOUD_ID, student.getCloud_id());
        contentValues.put(FIRSTNAME,student.getFirstname());
        contentValues.put(LASTNAME,student.getLastname());
        contentValues.put(AGE,student.getAge());
        contentValues.put(LEARNING_LEVEL,student.getLearning_level());
        contentValues.put(TIMESTAMP, new Date(System.currentTimeMillis()).toString());
        contentValues.put(GENDER,student.getGender());
        contentValues.put(INSTRUCTOR_ID, student.getInstructor_id());
        contentValues.put(STD_CLASS, student.getStd_class());
        contentValues.put(NOTES, student.getNotes());
        //Add Values into Database

        try{
            long r = sqLiteDatabase.insert(STUDENT_TABLE, null,contentValues);
            return Long.toString(r);
        }catch (Error error){
        }
        return uuid;
    }

    public boolean deleteStudent(String std_id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM "+STUDENT_TABLE +" WHERE "+ CLOUD_ID + " = '"+ std_id+ "'");
        return true;
    }

    public boolean deleteAssessment(String ass_id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM "+ASSESSMENT_TABLE +" WHERE "+ LOCAL_ID + " = '"+ ass_id+ "'");
        return true;
    }

    public boolean updateStudentLevel(String std_id, String new_level){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        // create contentValues
        //ContentValues contentValues = new ContentValues();
        //contentValues.put(LEARNING_LEVEL, new_level);
        //sqLiteDatabase.update(STUDENT_TABLE, contentValues, STUDENT_ID + " ? ", new String[]{ std_id });
        sqLiteDatabase.execSQL("UPDATE "+STUDENT_TABLE+" SET "+ LEARNING_LEVEL +" = '"+ new_level + "' WHERE "+ CLOUD_ID + " = '"+ std_id+ "'");
        //db.execSQL("UPDATE DB_TABLE SET YOUR_COLUMN='newValue' WHERE id=6 ");
        return true;

    }

    public boolean updateStudent(String std_id, String firstname, String lastname, String age, String gender, String notes, String std_class){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        // create contentValues
        //ContentValues contentValues = new ContentValues();
        //contentValues.put(LEARNING_LEVEL, new_level);
        //sqLiteDatabase.update(STUDENT_TABLE, contentValues, STUDENT_ID + " ? ", new String[]{ std_id });
        sqLiteDatabase.execSQL("UPDATE "+STUDENT_TABLE+" SET "+ FIRSTNAME +" = '"+ firstname + "' WHERE "+ CLOUD_ID + " = '"+ std_id+ "'");
        sqLiteDatabase.execSQL("UPDATE "+STUDENT_TABLE+" SET "+ LASTNAME +" = '"+ lastname + "' WHERE "+ CLOUD_ID + " = '"+ std_id+ "'");
        sqLiteDatabase.execSQL("UPDATE "+STUDENT_TABLE+" SET "+ AGE +" = '"+ age + "' WHERE "+ CLOUD_ID + " = '"+ std_id+ "'");
        sqLiteDatabase.execSQL("UPDATE "+STUDENT_TABLE+" SET "+ GENDER +" = '"+ gender + "' WHERE "+ CLOUD_ID + " = '"+ std_id+ "'");
        sqLiteDatabase.execSQL("UPDATE "+STUDENT_TABLE+" SET "+ NOTES +" = '"+ notes + "' WHERE "+ CLOUD_ID + " = '"+ std_id+ "'");
        sqLiteDatabase.execSQL("UPDATE "+STUDENT_TABLE+" SET "+ STD_CLASS +" = '"+ std_class + "' WHERE "+ CLOUD_ID + " = '"+ std_id+ "'");
        //db.execSQL("UPDATE DB_TABLE SET YOUR_COLUMN='newValue' WHERE id=6 ");
        return true;

    }

    public String addAssessment(Assessment assessment){
        //get WriteAble Database
        String uuid;
        uuid = UUID.randomUUID().toString();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        // create contentValues
        ContentValues contentValues = new ContentValues();
        contentValues.put(STUDENT_ID ,assessment.getSTUDENT_ID());
        contentValues.put(TIMESTAMP , new Date(System.currentTimeMillis()).toString());
        contentValues.put(LOCAL_ID , uuid);
        contentValues.put(ASSESSMENT_KEY ,assessment.getASSESSMENT_KEY());
        contentValues.put(LETTERS_CORRECT ,assessment.getLETTERS_CORRECT());
        contentValues.put(WORDS_CORRECT ,assessment.getWORDS_CORRECT());
        contentValues.put(LETTERS_WRONG ,assessment.getLETTERS_WRONG());
        contentValues.put(WORDS_WRONG ,assessment.getWORDS_WRONG());
        contentValues.put(PARAGRAPH_WORDS_WRONG,assessment.getPARAGRAPH_WORDS_WRONG());
        //contentValues.put(STORY_WORDS_WRONG, assessment.getSTORY_WORDS_WRONG());
        contentValues.put(STORY_ANS_Q1,assessment.getSTORY_ANS_Q1());
        contentValues.put(STORY_ANS_Q2,assessment.getSTORY_ANS_Q2());
        contentValues.put(LEARNING_LEVEL,assessment.getLEARNING_LEVEL());
        //Add Values into Database
        try{
            long r = sqLiteDatabase.insert(ASSESSMENT_TABLE, null,contentValues);
            return Long.toString(r);
        }catch (Error error){
        }

        return uuid;
    }

    public ArrayList getAllStudent(){
        // Get Readable Database
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Student> arrayList = new ArrayList<Student>();
        //Create Cursor to select All values
        Cursor cursor = sqLiteDatabase.rawQuery("select * from "+ STUDENT_TABLE +" ORDER BY "+ TIMESTAMP + " DESC ", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            // create student_activity object from entry
            Student student = new Student();
            student.setFirstname(cursor.getString(cursor.getColumnIndex(FIRSTNAME)));
            student.setLastname(cursor.getString(cursor.getColumnIndex(LASTNAME)));
            student.setLearning_level(cursor.getString(cursor.getColumnIndex(LEARNING_LEVEL)));
            student.setGender(cursor.getString(cursor.getColumnIndex(GENDER)));
            student.setNotes(cursor.getString(cursor.getColumnIndex(NOTES)));
            student.setAge(cursor.getString(cursor.getColumnIndex(AGE)));
            student.setTimestamp(cursor.getString(cursor.getColumnIndex(TIMESTAMP)));
            student.setLocal_id(cursor.getString(cursor.getColumnIndex(LOCAL_ID)));
            student.setStd_class(cursor.getString(cursor.getColumnIndex(STD_CLASS)));
            arrayList.add(student);
            cursor.moveToNext();
        }
        return arrayList;
    }

    public int FindStudent(String cloud_id){

        int len = 0;

        // Get Readable Database
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Student> arrayList = new ArrayList<Student>();
        //Create Cursor to select All values
        Cursor cursor = sqLiteDatabase.rawQuery("select * from "+ STUDENT_TABLE +" WHERE " + CLOUD_ID + " = '"+cloud_id+"'"+ " ORDER BY "+ TIMESTAMP + " DESC ", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            // create student_activity object from entry
            Student student = new Student();
            student.setFirstname(cursor.getString(cursor.getColumnIndex(FIRSTNAME)));
            student.setLastname(cursor.getString(cursor.getColumnIndex(LASTNAME)));
            student.setLearning_level(cursor.getString(cursor.getColumnIndex(LEARNING_LEVEL)));
            student.setGender(cursor.getString(cursor.getColumnIndex(GENDER)));
            student.setNotes(cursor.getString(cursor.getColumnIndex(NOTES)));
            student.setAge(cursor.getString(cursor.getColumnIndex(AGE)));
            student.setTimestamp(cursor.getString(cursor.getColumnIndex(TIMESTAMP)));
            student.setLocal_id(cursor.getString(cursor.getColumnIndex(LOCAL_ID)));
            student.setStd_class(cursor.getString(cursor.getColumnIndex(STD_CLASS)));
            arrayList.add(student);
            cursor.moveToNext();
        }
        len = arrayList.size();
        return len;

    }

    public Student getStudent(String cloud_id){

        int len = 0;
        Student student = null;

        // Get Readable Database
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Student> arrayList = new ArrayList<Student>();
        //Create Cursor to select All values
        Cursor cursor = sqLiteDatabase.rawQuery("select * from "+ STUDENT_TABLE +" WHERE " + CLOUD_ID + " = '"+cloud_id+"'"+ " ORDER BY "+ TIMESTAMP + " DESC ", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            // create student_activity object from entry
            student = new Student();
            student.setFirstname(cursor.getString(cursor.getColumnIndex(FIRSTNAME)));
            student.setLastname(cursor.getString(cursor.getColumnIndex(LASTNAME)));
            student.setLearning_level(cursor.getString(cursor.getColumnIndex(LEARNING_LEVEL)));
            student.setGender(cursor.getString(cursor.getColumnIndex(GENDER)));
            student.setNotes(cursor.getString(cursor.getColumnIndex(NOTES)));
            student.setAge(cursor.getString(cursor.getColumnIndex(AGE)));
            student.setTimestamp(cursor.getString(cursor.getColumnIndex(TIMESTAMP)));
            student.setLocal_id(cursor.getString(cursor.getColumnIndex(LOCAL_ID)));
            student.setStd_class(cursor.getString(cursor.getColumnIndex(STD_CLASS)));
            arrayList.add(student);
            cursor.moveToNext();
        }
        len = arrayList.size();
        return student;

    }
    public ArrayList getAllStudentOfInstructor(String instructor_id){
        // Get Readable Database
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Student> arrayList = new ArrayList<Student>();
        //Create Cursor to select All values
        Cursor cursor = sqLiteDatabase.rawQuery("select * from "+ STUDENT_TABLE +" WHERE " + INSTRUCTOR_ID + " = '"+instructor_id+"'"+ " ORDER BY "+ TIMESTAMP + " DESC ", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            // create student_activity object from entry
            Student student = new Student();
            student.setFirstname(cursor.getString(cursor.getColumnIndex(FIRSTNAME)));
            student.setLastname(cursor.getString(cursor.getColumnIndex(LASTNAME)));
            student.setLearning_level(cursor.getString(cursor.getColumnIndex(LEARNING_LEVEL)));
            student.setGender(cursor.getString(cursor.getColumnIndex(GENDER)));
            student.setNotes(cursor.getString(cursor.getColumnIndex(NOTES)));
            student.setAge(cursor.getString(cursor.getColumnIndex(AGE)));
            student.setTimestamp(cursor.getString(cursor.getColumnIndex(TIMESTAMP)));
            student.setLocal_id(cursor.getString(cursor.getColumnIndex(LOCAL_ID)));
            student.setStd_class(cursor.getString(cursor.getColumnIndex(STD_CLASS)));
            student.setCloud_id(cursor.getString(cursor.getColumnIndex(CLOUD_ID)));
            arrayList.add(student);
            cursor.moveToNext();
        }
        return arrayList;
    }


    public ArrayList getAllAssessment(){
        // Get Readable Database
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Assessment> arrayList = new ArrayList<Assessment>();
        //Create Cursor to select All values
        Cursor cursor = sqLiteDatabase.rawQuery("select * from "+ ASSESSMENT_TABLE+ " ORDER BY "+ TIMESTAMP, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Assessment assessment = new Assessment();
            assessment.setLOCAL_ID(cursor.getString(cursor.getColumnIndex(LOCAL_ID)));
            //assessment.setSTUDENT_ID(cursor.getString(cursor.getColumnIndex(STUDENT_ID)));
            assessment.setASSESSMENT_KEY(cursor.getString(cursor.getColumnIndex(ASSESSMENT_KEY)));
            assessment.setWORDS_CORRECT(cursor.getString(cursor.getColumnIndex(WORDS_CORRECT)));
            assessment.setWORDS_WRONG(cursor.getString(cursor.getColumnIndex(WORDS_WRONG)));
            assessment.setLETTERS_WRONG(cursor.getString(cursor.getColumnIndex(LETTERS_WRONG)));
            assessment.setLETTERS_CORRECT(cursor.getString(cursor.getColumnIndex(LETTERS_CORRECT)));
            assessment.setSTORY_ANS_Q1(cursor.getString(cursor.getColumnIndex(STORY_ANS_Q1)));
            assessment.setSTORY_ANS_Q2(cursor.getString(cursor.getColumnIndex(STORY_ANS_Q2)));
            assessment.setLEARNING_LEVEL(cursor.getString(cursor.getColumnIndex(LEARNING_LEVEL)));
            assessment.setPARAGRAPH_WORDS_WRONG(cursor.getString(cursor.getColumnIndex(PARAGRAPH_WORDS_WRONG)));
            //assessment.setSTORY_WORDS_WRONG(cursor.getString(cursor.getColumnIndex(STORY_WORDS_WRONG)));
            assessment.setTIMESTAMP(cursor.getString(cursor.getColumnIndex(TIMESTAMP)));
            arrayList.add(assessment);
            cursor.moveToNext();
        }
        //return cursor.getCount();
        return arrayList;
    }

    public ArrayList getAllStudentAssessment(String std_id){
        // Get Readable Database
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Assessment> arrayList = new ArrayList<Assessment>();
        //Create Cursor to select All values
        Cursor cursor = sqLiteDatabase.rawQuery("select * from "+ ASSESSMENT_TABLE +" where "+ STUDENT_ID + "='" + std_id +"'"+ " ORDER BY "+ TIMESTAMP +" ASC ", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Assessment assessment = new Assessment();
            assessment.setLOCAL_ID(cursor.getString(cursor.getColumnIndex(LOCAL_ID)));
            //assessment.setSTUDENT_ID(cursor.getString(cursor.getColumnIndex(STUDENT_ID)));
            assessment.setASSESSMENT_KEY(cursor.getString(cursor.getColumnIndex(ASSESSMENT_KEY)));
            assessment.setWORDS_CORRECT(cursor.getString(cursor.getColumnIndex(WORDS_CORRECT)));
            assessment.setWORDS_WRONG(cursor.getString(cursor.getColumnIndex(WORDS_WRONG)));
            assessment.setLETTERS_WRONG(cursor.getString(cursor.getColumnIndex(LETTERS_WRONG)));
            assessment.setLETTERS_CORRECT(cursor.getString(cursor.getColumnIndex(LETTERS_CORRECT)));
            assessment.setSTORY_ANS_Q1(cursor.getString(cursor.getColumnIndex(STORY_ANS_Q1)));
            assessment.setSTORY_ANS_Q2(cursor.getString(cursor.getColumnIndex(STORY_ANS_Q2)));
            assessment.setLEARNING_LEVEL(cursor.getString(cursor.getColumnIndex(LEARNING_LEVEL)));
            assessment.setPARAGRAPH_WORDS_WRONG(cursor.getString(cursor.getColumnIndex(PARAGRAPH_WORDS_WRONG)));
            //assessment.setSTORY_WORDS_WRONG(cursor.getString(cursor.getColumnIndex(STORY_WORDS_WRONG)));
            assessment.setTIMESTAMP(cursor.getString(cursor.getColumnIndex(TIMESTAMP)));
            arrayList.add(assessment);
            cursor.moveToNext();
        }
        return arrayList;
    }

    public Boolean addTeacher(Instructor instructor) {
        //get WriteAble Database
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        // create contentValues
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOCAL_ID, instructor.getLocal_id());
        contentValues.put(CLOUD_ID, instructor.getCloud_id());
        contentValues.put(FIRSTNAME,instructor.getFirstname());
        contentValues.put(LASTNAME, instructor.getLastname());
        contentValues.put(EMAIL,instructor.getEmail());
        contentValues.put(PASSWORD, instructor.getPassword());
        contentValues.put(TIMESTAMP,instructor.getTimestamp());

        //Add Values into Database
        sqLiteDatabase.insert(INSTRUCTOR_TABLE, null,contentValues);
        return true;
    }

    public Instructor getInstructor(){

        Instructor instructor = new Instructor();

        // Get Readable Database
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from "+ INSTRUCTOR_TABLE , null); // get instructor by id

        if(cursor.getCount() == 0){
            return null;
        }
        cursor.moveToFirst();
        instructor.setFirstname(cursor.getString(cursor.getColumnIndex(FIRSTNAME))); // doesn't need
        //instructor.setLastname(Integer.toString(cursor.getCount()));
        instructor.setLastname(cursor.getString(cursor.getColumnIndex(LASTNAME))); // doesn't need
        instructor.setPassword(cursor.getString(cursor.getColumnIndex(PASSWORD)));
        instructor.setCloud_id(cursor.getString(cursor.getColumnIndex(CLOUD_ID)));
        instructor.setLocal_id(cursor.getString(cursor.getColumnIndex(LOCAL_ID)));
        instructor.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));

        //Toast.makeText(this, cursor.getString(cursor.getColumnIndex(LASTNAME)), Toast.LENGTH_LONG).show();


        return instructor;

    }

    public boolean deleteInstructor(String instructor_id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM "+INSTRUCTOR_TABLE +" WHERE "+ CLOUD_ID + " = '"+ instructor_id+ "'");
        return true;
    }
    public Instructor getInstructorByID(String instructor_id){

        Instructor instructor = new Instructor();

        // Get Readable Database
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        try  {
            /* retrieve the data */
            cursor = sqLiteDatabase.rawQuery("select * from "+ INSTRUCTOR_TABLE +" where "+ CLOUD_ID + "='" + instructor_id +"'", null); // get instructor by id
        } catch (SQLException e) {
            /* handle the exception properly */
            Log.i("MyActivity",e.toString());
        }
        //cursor = sqLiteDatabase.rawQuery("select * from "+ INSTRUCTOR_TABLE +" where "+ EMAIL + "='" + toString +"'", null); // get instructor by email
        if(cursor.getCount() == 0){
            return null;
        }
        cursor.moveToFirst();
        instructor.setFirstname(cursor.getString(cursor.getColumnIndex(FIRSTNAME))); // doesn't need
        //instructor.setFirstname(Integer.toString(cursor.getCount()));
        instructor.setLastname(cursor.getString(cursor.getColumnIndex(LASTNAME))); // doesn't need
        instructor.setPassword(cursor.getString(cursor.getColumnIndex(PASSWORD)));
        instructor.setCloud_id(cursor.getString(cursor.getColumnIndex(CLOUD_ID)));
        instructor.setLocal_id(cursor.getString(cursor.getColumnIndex(LOCAL_ID)));
        instructor.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));

        return instructor;

    }

    public Instructor getInstructorByEmail(String toString) {
        Instructor instructor = new Instructor();

        // Get Readable Database
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        try  {
            /* retrieve the data */
            cursor = sqLiteDatabase.rawQuery("select * from "+ INSTRUCTOR_TABLE +" where "+ EMAIL + "='" + toString +"'", null); // get instructor by email
        } catch (SQLException e) {
            /* handle the exception properly */
            Log.i("MyActivity",e.toString());
        }
        //cursor = sqLiteDatabase.rawQuery("select * from "+ INSTRUCTOR_TABLE +" where "+ EMAIL + "='" + toString +"'", null); // get instructor by email
        if(cursor.getCount() == 0){
          return null;
        }
        cursor.moveToFirst();
        instructor.setFirstname(cursor.getString(cursor.getColumnIndex(FIRSTNAME))); // doesn't need
        instructor.setLastname(cursor.getString(cursor.getColumnIndex(LASTNAME))); // doesn't need
        instructor.setPassword(cursor.getString(cursor.getColumnIndex(PASSWORD)));
        instructor.setCloud_id(cursor.getString(cursor.getColumnIndex(CLOUD_ID)));
        instructor.setLocal_id(cursor.getString(cursor.getColumnIndex(LOCAL_ID)));

        return instructor;
    }
}
