package com.aurelioklv.githubuser.ui.details

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.aurelioklv.githubuser.R
import com.aurelioklv.githubuser.data.local.FavoriteUser
import com.aurelioklv.githubuser.data.remote.response.UserResponse
import com.aurelioklv.githubuser.databinding.ActivityDetailsBinding
import com.aurelioklv.githubuser.ui.formatCount
import com.aurelioklv.githubuser.util.ViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private val viewModel: DetailsViewModel by viewModels<DetailsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent != null && intent.hasExtra(EXTRA_USERNAME)) {
            username = intent.getStringExtra(EXTRA_USERNAME)
            viewModel.getUserDetails(username!!)
        }

        setupViewPager()
        observeLiveData()
        binding.fabFavorite.setOnClickListener {
            saveUser(viewModel.user.value!!)
        }
        supportActionBar?.hide()
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
        viewModel.isLoading.observe(this) {
            binding.fabFavorite.isEnabled = !it
            showLoading(it)
        }
        viewModel.errorMessage.observe(this) {
            if (it.isNotEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                binding.fabFavorite.isEnabled = false
            }
        }
        viewModel.isFavorite.observe(this) {
            binding.fabFavorite.setImageResource(
                if (it) R.drawable.baseline_favorite_24
                else R.drawable.baseline_favorite_border_24
            )
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
            binding.tvUsername.setOnClickListener { shareUserDetails(userResponse) }

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

    private fun shareUserDetails(userResponse: UserResponse) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"

        val userDetails = StringBuilder()
        userDetails.append("GitHub User Details\n\n")
        userResponse.name?.let { userDetails.append("Name: $it\n") }
        userDetails.append("Username: ${userResponse.login}\n")
        userResponse.bio?.let { userDetails.append("Bio: $it\n") }
        userDetails.append("Followers: ${userResponse.followers}\n")
        userDetails.append("Following: ${userResponse.following}\n")
        userDetails.append("Public Repos: ${userResponse.publicRepos}\n")
        userDetails.append("URL: ${userResponse.htmlUrl}")

        shareIntent.putExtra(Intent.EXTRA_TEXT, userDetails.toString())
        startActivity(Intent.createChooser(shareIntent, "Share user details"))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun saveUser(user: UserResponse) {
        val favoriteUser = FavoriteUser(user.login, user.avatarUrl)
        viewModel.setUserFavorite(favoriteUser)
    }

    companion object {
        const val TAG = "DetailsActivity"
        const val EXTRA_USERNAME = "username"

        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.tab_text_1, R.string.tab_text_2)
    }
}