package edu.cs371m.reddit.ui

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import edu.cs371m.reddit.databinding.ActivityOnePostBinding
import edu.cs371m.reddit.glide.Glide

class OnePost : AppCompatActivity() {

    private var postTitle: CharSequence? = null
    private var postSelfText: CharSequence? = null
    private var postImage: String? = null
    private var postThumbnail: String? = null
    private lateinit var onePostBinding : ActivityOnePostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onePostBinding = ActivityOnePostBinding.inflate(layoutInflater)
        setContentView(onePostBinding.root)
        setSupportActionBar(onePostBinding.postToolbar)

        // Setup intent to capture date from what MainFragment passed us
        val currentPost = intent
        postTitle = currentPost.getCharSequenceExtra("postTitle")
        postSelfText = currentPost.getCharSequenceExtra("postBody")
        postImage = currentPost.getStringExtra("postImage")
        postThumbnail = currentPost.getStringExtra("postThumbnail")

        // Calling the action bar, change title and configure back button
        val actionBar = supportActionBar
        if (actionBar != null) {
            if (postTitle!!.length > 30) {
                actionBar.title =
                    String.format("%s ...", postTitle!!.subSequence(0,30))
            } else {
                actionBar.title = postTitle
            }
            // Customize the back button
            actionBar.setHomeButtonEnabled(true)
            // showing the back button in action bar
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        // From here, bind views to activityOnePost
        onePostBinding.postTitle.text = postTitle
        onePostBinding.postBody.apply {
            text = postSelfText
            movementMethod = ScrollingMovementMethod()
        }
        Glide.glideFetch(postImage!!, postThumbnail!!, onePostBinding.postImage)
    }

    // This event will enable the back function to the button on press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}


