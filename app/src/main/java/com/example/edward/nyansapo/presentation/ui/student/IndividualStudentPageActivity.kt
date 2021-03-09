package com.example.edward.nyansapo.presentation.ui.student

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import com.example.edward.nyansapo.databinding.ActivityIndividualStudentPageBinding


class IndividualStudentPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityIndividualStudentPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIndividualStudentPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setParagraphSpannableString()
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
        wordtoSpan.setSpan(ForegroundColorSpan(Color.RED), 90,100, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        wordtoSpan.setSpan(ForegroundColorSpan(Color.RED), 152,167, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.paragraphTxtView.text = wordtoSpan
    }
}