package com.example.test_crypt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.test_crypt.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "Enabled" else "Disabled"
            Toast.makeText(context, "Notifications $status", Toast.LENGTH_SHORT).show()
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "On" else "Off"
            Toast.makeText(context, "Dark Mode $status", Toast.LENGTH_SHORT).show()
        }

        binding.tvChangePassword.setOnClickListener {
            Toast.makeText(context, "Change Password clicked", Toast.LENGTH_SHORT).show()
        }

        binding.tvLanguage.setOnClickListener {
            Toast.makeText(context, "Language clicked", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
