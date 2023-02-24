package edu.cs371m.reddit.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.cs371m.reddit.R
import edu.cs371m.reddit.api.RedditPost
import edu.cs371m.reddit.databinding.RowPostBinding
import edu.cs371m.reddit.glide.Glide

/**
 * Created by witchel on 8/25/2019
 */

// https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
// Slick adapter that provides submitList, so you don't worry about how to update
// the list, you just submit a new one when you want to change the list and the
// Diff class computes the smallest set of changes that need to happen.
// NB: Both the old and new lists must both be in memory at the same time.
// So you can copy the old list, change it into a new list, then submit the new list.
//
// You can call adapterPosition to get the index of the selected item
class PostRowAdapter(private val viewModel: MainViewModel)
    : ListAdapter<RedditPost, PostRowAdapter.VH>(RedditDiff()) {

    inner class VH(val rowPostBinding: RowPostBinding)
        : RecyclerView.ViewHolder(rowPostBinding.root) {
        init {
            rowPostBinding.title.setOnClickListener {
                // Calling One Post Activity
                MainViewModel.doOnePost(rowPostBinding.root.context, getItem(adapterPosition))
            }
            rowPostBinding.imageTextUnion.setOnClickListener {
                // Calling One Post Activity
                MainViewModel.doOnePost(rowPostBinding.root.context, getItem(adapterPosition))
            }
            rowPostBinding.rowFav.setOnClickListener {
                Log.d("XXX", "Row Fav button clicked \n")
                viewModel.setHeartClicked()
                val local = getItem(adapterPosition)
                local.let {
                    if (viewModel.isFavorite(it)) {
                        viewModel.removeFavorite(it)
                    } else {
                        viewModel.addFavorite(it)
                    }
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RowPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        //XXX Write me.
        // Handle views in row binding
        val binding = holder.rowPostBinding
        val post = getItem(position)

        if (post.selfText.toString() != "") {
            // when self text is present, make imageview invisible
            binding.image.visibility = INVISIBLE
        } else {
            binding.image.visibility = VISIBLE
            Glide.glideFetch(post.imageURL,post.thumbnailURL,binding.image)
        }
        binding.selfText.text = post.selfText
        binding.title.text = post.title
        binding.comments.text = post.commentCount.toString()
        binding.score.text = post.score.toString()
        // Handle default rowFav icon
        if (viewModel.isFavorite(post)) {
            binding.rowFav.setImageResource(R.drawable.ic_favorite_black_24dp)
        } else {
            binding.rowFav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
        }
    }

    class RedditDiff : DiffUtil.ItemCallback<RedditPost>() {
        override fun areItemsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean {
            return oldItem.key == newItem.key
        }
        override fun areContentsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean {
            return RedditPost.spannableStringsEqual(oldItem.title, newItem.title) &&
                    RedditPost.spannableStringsEqual(oldItem.selfText, newItem.selfText) &&
                    RedditPost.spannableStringsEqual(oldItem.publicDescription, newItem.publicDescription) &&
                    RedditPost.spannableStringsEqual(oldItem.displayName, newItem.displayName)
        }
    }
}

