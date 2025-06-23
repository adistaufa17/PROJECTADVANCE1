package com.adista.projectadvance1.profil

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adista.projectadvance1.editPassword.EditPasswordActivity
import com.adista.projectadvance1.util.ImageUtil
import com.adista.projectadvance1.R
import com.adista.projectadvance1.databinding.ActivityProfilBinding
import com.adista.projectadvance1.editProfil.EditProfileActivity 
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
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

        binding.btnEdit.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(this, EditPasswordActivity::class.java))
        }

        viewModel.photoUrl.observe(this) { path ->
            val imageUrl = ImageUtil.getFullImageUrl(path)
            Glide.with(this)
                .load(imageUrl)
                .transform(CircleCrop())
                .into(binding.ivProfile)
        }

        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
