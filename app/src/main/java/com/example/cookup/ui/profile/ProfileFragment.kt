package com.example.cookup.ui.profile
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.example.cookup.R
import com.example.cookup.models.User
import com.example.cookup.view_models.AuthViewModel
import java.util.concurrent.Executors
import kotlin.getValue

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val authViewModel: AuthViewModel by viewModels()
    private var cachedImagePath: String? = null
    private val args: ProfileFragmentArgs by navArgs()
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.user != null) {
            user = args.user!!
        } else {
            user = authViewModel.user.value!!
        }


        Executors.newSingleThreadExecutor().execute {
            val future = Glide.with(requireContext())
                .downloadOnly()
                .load(user.profileImageUrl)
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
        setUserDetails()

        val settingButton = view.findViewById<Button>(R.id.settingButton)
        settingButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ProfileSettingsFragment)
        }

        authViewModel.updateStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                setUserDetails()
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.gridFragmentContainer, RecipeGridFragment())
            .commit()

    }

    private fun setUserDetails() {
        val usernameTextView = view?.findViewById<TextView>(R.id.username)
        val profileImageView = view?.findViewById<ImageView>(R.id.profileImageView)

        usernameTextView?.text = user.username
        if (user.profileImageUrl?.isNotEmpty() == true) {
            cachedImagePath?.let {
                Glide.with(this)
                    .load(it)
                    .into(profileImageView!!)
            } ?: run {
                Glide.with(this)
                    .load(user.profileImageUrl)
                    .into(profileImageView!!)
            }
        }
    }
}
