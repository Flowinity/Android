package com.troplo.privateuploader.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.troplo.privateuploader.R
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.Chat
import com.troplo.privateuploader.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

  private var _binding: FragmentHomeBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!
  private var recyclerView: RecyclerView? = null
  private var progressBar: ProgressBar? = null
  private var layoutManager: RecyclerView.LayoutManager? = null
  private var chatList: List<Chat> = ArrayList()

  override fun onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
  ): View {
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root
    recyclerView = root.findViewById(R.id.chatRecyclerView)
    recyclerView?.adapter = ChatAdapter(chatList)
    progressBar = root.findViewById(R.id.progressBar)
    layoutManager = LinearLayoutManager(context)
    getChats()
    return root
  }

  override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
  }

  private fun getChats() {
    CoroutineScope(Dispatchers.IO).launch {
      progressBar?.visibility = View.VISIBLE
      TpuApi.retrofitService.getChats(
        SessionManager(requireContext()).fetchAuthToken() ?: ""
      ).enqueue(object : Callback<List<Chat>> {
        override fun onResponse(call: Call<List<Chat>>, response: Response<List<Chat>>) {
          if (response.isSuccessful) {
            chatList = response.body()!!
            recyclerView?.adapter = ChatAdapter(chatList)
            recyclerView?.layoutManager = layoutManager
            progressBar?.visibility = View.GONE
          } else {
            Toast.makeText(context, "Error: " + response.code(), Toast.LENGTH_LONG).show()
          }
        }

        override fun onFailure(call: Call<List<Chat>>, t: Throwable) {
          Toast.makeText(context, "Error: " + t.message, Toast.LENGTH_LONG).show()
        }
      })
    }
  }
}