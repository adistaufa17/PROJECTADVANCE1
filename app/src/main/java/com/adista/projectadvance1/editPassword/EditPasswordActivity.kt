package com.adista.projectadvance1.editPassword

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adista.projectadvance1.R
import com.adista.projectadvance1.databinding.ActivityEditPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPasswordBinding
    private val viewModel: EditPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_password)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.btnSave.setOnClickListener {
            viewModel.changePassword { success, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) finish() // kembali ke profil setelah berhasil
            }
        }


    }
}
