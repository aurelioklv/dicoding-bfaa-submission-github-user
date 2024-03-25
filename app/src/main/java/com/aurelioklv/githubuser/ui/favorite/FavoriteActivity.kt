package com.aurelioklv.githubuser.ui.favorite

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aurelioklv.githubuser.data.local.FavoriteUser
import com.aurelioklv.githubuser.databinding.ActivityFavoriteBinding
import com.aurelioklv.githubuser.util.ViewModelFactory

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private val viewModel: FavoriteViewModel by viewModels<FavoriteViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Favorite Users"

        setupRecyclerView()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.favUsers.observe(this) { setFavoriteUsers(it) }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvFavUsers.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvFavUsers.addItemDecoration(itemDecoration)
    }

    private fun setFavoriteUsers(users: List<FavoriteUser>) {
        val adapter = FavoriteAdapter()
        adapter.submitList(users)
        binding.rvFavUsers.adapter = adapter
    }
}