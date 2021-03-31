package com.nyansapo;

import android.util.Log;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class NyansapoNLP {
    private static final String TAG = "NyansapoNLP";

    static Hashtable<String, Integer> ans_s3_dict_1;
    static Hashtable<String, Integer> ans_s3_dict_2;
    static Hashtable<String, Integer> ans_s4_dict_1;
    static Hashtable<String, Integer> ans_s4_dict_2;
    static Hashtable<String, Integer> ans_s5_dict_1;
    static Hashtable<String, Integer> ans_s5_dict_2;
    static Hashtable<String, Integer> ans_s6_dict_1;
    static Hashtable<String, Integer> ans_s6_dict_2;
    static Hashtable<String, Integer> ans_s7_dict_1;
    static Hashtable<String, Integer> ans_s7_dict_2;

    static {

        ans_s3_dict_1 = new Hashtable<String, Integer>();
        ans_s3_dict_1.put("janet", 50);
        ans_s3_dict_1.put("birthday", 20);
        ans_s3_dict_1.put("party", 20);
        ans_s3_dict_1.put("had", 5);
        ans_s3_dict_1.put("a", 5);

        ans_s3_dict_2 = new Hashtable<String, Integer>();
        ans_s3_dict_2.put("sang", 25);
        ans_s3_dict_2.put("sung", 25);
        ans_s3_dict_2.put("and", 5);
        ans_s3_dict_2.put("danced", 25);
        ans_s3_dict_2.put("two", 20);
        ans_s3_dict_2.put("hours", 20);
        ans_s3_dict_2.put("sweet", 5);
        ans_s3_dict_2.put("eat", 5);
        ans_s3_dict_2.put("cake", 5);
        ans_s3_dict_2.put("juice", 5);
        ans_s3_dict_2.put("beans", 5);

        ans_s4_dict_1 = new Hashtable<String, Integer>();
        ans_s4_dict_1.put("fifth", 35);
        ans_s4_dict_1.put("5", 35);
        ans_s4_dict_1.put("5th", 35);
        ans_s4_dict_1.put("may", 35);
        ans_s4_dict_1.put("our", 10);
        ans_s4_dict_1.put("school", 10);
        ans_s4_dict_1.put("opened", 5);
        ans_s4_dict_1.put("on", 5);

        ans_s4_dict_2 = new Hashtable<String, Integer>();
        ans_s4_dict_2.put("peter", 25);
        ans_s4_dict_2.put("and", 5);
        ans_s4_dict_2.put("jim", 25);
        ans_s4_dict_2.put("not", 10);
        ans_s4_dict_2.put("fight", 20);
        ans_s4_dict_2.put("told", 10);
        ans_s4_dict_2.put("she", 5);
        ans_s4_dict_2.put("them", 10);

        ans_s5_dict_1 = new Hashtable<String, Integer>();
        ans_s5_dict_1.put("many", 20);
        ans_s5_dict_1.put("wild", 30);
        ans_s5_dict_1.put("animals", 30);
        ans_s5_dict_1.put("forest", 10);
        ans_s5_dict_1.put("had", 5);
        ans_s5_dict_1.put("it", 5);

        ans_s5_dict_2 = new Hashtable<String, Integer>();
        ans_s5_dict_2.put("lion", 25);
        ans_s5_dict_2.put("roared", 25);
        ans_s5_dict_2.put("at", 10);
        ans_s5_dict_2.put("them", 20);
        ans_s5_dict_2.put("calf", 10);
        ans_s5_dict_2.put("cow", 10);
        ans_s5_dict_2.put("and", 5);

        ans_s6_dict_1 = new Hashtable<String, Integer>();
        ans_s6_dict_1.put("ali", 10);
        ans_s6_dict_1.put("has", 10);
        ans_s6_dict_1.put("four", 60);
        ans_s6_dict_1.put("children", 20);
        ans_s6_dict_1.put("4", 60);

        ans_s6_dict_2 = new Hashtable<String, Integer>();
        ans_s6_dict_2.put("fish", 60);
        ans_s6_dict_2.put("lot", 20);
        ans_s6_dict_2.put("has", 10);
        ans_s6_dict_2.put("turkana", 60);
        ans_s6_dict_2.put("fishing", 5);
        ans_s6_dict_2.put("boat", 5);


        ans_s7_dict_1 = new Hashtable<String, Integer>();
        ans_s7_dict_1.put("hare", 10);
        ans_s7_dict_1.put("hyena", 10);
        ans_s7_dict_1.put("see", 20);
        ans_s7_dict_1.put("dance", 50);
        ans_s7_dict_1.put("come", 10);
        ans_s7_dict_1.put("him", 10);
        ans_s7_dict_1.put("invited", 5);
        ans_s7_dict_1.put("home", 5);

        ans_s7_dict_2 = new Hashtable<String, Integer>();
        ans_s7_dict_2.put("most", 10);
        ans_s7_dict_2.put("clapped", 30);
        ans_s7_dict_2.put("hyena", 10);
        ans_s7_dict_2.put("knew", 10);
        ans_s7_dict_2.put("win", 20);
        ans_s7_dict_2.put("winner", 20);
        ans_s7_dict_2.put("animals", 10);

    }

    public int evaluateAnswer(String ans, int assessment_key, int question){
    //   Log.d(TAG, "evaluateAnswer: ans:$ans :assessment_key:$asse");
        String[] tokens = ans.split(" ");//splitting answer to list

        //score 110 you passed

        int score = 0;
        for(String token: tokens){
            try{
                switch (assessment_key){
                    case 3:{
                        if(question == 0){ // question 1
                            if(ans_s3_dict_1.containsKey(token)){
                                score += ans_s3_dict_1.get(token.toLowerCase());
                            }
                        }else{ // question 2
                            if(ans_s3_dict_2.containsKey(token)){
                                score += ans_s3_dict_2.get(token.toLowerCase());
                            }
                        }

                    }
                    case 4: {
                        if(question == 0){ // question 1
                            if(ans_s4_dict_1.containsKey(token)){
                                score += ans_s4_dict_1.get(token.toLowerCase());
                            }
                        }else{ // question 2
                            if(ans_s4_dict_2.containsKey(token)){
                                score += ans_s4_dict_2.get(token.toLowerCase());
                            }
                        }

                    }
                    case 5: {
                        if(question == 0){ // question 1
                            if(ans_s5_dict_1.containsKey(token)){
                                score += ans_s5_dict_1.get(token.toLowerCase());
                            }
                        }else{ // question 2
                            if(ans_s5_dict_2.containsKey(token)){
                                score += ans_s5_dict_2.get(token.toLowerCase());
                            }
                        }
                    }
                    case 6: {
                        if(question == 0){ // question 1
                            if(ans_s6_dict_1.containsKey(token)){
                                score += ans_s5_dict_1.get(token.toLowerCase());
                            }
                        }else{ // question 2
                            if(ans_s6_dict_2.containsKey(token)){
                                score += ans_s5_dict_2.get(token.toLowerCase());
                            }
                        }
                    }
                    case 7: {
                        if(question == 0){ // question 1
                            if(ans_s7_dict_1.containsKey(token)){
                                score += ans_s5_dict_1.get(token.toLowerCase());
                            }
                        }else{ // question 2
                            if(ans_s7_dict_2.containsKey(token)){
                                score += ans_s5_dict_2.get(token.toLowerCase());
                            }
                        }
                    }
                    default: {
                        score += 0;
                    }
                }
            }catch (Error err){
                return 0;
            }
        }

        return score;
    }
}
