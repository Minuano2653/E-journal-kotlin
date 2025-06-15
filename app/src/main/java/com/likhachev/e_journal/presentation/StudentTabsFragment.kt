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
import com.likhachev.e_journal.databinding.FragmentStudentTabsBinding
import com.likhachev.e_journal.presentation.viewmodel.StudentTabsViewModel
import com.likhachev.e_journal.utils.DateChangeListener
import com.likhachev.e_journal.utils.SessionManager
import com.likhachev.e_journal.utils.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StudentTabsFragment: Fragment(), DateChangeListener {
    private var _binding: FragmentStudentTabsBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var themeManager: ThemeManager

    private val viewModel: StudentTabsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentTabsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.nav_host_fragment_bottom) as NavHostFragment

        navController = navHostFragment.navController

        binding.bottomNavView.setupWithNavController(navController)

        observeViewModel()
        setupMoreButton()

        // Обновление заголовка при смене фрагмента
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.studentScheduleFragment -> viewModel.setTitle("Расписание на")
                R.id.studentGradesFragment -> viewModel.setTitle("Оценки")
                R.id.studentPerformanceFragment -> viewModel.setTitle("Успеваемость")
            }
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
                    showThemeSelectionDialog()
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

    private fun showThemeSelectionDialog() {
        val dialog = ThemeSelectionDialog.newInstance() {
            // Коллбек вызывается после выбора темы
            // Можно добавить дополнительную логику, если необходимо
        }
        dialog.show(parentFragmentManager, ThemeSelectionDialog.TAG)
    }

    private fun logout() {
        // Очищаем сессию
        sessionManager.clearSession()

        // Переходим на экран авторизации
        findNavController().navigate(R.id.action_studentTabsFragment_to_loginFragment)
    }

    private fun observeViewModel() {
        viewModel.title.observe(viewLifecycleOwner) { title ->
            binding.dateTextView.text = title
            binding.dateValueTextView.visibility = if (title == "Расписание на") View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDateChanged(newDate: String) {
        binding.dateValueTextView.text = newDate
    }
}