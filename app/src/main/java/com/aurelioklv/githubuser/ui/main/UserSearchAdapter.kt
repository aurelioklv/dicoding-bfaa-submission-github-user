package com.aurelioklv.githubuser.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aurelioklv.githubuser.data.remote.response.UserSearchItem
import com.aurelioklv.githubuser.databinding.UserSearchItemBinding
import com.aurelioklv.githubuser.ui.details.DetailsActivity
import com.bumptech.glide.Glide

class UserSearchAdapter :
    ListAdapter<UserSearchItem, UserSearchAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: UserSearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserSearchItem) {
            Glide.with(itemView.context)
                .load(item.avatarUrl)
                .into(binding.ivUserAvatar)
            binding.tvUsername.text = item.login
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            UserSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val userSearchItem = getItem(position)
        holder.bind(userSearchItem)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailsActivity::class.java)
            intent.putExtra(DetailsActivity.EXTRA_USERNAME, userSearchItem.login)
            holder.itemView.context.startActivity(intent)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserSearchItem>() {
            override fun areItemsTheSame(
                oldItem: UserSearchItem,
                newItem: UserSearchItem,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: UserSearchItem,
                newItem: UserSearchItem,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}