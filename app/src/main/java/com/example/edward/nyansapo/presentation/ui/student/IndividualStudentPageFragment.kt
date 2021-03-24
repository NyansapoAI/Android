package com.example.edward.nyansapo.presentation.ui.student

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.edward.nyansapo.Assessment
import com.example.edward.nyansapo.Assessment_Content
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.Student
import com.example.edward.nyansapo.databinding.ActivityIndividualStudentPageBinding
import com.example.edward.nyansapo.presentation.ui.main.MainActivity2
import com.example.edward.nyansapo.presentation.utils.assessmentDocumentSnapshot
import com.example.edward.nyansapo.presentation.utils.studentDocumentSnapshot
import es.dmoral.toasty.Toasty

class IndividualStudentPageFragment : Fragment(R.layout.activity_individual_student_page) {

    private val TAG = "IndividualStudentPageFr"

    lateinit var binding: ActivityIndividualStudentPageBinding
    lateinit var student: Student
    lateinit var assessment: Assessment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = ActivityIndividualStudentPageBinding.bind(view)
        student = studentDocumentSnapshot!!.toObject(Student::class.java)!!
        assessment = assessmentDocumentSnapshot!!.toObject(Assessment::class.java)!!
        setupToolBar()
        underLineHeaderTxt()
        setAssessmentInfoToUi()

    }

    private fun underLineHeaderTxt() {
        binding.apply {
            letterHeader.paint.isUnderlineText = true
            wordHeader.paint.isUnderlineText = true
            paragraphHeader.paint.isUnderlineText = true
            storyHeader.paint.isUnderlineText = true
        }
    }

    private fun setupToolBar() {
        //setting up name of students

        val fullname = "${studentDocumentSnapshot!!.toObject(Student::class.java)!!.firstname}  ${studentDocumentSnapshot!!.toObject(Student::class.java)!!.lastname}"
        binding.toolbar.root.title = fullname

    }

    private fun setAssessmentInfoToUi() {
//first determine which level we are in
        when (assessment.learningLevel) {
            "UNKNOWN" -> {
                Toasty.info(MainActivity2.activityContext!!, "You assessment is unknown You might have started the assessment but didnt complete", Toasty.LENGTH_LONG).show()
            }
            "BEGINNER" -> setDataForBeginnerLevel()
            "LETTER" -> setDataForLetterLevel()
            "WORD" -> setDataForWordLevel()
            "PARAGRAPH" -> setDataForParagraphLevel()
            "STORY" -> setDataForStoryLevel()
            "ABOVE" -> setDataForAboveLevel()


        }
    }

    private fun setDataForAboveLevel() {
        Log.d(TAG, "setDataForAboveLevel:")
        binding.learningLevelImageView.setImageResource(R.mipmap.above_level)

        binding.letterLinearLayout.visibility = View.GONE
        binding.wordLinearLayout.visibility = View.GONE

        startSettingParagraphs()
        startSettingStory()
    }

    private fun setDataForBeginnerLevel() {
        Log.d(TAG, "setDataForBeginnerLevel: ")
        binding.learningLevelImageView.setImageResource(R.mipmap.beginner_level)

        binding.storyLinearLayout.visibility = View.GONE

        startSettingLetters()
        startSettingWords()
        startSettingParagraphs()

    }

    private fun setDataForParagraphLevel() {
        Log.d(TAG, "setDataForParagraphLevel: ")
        binding.learningLevelImageView.setImageResource(R.mipmap.paragraph_level)

        binding.letterLinearLayout.visibility = View.GONE
        binding.wordLinearLayout.visibility = View.GONE

        startSettingParagraphs()
        startSettingStory()


    }

    private fun startSettingQuestions(isParagraph: Boolean) {
        val questions = getQuestions(assessment.assessmentKey)

        binding.q1TxtView.text = "Q1. ${questions[0]} "
        binding.q2TxtView.text = "Q2. ${questions[1]} "


        if (isParagraph) {
            binding.answer1TxtView.setTextColor(Color.RED)
            binding.answer2TxtView.setTextColor(Color.RED)
        }

        binding.answer1TxtView.text = assessment.storyAnswerQ1
        binding.answer2TxtView.text = assessment.storyAnswerQ2
    }

    private fun startSettingStory() {
        Log.d(TAG, "startSettingStory: ")
        val wholeStory = getStory(assessment.assessmentKey)


        val wordtoSpan: Spannable = SpannableString(wholeStory)



        Log.d(TAG, "startSettingStory: ${assessment.storyWordsWrong}")

        assessment.storyWordsWrong.split(",", ignoreCase = true).forEach { string ->

            if (!string.isBlank()) {

                underLineThisWord(string.trim(), wholeStory, wordtoSpan)

            }
        }

        binding.storyTxtView.text = wordtoSpan



        startSettingQuestions(false)

    }

    private fun setDataForStoryLevel() {
        Log.d(TAG, "setDataForStoryLevel: ")
        binding.learningLevelImageView.setImageResource(R.mipmap.story_level)

        binding.letterLinearLayout.visibility = View.GONE
        binding.wordLinearLayout.visibility = View.GONE

        startSettingParagraphs()
        startSettingStory()


    }

    private fun setDataForWordLevel() {
        Log.d(TAG, "setDataForWordLevel: ")
        binding.learningLevelImageView.setImageResource(R.mipmap.word_level)

        binding.letterLinearLayout.visibility = View.GONE
        binding.storyLinearLayout.visibility = View.GONE

        startSettingWords()
        startSettingParagraphs()

    }


    private fun setDataForLetterLevel() {
        Log.d(TAG, "setDataForLetterLevel: ")
        binding.learningLevelImageView.setImageResource(R.mipmap.letter_level)

        binding.storyLinearLayout.visibility = View.GONE

        startSettingLetters()
        startSettingWords()
        startSettingParagraphs()

    }

    private fun startSettingParagraphs() {
        Log.d(TAG, "startSettingParagraphs: ")
        val wholeParagraph = getPara(assessment.assessmentKey)[0]


        val wordtoSpan: Spannable = SpannableString(wholeParagraph)



        Log.d(TAG, "startSettingParagraphs: paragraph words wrong ${assessment.paragraphWordsWrong}")

        assessment.paragraphWordsWrong.split(",", ignoreCase = true).forEach { string ->

            if (!string.isBlank()) {

                underLineThisWord(string.trim().toLowerCase(), wholeParagraph.toLowerCase(), wordtoSpan)

            }
        }

        binding.paragraphTxtView.text = wordtoSpan

    }

    private fun underLineThisWord(string: String, wholeParagraph: String, wordsToSpan: Spannable) {
        Log.d(TAG, "underLineThisWord: started underlining words in paragraph/story")
        wholeParagraph.indexOf(string, ignoreCase = true).apply {
            val endOfString = this + string.length
            wordsToSpan.setSpan(ForegroundColorSpan(Color.RED), this, endOfString, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun startSettingWords() {
        Log.d(TAG, "startSettingWords words wrong: ${assessment.wordsWrong}")
        Log.d(TAG, "startSettingWords words correct: ${assessment.wordsCorrect}")


        var lastIndex: Int = 0
        var flag: Boolean = true

        assessment.wordsWrong.split(",", ignoreCase = true).filter {
            !it.isBlank()
        }.forEachIndexed { index, string ->
            Log.d(TAG, "startSettingWords: index: $index word: $string")

            flag = false
            val textView: TextView = binding.root.findViewWithTag<TextView>("word_$index")

            textView.setBackgroundResource(R.drawable.bg_wrong_word)
            textView.text = string.trim()

            lastIndex = index

            Log.d(TAG, "startSettingWords: last index $lastIndex")
        }

        if (flag) {
            lastIndex = -1
        }
        assessment.wordsCorrect.split(",", ignoreCase = true).filter {
            !it.isBlank()
        }.forEachIndexed { index, string ->
            Log.d(TAG, "startSettingWords: index: $index word: $string")

            val pos = lastIndex + 1 + index
            if (pos == 6) {
                Log.d(TAG, "startSettingWords: returning")
                return@forEachIndexed
            }
            val textView: TextView = binding.root.findViewWithTag<TextView>("word_${pos}")

            textView.setBackgroundResource(R.drawable.bg_correct_word)

            textView.text = string.trim()

        }

    }

    private fun startSettingLetters() {
        Log.d(TAG, "startSettingLetters: ")



        Log.d(TAG, "startSettingLetters letter wrong: ${assessment.lettersWrong}")
        Log.d(TAG, "startSettingLetters letter correct: ${assessment.letterCorrect}")


        var lastIndex: Int = 0
        var flag: Boolean = true

        assessment.lettersWrong.split(",", ignoreCase = true).filter {
            !it.isBlank()
        }.forEachIndexed { index, string ->

            flag=false

            val textView: TextView = binding.root.findViewWithTag<TextView>("letter_$index")
            textView.setBackgroundResource(R.drawable.bg_wrong_word)
            textView.text = string.trim()
            lastIndex = index
        }

        Log.d(TAG, "startSettingLetters: lastIndex :$lastIndex")
        if (flag) {
            lastIndex = -1
        }

        assessment.letterCorrect.split(",", ignoreCase = true).filter {
            !it.isBlank()
        }.forEachIndexed { index, string ->

            val pos = lastIndex + 1 + index
            if (pos == 6) {
                return@forEachIndexed
            }
            val textView: TextView = binding.root.findViewWithTag<TextView>("letter_${pos}")

            textView.setBackgroundResource(R.drawable.bg_correct_word)

            textView.text = string.trim()

        }

    }

    private fun setParagraphSpannableString() {


        val wordtoSpan: Spannable = SpannableString("One morning, the cow went to the lion. She\n" +
                "wanted him to help her. The lion roared at them.\n" +
                "The cow and her calf ran away. They found a\n" +
                "man outside his house. The man loved the\n" +
                "animals. He made a cow shed for them. The cow\n" +
                "never went back to the forest.")

        wordtoSpan.setSpan(ForegroundColorSpan(Color.RED), 17, 29, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        wordtoSpan.setSpan(ForegroundColorSpan(Color.RED), 50, 61, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        wordtoSpan.setSpan(ForegroundColorSpan(Color.RED), 90, 100, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        wordtoSpan.setSpan(ForegroundColorSpan(Color.RED), 152, 167, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.paragraphTxtView.text = wordtoSpan
    }


    fun getPara(key: String?): Array<String> {
        return when (key) {
            "3" -> {
                Assessment_Content.getP3()
            }
            "4" -> {
                Assessment_Content.getP4()
            }
            "5" -> {
                Assessment_Content.getP5()
            }
            "6" -> {
                Assessment_Content.getP6()
            }
            "7" -> {
                Assessment_Content.getP7()
            }
            "8" -> {
                Assessment_Content.getP8()
            }
            "9" -> {
                Assessment_Content.getP9()
            }
            "10" -> {
                Assessment_Content.getP10()
            }
            else -> Assessment_Content.getP3()
        }
    }

    fun getStory(key: String?): String {
        return when (key) {
            "3" -> {
                Assessment_Content.getS3()
            }
            "4" -> {
                Assessment_Content.getS4()
            }
            "5" -> {
                Assessment_Content.getS5()
            }
            "6" -> {
                Assessment_Content.getS6()
            }
            "7" -> {
                Assessment_Content.getS7()
            }
            "8" -> {
                Assessment_Content.getS8()
            }
            "9" -> {
                Assessment_Content.getS9()
            }
            "10" -> {
                Assessment_Content.getS10()
            }
            else -> Assessment_Content.getS3()
        }
    }

    fun getQuestions(key: String?): Array<String> {
        return when (key) {
            "3" -> {
                Assessment_Content.getQ3()
            }
            "4" -> {
                Assessment_Content.getQ4()
            }
            "5" -> {
                Assessment_Content.getQ5()
            }
            "6" -> {
                Assessment_Content.getQ6()
            }
            "7" -> {
                Assessment_Content.getQ7()
            }
            "8" -> {
                Assessment_Content.getQ8()
            }
            "9" -> {
                Assessment_Content.getQ9()
            }
            "10" -> {
                Assessment_Content.getQ10()
            }
            else -> Assessment_Content.getQ3()
        }
    }

}