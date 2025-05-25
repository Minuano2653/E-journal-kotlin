package com.likhachev.e_journal.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.likhachev.e_journal.R
import com.likhachev.e_journal.databinding.FragmentAdminTabsBinding
import com.likhachev.e_journal.presentation.viewmodel.AdminTabsViewModel
import com.likhachev.e_journal.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminTabsFragment: Fragment() {
    private var _binding: FragmentAdminTabsBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    @Inject lateinit var sessionManager: SessionManager

    private val viewModel: AdminTabsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminTabsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.nav_host_admin_fragment_bottom) as NavHostFragment

        navController = navHostFragment.navController

        binding.bottomNavView.setupWithNavController(navController)

        observeViewModel()
        setupMoreButton()

        // Обновление заголовка при смене фрагмента
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.adminGroupsFragment -> viewModel.setTitle("Классы")
                R.id.adminTeachersFragment -> viewModel.setTitle("Учителя")
                R.id.adminStudentsFragment -> viewModel.setTitle("Учащиеся")
            }
        }
    }

    private fun observeViewModel() {
        viewModel.title.observe(viewLifecycleOwner) { title ->
            binding.firstTextView.text = title
        }
    }

    private fun setupMoreButton() {
        binding.moreButton.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_more, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_change_theme -> {
                    // TODO: Реализовать смену темы
                    // Например, можно открыть диалог выбора темы или переключить между светлой/темной
                    true
                }
                R.id.menu_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun logout() {
        sessionManager.clearSession()
        findNavController().navigate(R.id.action_adminTabsFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}