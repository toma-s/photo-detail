package app.photodetail.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.photodetail.R
import app.photodetail.model.Photo
import app.photodetail.content.PhotoContent
import kotlinx.android.synthetic.main.activity_photo_detail.*
import kotlinx.android.synthetic.main.photo_detail.view.*

/**
 * A fragment representing a single Photo detail screen.
 * This fragment is either contained in a [PhotoListActivity]
 * in two-pane mode (on tablets) or a [PhotoDetailActivity]
 * on handsets.
 */
class PhotoDetailFragment : Fragment() {

    private var item: Photo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                val pom = it.getInt(ARG_ITEM_ID)
                item = PhotoContent.photoToId[it.getString(ARG_ITEM_ID)]
                activity?.toolbar_layout?.title = item?.content
                Log.d("mylog", "details: " + item?.detail.toString())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.photo_detail, container, false)

        item?.let {
            rootView.photo_detail.text = it.detail
        }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
