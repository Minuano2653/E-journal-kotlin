package com.likhachev.e_journal.presentation.ui.admin_groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.likhachev.e_journal.databinding.FragmentAdminGroupsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminGroupsFragment: Fragment() {
    private var _binding: FragmentAdminGroupsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}