package edu.cs371m.reddit.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import edu.cs371m.reddit.MainActivity
import edu.cs371m.reddit.databinding.FragmentRvBinding

// XXX Write most of this file
class HomeFragment: Fragment() {
    // XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentRvBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    // Set up the adapter
    private fun initAdapter(binding: FragmentRvBinding) : PostRowAdapter {
        val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = layoutManager
        val adapter = PostRowAdapter(viewModel)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator = null
        return adapter
        //adapter.submitList(viewModel.observeSearchPosts().value)
        //adapter.notifyDataSetChanged()
    }

    private fun notifyWhenFragmentForegrounded(postRowAdapter: PostRowAdapter) {
        // When we return to our fragment, notifyDataSetChanged
        // to pick up modifications to the favorites list.  Maybe do more.
        postRowAdapter.notifyDataSetChanged()
    }

    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
            viewModel.refreshPosts()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRvBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "Main Frag created")
        // XXX Write me
        val adapter = initAdapter(_binding!!)
        initSwipeLayout(binding.swipeRefreshLayout)
        viewModel.refreshPosts()
        viewModel.refreshSubReddits()

        viewModel.observeSearchPosts().observe(viewLifecycleOwner) {
            val newFetchPost = viewModel.observeSearchPosts().value
            adapter.submitList(newFetchPost)
            adapter.notifyDataSetChanged()
        }

        // This part of the code works on mainFragment when we comes back "home"
        // a.k.a backStackEntryCount == 0
        (requireActivity() as MainActivity)
            .supportFragmentManager.addOnBackStackChangedListener {
                if ((requireActivity() as MainActivity)
                        .supportFragmentManager.backStackEntryCount == 0) {
                    viewModel.setTitleToSubreddit()
                    notifyWhenFragmentForegrounded(adapter)
                    (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
        }
    }

}