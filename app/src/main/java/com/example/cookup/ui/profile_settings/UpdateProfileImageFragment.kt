package com.example.cookup.ui.profile_settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.cookup.R
import com.example.cookup.services.FirestoreService
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.widget.LinearLayout
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import java.util.concurrent.Executors

class UpdateProfileImageFragment : Fragment(R.layout.fragment_update_profile_image) {
    private lateinit var navController: NavController
    private lateinit var firestoreService: FirestoreService
    private lateinit var progressBar: ProgressBar
    private lateinit var editLayout: LinearLayout
    private lateinit var buttonsLayout: LinearLayout
    private var selectedImageUri: Uri? = null
    private var cachedImagePath: String? = null
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data: Intent? = result.data
            selectedImageUri = data?.data
            view?.findViewById<ImageView>(R.id.profileImageView)?.setImageURI(selectedImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = requireContext().getSharedPreferences("CookUpPrefs", Context.MODE_PRIVATE)
        val profileImageUrl = sharedPref.getString("profileImageUrl", "").toString()

        Executors.newSingleThreadExecutor().execute {
            val future = Glide.with(requireContext())
                .downloadOnly()
                .load(profileImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)

            try {
                val file = future.get()
                cachedImagePath = file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        firestoreService = FirestoreService()
        viewUserProfile()

        var btnUpload = view.findViewById<Button>(R.id.btnUpload)
        var btnSave = view.findViewById<Button>(R.id.btnSave)
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        editLayout = view.findViewById<LinearLayout>(R.id.editLayout)
        buttonsLayout = view.findViewById<LinearLayout>(R.id.buttonsLayout)

        btnUpload.setOnClickListener {
            openGallery()
        }

        btnSave.setOnClickListener {
            selectedImageUri?.let { uri ->
                setLoading(true)
                firestoreService.uploadProfileImage(uri,
                    onSuccess = { imageUrl ->
                        setLoading(false)
                        Toast.makeText(requireContext(), "תמונת פרופיל עודכנה", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onFailure = { error ->
                        setLoading(false)
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    })
            } ?: Toast.makeText(requireContext(), "יש לבחור תמונה", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            navController.popBackStack()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        imagePickerLauncher.launch(intent)
    }

    private fun viewUserProfile() {
        val profileImageView = view?.findViewById<ImageView>(R.id.profileImageView)
        val sharedPref = requireContext().getSharedPreferences("CookUpPrefs", Context.MODE_PRIVATE)
        val profileImageUrl = sharedPref.getString("profileImageUrl", "").toString()

        if (profileImageUrl.isNotEmpty()) {
            cachedImagePath?.let {
                Glide.with(this)
                    .load(it)
                    .into(profileImageView!!)
            } ?: run {
                Glide.with(this)
                    .load(profileImageUrl)
                    .into(profileImageView!!)
            }
        }
    }

    private fun setLoading(value: Boolean) {
        if (value) {
            progressBar.visibility = View.VISIBLE
            editLayout.visibility = View.GONE
            buttonsLayout.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            editLayout.visibility = View.VISIBLE
            buttonsLayout.visibility = View.VISIBLE
        }
    }
}
