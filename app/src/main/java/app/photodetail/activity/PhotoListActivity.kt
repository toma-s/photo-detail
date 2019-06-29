package app.photodetail.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.photodetail.content.PhotoContent
import kotlinx.android.synthetic.main.activity_photo_list.*
import kotlinx.android.synthetic.main.photo_list_content.view.*
import kotlinx.android.synthetic.main.photo_list.*
import android.Manifest
import android.annotation.TargetApi
import android.app.ProgressDialog
import android.os.AsyncTask;
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.widget.ImageView
import app.photodetail.R
import app.photodetail.fragment.PhotoDetailFragment
import app.photodetail.model.Photo
import java.lang.Exception


/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices.
 * On handsets, the activity presents a list of items, which when touched,
 * lead to a [PhotoDetailActivity] representing item details.
 * On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class PhotoListActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    private val readExtStoragePermissionCode = 0
    private var recyclerView: RecyclerView? = null
    private var adapter: SimpleItemRecyclerViewAdapter? = null
    private var pd: ProgressDialog? = null
    private val photosPerLoad: Int = 10

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener {
            Log.d("mylog", "Click")
            loadPhotosAsync()
        }

        if (photo_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        setupRecyclerView()
        setupPermissions()
        if (PhotoContent.photos.size == 0) {
            loadPhotosAsync()
        }
    }

    private fun loadPhotosAsync() {
        AsyncLoadPhotos().execute()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupRecyclerView() {
        recyclerView = photo_list
        adapter = SimpleItemRecyclerViewAdapter(this,
            PhotoContent.photos,
            twoPane)

        (recyclerView as RecyclerView).adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupPermissions() {
        Log.d("mylog", "Permissions: askPermission started")
        if (applicationContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("mylog", "Permissions: shows an explanation for why permission is needed")
            if (/*false && */ shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.d("mylog", "Permissions: explain")
                val builder = AlertDialog.Builder(this@PhotoListActivity)
                builder.setPositiveButton(
                    android.R.string.ok
                ) { _, _ ->
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        readExtStoragePermissionCode
                    )
                }
                builder.create().show()
            } else {
                Log.d("mylog", "Permissions: request")
                ActivityCompat.requestPermissions(
                    this@PhotoListActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    readExtStoragePermissionCode
                )
            }
        } else {
            Log.d("mylog", "Permissions: READ_EXTERNAL_STORAGE granted")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            readExtStoragePermissionCode -> {
                for (i in grantResults.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Permissions", "GRANTED")
                    } else {  // denied
                        Log.d("Permissions", "DENIED")
                    }
                }
                return
            }
        }
    }

    @Suppress("DEPRECATION")
    inner class AsyncLoadPhotos: AsyncTask<Void, Int, String>() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun doInBackground(vararg count: Void?): String {
            var i = 0
            while (i <= photosPerLoad) {
                try {
                    PhotoContent.loadPhotos()
                    i++
                    publishProgress(i)
                }
                catch (e: Exception) {
                    return(e.localizedMessage)
                }
            }
            return "Loaded"
        }

        override fun onPreExecute() {
            super.onPreExecute()
            pd = ProgressDialog(this@PhotoListActivity)
            pd!!.setMessage("Loading...")
            pd!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            pd!!.max = photosPerLoad
            pd!!.show()
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            pd!!.incrementProgressBy(values[0]!!)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            adapter!!.notifyDataSetChanged()
//            publishProgress(pd!!.max)
            pd!!.dismiss()
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: PhotoListActivity,
        private val values: List<Photo>,
        private val twoPane: Boolean
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as Photo
                item.makeDescription()

                if (twoPane) {
                    val fragment = PhotoDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(PhotoDetailFragment.ARG_ITEM_ID, item.id.toString())
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.photo_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, PhotoDetailActivity::class.java).apply {
                        putExtra(PhotoDetailFragment.ARG_ITEM_ID, item.id.toString())
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.photo_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Log.d("mylog", "Adapter")
            val item = values[position]
            holder.idView.text = item.id.toString()
            holder.contentView.text = item.content
            try {
                holder.imageView.setImageBitmap(item.bitmap)
            } catch (e: Exception) {
                Log.d("mylog", "holder exception: $e")
            }

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.plc_id
            val contentView: TextView = view.plc_content
            val imageView: ImageView = view.plc_image
        }
    }


}
