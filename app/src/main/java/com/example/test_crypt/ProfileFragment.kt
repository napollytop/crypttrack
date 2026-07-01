package com.example.test_crypt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.test_crypt.databinding.FragmentProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        fetchProfile()

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.SettingsFragment)
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            findNavController().navigate(R.id.LoginFragment)
            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchProfile() {
        val token = "Bearer ${sessionManager.getToken()}"
        RetrofitClient.backendInstance.getProfile(token).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    binding.tvName.text = profile?.name ?: "Unknown"
                    binding.tvEmail.text = profile?.email ?: "No Email"
                } else {
                    Toast.makeText(context, "Gagal mengambil data profil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(context, "Kesalahan Jaringan", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
