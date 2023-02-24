package edu.cs371m.reddit.ui.subreddits

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import edu.cs371m.reddit.databinding.FragmentRvBinding
import edu.cs371m.reddit.ui.MainViewModel

class Subreddits : Fragment() {
    // XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentRvBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): Subreddits {
            return Subreddits()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.setTitle("Pick")
        _binding = FragmentRvBinding.inflate(inflater, container, false)
        return binding.root
    }

    // XXX Write me, onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "Subreddits Frag created")
        val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = layoutManager
        val adapter = SubredditListAdapter(viewModel, requireActivity())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator = null
        initSwipeLayout(binding.swipeRefreshLayout)
        viewModel.observeSearchSubReddits().observe(viewLifecycleOwner) {
            val newSubreddits = viewModel.observeSearchSubReddits().value
            adapter.submitList(newSubreddits)
            adapter.notifyDataSetChanged()
        }
    }

    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
            viewModel.refreshSubReddits()
        }
    }
}