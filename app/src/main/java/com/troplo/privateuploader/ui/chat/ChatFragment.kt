package com.troplo.privateuploader.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.troplo.privateuploader.databinding.FragmentCollectionsBinding
import com.troplo.privateuploader.ui.collections.ChatViewModel

class ChatFragment : Fragment() {

    private var _binding: FragmentCollectionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this)[ChatViewModel::class.java]

        _binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textCollections
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}