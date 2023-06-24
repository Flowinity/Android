package com.troplo.privateuploader.ui.gallery

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.troplo.privateuploader.R
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.Chat
import com.troplo.privateuploader.data.model.Gallery
import com.troplo.privateuploader.data.model.Upload
import com.troplo.privateuploader.databinding.FragmentGalleryBinding
import com.troplo.privateuploader.databinding.FragmentHomeBinding
import com.troplo.privateuploader.ui.home.ChatAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var galleryList: List<Upload> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView = root.findViewById(R.id.galleryRecyclerView)
        recyclerView?.adapter = GalleryAdapter(galleryList)
        progressBar = root.findViewById(R.id.progressBar)
        layoutManager = LinearLayoutManager(context)
        getUploads(requireContext())
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getUploads(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            progressBar?.visibility = View.VISIBLE
            TpuApi.retrofitService.getGallery(
                SessionManager(context).fetchAuthToken() ?: ""
            ).enqueue(object : Callback<Gallery> {
                override fun onResponse(call: Call<Gallery>, response: Response<Gallery>) {
                    if (response.isSuccessful) {
                        galleryList = response.body()?.gallery ?: ArrayList()
                        recyclerView?.adapter = GalleryAdapter(galleryList)
                        recyclerView?.layoutManager = layoutManager
                        progressBar?.visibility = View.GONE
                    } else {
                        Toast.makeText(context, "Error: " + response.code(), Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Gallery>, t: Throwable) {
                    Toast.makeText(context, "Error: " + t.message, Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}