package edu.cs371m.reddit.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import edu.cs371m.reddit.MainActivity
import edu.cs371m.reddit.databinding.FragmentRvBinding

class Favorites: Fragment() {
    // XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentRvBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): Favorites {
            return Favorites()
        }
    }
    private fun setDisplayHomeAsUpEnabled(value : Boolean) {
        val mainActivity = requireActivity() as MainActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(value)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.setTitle("Favorite")
        _binding = FragmentRvBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(javaClass.simpleName, "Favorite Frag created")
        setDisplayHomeAsUpEnabled(true)
        // XXX Write me
        // Setting itemAnimator = null on your recycler view might get rid of an annoying flicker
        val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = layoutManager
        val adapter = PostRowAdapter(viewModel)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator = null
        initSwipeLayout(binding.swipeRefreshLayout)
        viewModel.observeFavorites().observe(viewLifecycleOwner) {
            adapter.submitList(viewModel.observeFavorites().value)
            adapter.notifyDataSetChanged()
        }

        // Add to menu
        val menuHost: MenuHost = requireActivity()
        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Menu is already inflated by main activity
            }
            // XXX Write me, onMenuItemSelected
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle action bar item clicks here.
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        (requireActivity() as MainActivity).hideKeyboard()
                        parentFragmentManager.popBackStack()
                        val favoriteFrag = parentFragmentManager.findFragmentByTag("favoritesFragTag")
                        parentFragmentManager.beginTransaction()
                            .remove(favoriteFrag!!)
                            .commit()
                        false
                    }// Handle in fragment
                    else -> true
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
        }
    }
    override fun onDestroyView() {
        // XXX Write me
        // Don't let back to home button stay when we exit favorites
        setDisplayHomeAsUpEnabled(false)
        super.onDestroyView()
    }
}