package com.example.fotografpaylasimuygulamasi.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fotografpaylasimuygulamasi.R
import com.example.fotografpaylasimuygulamasi.adapter.PostAdapter
import com.example.fotografpaylasimuygulamasi.databinding.FragmentFeedBinding
import com.example.fotografpaylasimuygulamasi.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class FeedFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var popup: PopupMenu
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var adapter: PostAdapter? = null
    private val postList: ArrayList<Post> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener { floatingButtonTiklandi(it) }

        popup = PopupMenu(requireContext(), binding.floatingActionButton)
        popup.menuInflater.inflate(R.menu.my_popup_menu, popup.menu)
        popup.setOnMenuItemClickListener(this)

        fireStoreVerileriAl()

        adapter = PostAdapter(postList)
        binding.feedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.feedRecyclerView.adapter = adapter
    }

    private fun fireStoreVerileriAl() {
        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                value?.let {
                    if (!it.isEmpty) {
                        postList.clear()
                        for (document in it.documents) {
                            val comment = document.getString("comment") ?: ""
                            val email = document.getString("email") ?: ""
                            val downloadUrl = document.getString("downloadUrl") ?: ""
                            val kullaniciAdi = document.getString("kullaniciAdi") ?: ""
                            postList.add(Post(email, comment, downloadUrl, kullaniciAdi))
                        }
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
    }

    fun floatingButtonTiklandi(view: View) {
        popup.show()
    }



    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.yuklemeItem -> {

                val navOptions = androidx.navigation.NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_left)
                    .setPopExitAnim(R.anim.slide_out_right)
                    .build()
                val action = FeedFragmentDirections.actionFeedFragment2ToYuklemeFragment2()
                findNavController().navigate(action, navOptions)
                true
            }
            R.id.cikisItem -> {
                auth.signOut()

                // Gecikmeli navigasyon, crash'i önlemek için
                Handler(Looper.getMainLooper()).postDelayed({
                    // Back stack'i temizleyerek giriş ekranına yönlendir
                    val navOptions = androidx.navigation.NavOptions.Builder()
                        .setEnterAnim(R.anim.slide_in_right)
                        .setExitAnim(R.anim.slide_out_left)
                        .setPopEnterAnim(R.anim.slide_in_left)
                        .setPopExitAnim(R.anim.slide_out_right)
                        .setPopUpTo(R.id.feedFragment2, true)  // Back stack'i temizler
                        .build()

                    val action = FeedFragmentDirections.actionFeedFragment2ToKullaniciFragment2()
                    findNavController().navigate(action, navOptions)
                }, 500) // 200 ms gecikme

                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
