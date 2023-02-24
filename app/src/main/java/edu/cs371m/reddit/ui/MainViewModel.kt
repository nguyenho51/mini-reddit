package edu.cs371m.reddit.ui


import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import edu.cs371m.reddit.api.RedditApi
import edu.cs371m.reddit.api.RedditPost
import edu.cs371m.reddit.api.RedditPostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// XXX Much to write
class MainViewModel : ViewModel() {
    private var title = MutableLiveData<String>()
    private var searchTerm = MutableLiveData<String>()
    fun setSearchTerm(term: String) {
        searchTerm.value = term
    }
    private var subreddit = MutableLiveData<String>().apply {
        value = "aww"
    }

    // Initiate network variables
    private val api = RedditApi.create()
    private val repository = RedditPostRepository(api)
    private var netPosts: MutableLiveData<List<RedditPost>> = MutableLiveData()
    private var netSubreddits: MutableLiveData<List<RedditPost>> = MutableLiveData()

    fun refreshPosts() {
        // This is where the network request for posts is initiated.
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO){
            netPosts.postValue(repository.getPosts(subreddit.value!!))
        }
    }
    fun refreshSubReddits() {
        // This is where the network request for subReddits is initiated.
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO){
            netSubreddits.postValue(repository.getSubreddits())
        }
    }

    // XXX Write netPosts/searchPosts
    /* HANDLE SEARCH FUNCTIONALITY */
    private var heartClicked = MutableLiveData(false)
    fun setHeartClicked() {
        heartClicked.postValue(true)
    }
    private var searchPosts = MediatorLiveData<List<RedditPost>>().apply {
        // Always go back to the entire netPosts list
        addSource(netPosts) {value = filterPost()}
        addSource(searchTerm) {value = filterPost()}
    }
    private var searchSubreddits = MediatorLiveData<List<RedditPost>>().apply {
        // Always go back to the entire netSubReddits list
        addSource(netSubreddits) {value = filterSubreddits()}
        addSource(searchTerm)  {value = filterSubreddits()}
    }
    private var searchFavorites = MediatorLiveData<List<RedditPost>>().apply {
        // Always go back to the entire netSubReddits list
        addSource(searchTerm)  {value = filterFavorites()}
        addSource(heartClicked) {value = filterFavorites()}
    }

    private fun filterPost(): List<RedditPost> {
        //Log.d(javaClass.simpleName, "Filter $searchTerm is in ${title.value}")
        // When the app is first started, searchTerm value is null and prevent searchPosts to fetch
        val searchTermValue: String =
            if (searchTerm.value == null) {""} else {searchTerm.value!!}
        return netPosts.value!!.filter {
            var titleFound = false
            var selfTextFound = false
            titleFound = titleFound || it.searchFor(searchTermValue)
            selfTextFound = selfTextFound || it.searchFor(searchTermValue)
            titleFound || selfTextFound
        }
    }

    private fun filterSubreddits(): List<RedditPost> {
        //Log.d(javaClass.simpleName, "Filter $searchTerm is in ${title.value}")
        // When the app is first started, searchTerm value is null and prevent searchSubreddits to fetch
        val searchTermValue: String =
            if (searchTerm.value == null) {""} else {searchTerm.value!!}
        return netSubreddits.value!!.filter {
            var displayNameFound = false
            var descriptionFound = false
            displayNameFound = displayNameFound || it.searchFor(searchTermValue)
            descriptionFound = descriptionFound || it.searchFor((searchTermValue))
            displayNameFound || descriptionFound
        }
    }

    private fun filterFavorites(): List<RedditPost> {
        //Log.d(javaClass.simpleName, "Filter $searchTerm is in ${title.value}")
        // When the app is first started, searchTerm value is null and prevent searchPosts to fetch
        val searchTermValue: String =
            if (searchTerm.value == null) {""} else {searchTerm.value!!}
        return favorites.filter {
            var titleFound = false
            var selfTextFound = false
            titleFound = titleFound || it.searchFor(searchTermValue)
            selfTextFound = selfTextFound || it.searchFor(searchTermValue)
            titleFound || selfTextFound
        }
    }

    fun observeSearchPosts(): MutableLiveData<List<RedditPost>> {
        // Observe current state of the search list of posts
        // Default search list contains all posts
        return searchPosts
    }
    fun observeSearchSubReddits(): MutableLiveData<List<RedditPost>> {
        // Observe current state of the search list of subreddits
        // Default search list contains all posts
        return searchSubreddits
    }
    fun observeFavorites(): MutableLiveData<List<RedditPost>> {
        // Observe current state of the search list of favorites
        // Default search list contains all posts
        return searchFavorites
    }

    // Looks pointless, but if LiveData is set up properly, it will fetch posts
    // from the network
    fun repoFetch() {
        val fetch = subreddit.value
        subreddit.value = fetch
    }

    /* THIS PART HANDLES ANYTHING RELATED TO THE TITLE
    // SET, OBSERVE, CHANGE TITLE DEPENDING ON CORRESPONDING FRAGMENT
     */
    fun observeTitle(): LiveData<String> {
        return title
    }
    fun setTitle(newTitle: String) {
        title.value = newTitle
    }
    fun setSubReddit(name: String) {
        subreddit.value = name
    }
    // The parsimonious among you will find that you can call this in exactly two places
    fun setTitleToSubreddit() {
        title.value = "r/${subreddit.value}"
    }

    // XXX Write me, set, observe, deal with favorites
    /* THIS PART HANDLES ANYTHING RELATED TO FAVORITE LIST
     */
    private var favorites = mutableListOf<RedditPost>()

    fun isFavorite(post: RedditPost): Boolean {
        for (favPost in favorites) {
            if (favPost == post) { return true }
        }
        return false
    }
    fun addFavorite(post: RedditPost) {
        favorites.add(post)
    }
    fun removeFavorite(post: RedditPost) {
        favorites.remove(post)
    }

    /* THIS PART HANDLES ONE POST ACTIVITY
    // UTILIZE COMPANION OBJECT THAT COMES WITH THE VIEW MODEL */
    // Convenient place to put it as it is shared
    companion object {
        fun doOnePost(context: Context, redditPost: RedditPost) {
            val onePostIntent = Intent(context, OnePost::class.java).apply {
                putExtra("postTitle", redditPost.title)
                putExtra("postBody", redditPost.selfText)
                putExtra("postImage", redditPost.imageURL)
                putExtra("postThumbnail", redditPost.thumbnailURL)
            }
            context.startActivity(onePostIntent)
        }
    }

}