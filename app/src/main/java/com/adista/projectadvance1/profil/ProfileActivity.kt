package com.adista.projectadvance1.profil

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adista.projectadvance1.R
import com.adista.projectadvance1.databinding.ActivityProfilBinding
import com.adista.projectadvance1.profil.ProfilViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfilBinding
    private val viewModel: ProfilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profil)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }
}
