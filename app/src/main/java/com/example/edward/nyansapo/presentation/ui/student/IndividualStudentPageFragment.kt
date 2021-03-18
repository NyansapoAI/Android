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
import com.example.edward.nyansapo.presentation.utils.assessmentDocumentSnapshot
import com.example.edward.nyansapo.presentation.utils.studentDocumentSnapshot
import com.google.common.reflect.Reflection.getPackageName

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

        setAssessmentInfoToUi()

    }

    private fun setAssessmentInfoToUi() {
//first determine which level we are in
        when (assessment.learningLevel) {
            "LETTER" -> setDataForLetterLevel()
            "WORD" -> setDataForWordLevel()
            "STORY" -> setDataForStoryLevel()
            "PARAGRAPH" -> setDataForParagraphLevel()


        }
    }

    private fun setDataForParagraphLevel() {
        Log.d(TAG, "setDataForParagraphLevel: ")
        binding.learningLevelImageView.setImageResource(R.mipmap.paragraph_level)


        binding.letterLinearLayout.visibility = View.GONE
        binding.wordLinearLayout.visibility = View.GONE
        startSettingParagraphs()

    }

    private fun setDataForStoryLevel() {
        Log.d(TAG, "setDataForStoryrLevel: ")
        binding.learningLevelImageView.setImageResource(R.mipmap.story_level)


        binding.letterLinearLayout.visibility = View.GONE

        startSettingParagraphs()

    }

    private fun setDataForWordLevel() {
        Log.d(TAG, "setDataForWordLevel: ")
        binding.learningLevelImageView.setImageResource(R.mipmap.word_level)
        binding.letterLinearLayout.visibility = View.GONE

        startSettingWords()
        startSettingParagraphs()

    }


    private fun setDataForLetterLevel() {
        Log.d(TAG, "setDataForLetterLevel: ")
        binding.learningLevelImageView.setImageResource(R.mipmap.letter_level)

        startSettingLetters()
        startSettingWords()
        startSettingParagraphs()

    }

    private fun startSettingParagraphs() {
        Log.d(TAG, "startSettingParagraphs: ")
        val wholeParagraph = getPara(assessment.assessmentKey)[0]


        val wordtoSpan: Spannable = SpannableString(wholeParagraph)



        Log.d(TAG, "startSettingParagraphs: ${assessment.paragraphWordsWrong}")

        assessment.paragraphWordsWrong.split(",", ignoreCase = true).forEach { string ->

            if (!string.isBlank()) {

                underLineThisWord(string.trim(), wholeParagraph, wordtoSpan)

            }
        }

        binding.paragraphTxtView.text = wordtoSpan

    }

    private fun underLineThisWord(string: String, wholeParagraph: String, wordsToSpan: Spannable) {
        Log.d(TAG, "underLineThisWord: started underlining words in paragraph")
        wholeParagraph.indexOf(string, ignoreCase = true).apply {
            val endOfString = this + string.length
            wordsToSpan.setSpan(ForegroundColorSpan(Color.RED), this, endOfString, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun startSettingWords() {
        Log.d(TAG, "startSettingWords: ")
        val words = assessment.wordsWrong + assessment.wordsCorrect

        Log.d(TAG, "startSettingWords: $words")


        var lastIndex: Int = 0



        assessment.wordsWrong.split(",", ignoreCase = true).filter {
            !it.isBlank()
        }.forEachIndexed { index, string ->

            val resID = resources.getIdentifier("word_$index",
                    "id", getPackageName(IndividualStudentPageFragment::class.java))


            val textView: TextView = binding.root.findViewById(resID)
            textView.setBackgroundResource(R.drawable.bg_wrong_word)
            textView.text = string.trim()

            lastIndex = index

        }


        assessment.wordsCorrect.split(",", ignoreCase = true).filter {
            !it.isBlank()
        }.forEachIndexed { index, string ->

            val resID = resources.getIdentifier("word_${lastIndex + 1 + index}",
                    "id", getPackageName(IndividualStudentPageFragment::class.java))


            val textView: TextView = binding.root.findViewById(resID)
            textView.setBackgroundResource(R.drawable.bg_correct_word)

            textView.text = string.trim()

        }

    }

    private fun startSettingLetters() {
        Log.d(TAG, "startSettingLetters: ")

        val letters = assessment.lettersWrong + assessment.letterCorrect

        Log.d(TAG, "startSettingLetters: $letters")


        var lastIndex: Int = 0

        assessment.lettersWrong.split(",", ignoreCase = true).filter {
            !it.isBlank()
        }.forEachIndexed { index, string ->

            val resID = resources.getIdentifier("letter_$index",
                    "id", getPackageName(IndividualStudentPageFragment::class.java))


            val textView: TextView = binding.root.findViewById(resID)
            textView.setBackgroundResource(R.drawable.bg_wrong_word)
            textView.text = string.trim()
            lastIndex = index
        }


        assessment.letterCorrect.split(",", ignoreCase = true).filter {
            !it.isBlank()
        }.forEachIndexed { index, string ->

            val resID = resources.getIdentifier("letter_${lastIndex + 1 + index}",
                    "id", getPackageName(IndividualStudentPageFragment::class.java))


            val textView: TextView = binding.root.findViewById(resID)
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

}