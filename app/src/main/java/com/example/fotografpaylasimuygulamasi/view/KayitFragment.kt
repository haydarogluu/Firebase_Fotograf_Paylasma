package com.example.fotografpaylasimuygulamasi.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.fotografpaylasimuygulamasi.R

import com.example.fotografpaylasimuygulamasi.databinding.FragmentKayitBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


open class KayitFragment : Fragment() {
    private var _binding: FragmentKayitBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKayitBinding.inflate(inflater, container, false)
        val view = binding.root
        return view




    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener{kayitTiklandi(it) }
    }

    fun kayitTiklandi(view: View?) {



        val email = binding.editTextKayitMail.text.toString()
        val password = binding.editTextKayitPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    //Kullanıcı oluşturuldu
                    if (task.isSuccessful){
                        sendVerificationEmail()

                        val navOptions = androidx.navigation.NavOptions.Builder()
                            .setEnterAnim(R.anim.slide_in_right)
                            .setExitAnim(R.anim.slide_out_left)
                            .setPopEnterAnim(R.anim.slide_in_left)
                            .setPopExitAnim(R.anim.slide_out_right)
                            .build()

                        val action = KayitFragmentDirections.actionKayitFragmentToKullaniciFragment22()
                        if (view != null) {
                            Navigation.findNavController(view).navigate(action, navOptions)
                        }
                    }
                    else{
                        Toast.makeText(requireContext(), task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                    }

                }

            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun sendVerificationEmail() {
        val user = auth.currentUser
        if (user != null) {
            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(),"Doğrulama maili gönderildi", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(),"Doğrulama maili gönderilmedi", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}