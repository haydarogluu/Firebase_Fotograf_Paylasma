package com.example.fotografpaylasimuygulamasi.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.fotografpaylasimuygulamasi.R
import com.example.fotografpaylasimuygulamasi.databinding.FragmentKullaniciBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class KullaniciFragment : Fragment() {
    private var _binding: FragmentKullaniciBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        sharedPreferences = requireActivity().getSharedPreferences("com.example.fotografpaylasimuygulamasi", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKullaniciBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "Beni Hatırla" durumu kontrol ediliyor
        checkAutoLogin(view)

        binding.btnKayit.setOnClickListener { kayitTiklandi(it) }
        binding.btnGiris.setOnClickListener { girisTiklandi(it) }

        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sharedPreferences.edit().putBoolean("rememberMe", true).apply()
            } else {
                sharedPreferences.edit().putBoolean("rememberMe", false).apply()
            }
        }
    }

    // "Beni Hatırla" durumu kontrol ediliyor
    private fun checkAutoLogin(view: View) {

        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)

        if (rememberMe) {
            val user = auth.currentUser
            if (user != null && user.isEmailVerified) {
                // Kullanıcı doğrulanmış, feed ekranına yönlendir

                val navOptions = androidx.navigation.NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_left)
                    .setPopExitAnim(R.anim.slide_out_right)
                    .build()

                val action = KullaniciFragmentDirections.actionKullaniciFragment2ToFeedFragment2()
                Navigation.findNavController(view).navigate(action, navOptions)
            }
        }
    }

    // Giriş işlemi
    fun girisTiklandi(view: View) {
        val email = binding.emailTextGiris.text.toString()
        val password = binding.passwordTextGiris.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // Kullanıcıyı feed ekranına yönlendir

                        val navOptions = androidx.navigation.NavOptions.Builder()
                            .setEnterAnim(R.anim.slide_in_right)
                            .setExitAnim(R.anim.slide_out_left)
                            .setPopEnterAnim(R.anim.slide_in_left)
                            .setPopExitAnim(R.anim.slide_out_right)
                            .build()

                        val action = KullaniciFragmentDirections.actionKullaniciFragment2ToFeedFragment2()
                        Navigation.findNavController(view).navigate(action, navOptions)
                    } else {
                        Toast.makeText(requireContext(), "E-posta doğrulanmamış. Lütfen e-postanızı doğrulayın.", Toast.LENGTH_LONG).show()
                        auth.signOut()
                    }
                } else {
                    Toast.makeText(requireContext(), task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show()
        }
    }

    // Kayıt işlemi
    fun kayitTiklandi(view: View) {

        val navOptions = androidx.navigation.NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()

        val action = KullaniciFragmentDirections.actionKullaniciFragment2ToKayitFragment()
        Navigation.findNavController(view).navigate(action, navOptions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
