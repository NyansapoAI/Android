package com.nyansapo.presentation.ui.assessment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.nyansapo.R
import com.nyansapo.SelectAssessment
import com.nyansapo.databinding.ActivityBeginAssessMentChooserBinding
import com.nyansapo.databinding.ActivityBeginAssessementBinding
import com.nyansapo.presentation.ui.main.MainActivity2
import com.nyansapo.presentation.utils.GlobalData
import kotlinx.android.synthetic.main.activity_begin_assess_ment_chooser.*

class AvatarChooserFragment : Fragment(R.layout.activity_begin_assess_ment_chooser), View.OnClickListener {

    private val TAG = "AvatarChooserFragment"

    lateinit var binding: ActivityBeginAssessMentChooserBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ActivityBeginAssessMentChooserBinding.bind(view)

        setOnClickListeners()

    }

    private fun setOnClickListeners() {
        binding.apply {
            cheetaImageView.setOnClickListener(this@AvatarChooserFragment)
            lionImageView.setOnClickListener(this@AvatarChooserFragment)
            rhinoImageView.setOnClickListener(this@AvatarChooserFragment)
            buffaloImageView.setOnClickListener(this@AvatarChooserFragment)
            elephantImageView.setOnClickListener(this@AvatarChooserFragment)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cheetaImageView -> {
                GlobalData.avatar = R.drawable.nyansapo_avatar_cheeta
            }
            R.id.lionImageView -> {
                GlobalData.avatar = R.drawable.nyansapo_avatar_lion
            }
            R.id.rhinoImageView -> {
                GlobalData.avatar = R.drawable.nyansapo_avatar_rhino
            }
            R.id.buffaloImageView -> {
                GlobalData.avatar = R.drawable.nyansapo_avatar_waterbuffalo
            }
            R.id.elephantImageView -> {
                GlobalData.avatar = R.drawable.nyansapo_avatar_elelphant
            }

        }


        goToSelectAssessment()

    }

    private fun goToSelectAssessment() {

        Log.d(TAG, "addAssessment: btn clicked ")
        val intent = Intent(MainActivity2.activityContext!!, SelectAssessment::class.java)
        startActivity(intent)
    }
}