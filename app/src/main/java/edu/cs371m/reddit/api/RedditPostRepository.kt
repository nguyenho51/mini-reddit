package edu.cs371m.reddit.api

import android.text.SpannableString
import com.google.gson.GsonBuilder
import edu.cs371m.reddit.MainActivity

class RedditPostRepository(private val redditApi: RedditApi) {
    // NB: This is for our testing.
    val gson = GsonBuilder().registerTypeAdapter(
            SpannableString::class.java, RedditApi.SpannableDeserializer()
        ).create()

    private fun unpackPosts(response: RedditApi.ListingResponse): List<RedditPost> {
        // XXX Write me.
        val redditPosts: MutableList<RedditPost> = mutableListOf()
        for (children in response.data.children) {
            redditPosts.add(children.data)
        }
        return redditPosts
    }

    suspend fun getPosts(subreddit: String): List<RedditPost> {
        if (MainActivity.globalDebug) {
            val response = gson.fromJson(
                MainActivity.jsonAww100,
                RedditApi.ListingResponse::class.java)
            return unpackPosts(response)
        } else {
            // XXX Write me.
            return unpackPosts(redditApi.getPosts(subreddit))
        }
    }

    suspend fun getSubreddits(): List<RedditPost> {
        if (MainActivity.globalDebug) {
            val response = gson.fromJson(
                MainActivity.subreddit1,
                RedditApi.ListingResponse::class.java)
            return unpackPosts(response)
        } else {
            // XXX Write me.
            return unpackPosts(redditApi.getSubReddits())
        }
    }
}
