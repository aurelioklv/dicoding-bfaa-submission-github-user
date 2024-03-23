package com.aurelioklv.githubuser.ui.details

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aurelioklv.githubuser.R
import com.aurelioklv.githubuser.data.response.UserResponse
import com.aurelioklv.githubuser.databinding.ActivityDetailsBinding
import com.aurelioklv.githubuser.ui.formatCount
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private val viewModel: DetailsViewModel by viewModels()

    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (intent != null && intent.hasExtra(EXTRA_USERNAME)) {
            username = intent.getStringExtra(EXTRA_USERNAME)
            viewModel.getUserDetails(username!!)
        }

        setupViewPager()
        observeLiveData()
    }

    private fun setupViewPager() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this, username!!)
        val viewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter

        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    private fun observeLiveData() {
        viewModel.user.observe(this) { setUserDetails(it) }
        viewModel.isLoading.observe(this) { showLoading(it) }
        viewModel.errorMessage.observe(this) {
            if (it.isNotEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUserDetails(userResponse: UserResponse) {
        with(userResponse) {
            Glide.with(this@DetailsActivity)
                .load(avatarUrl)
                .into(binding.ivUserAvatar)
            Log.i(TAG, "name: $name\nusername: $login")
            if (name.isNullOrEmpty() || name.isBlank()) {
                binding.tvName.visibility = View.INVISIBLE

                val usernameLayoutParams =
                    binding.tvUsername.layoutParams as ConstraintLayout.LayoutParams
                usernameLayoutParams.topToTop = binding.ivUserAvatar.id
                usernameLayoutParams.bottomToBottom = binding.ivUserAvatar.id
            } else {
                binding.tvName.text = name
            }
            binding.tvUsername.text = userResponse.login

            if (bio.isNullOrEmpty() || bio.isBlank()) {
                binding.tvBio.visibility = View.INVISIBLE

                val followersLayoutParams =
                    binding.tvFollowers.layoutParams as ConstraintLayout.LayoutParams
                followersLayoutParams.topToBottom = binding.ivUserAvatar.id
            } else {
                binding.tvBio.text = bio
            }

            Log.i(TAG, "followers: $followers\nfollowing: $following")
            binding.tvFollowers.text =
                resources.getQuantityString(
                    R.plurals.followers,
                    followers,
                    formatCount(followers.toLong())
                )
            binding.tvFollowing.text = getString(R.string.following, following)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val TAG = "DetailsActivity"
        const val EXTRA_USERNAME = "username"

        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.tab_text_1, R.string.tab_text_2)
    }
}