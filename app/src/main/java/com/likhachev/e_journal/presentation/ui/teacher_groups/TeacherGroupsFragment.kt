package com.likhachev.e_journal.presentation.ui.teacher_groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.likhachev.e_journal.data.model.TeacherGroupDto
import com.likhachev.e_journal.databinding.FragmentTeacherGroupsBinding
import com.likhachev.e_journal.domain.model.TeacherGroup
import com.likhachev.e_journal.presentation.viewmodel.TeacherGroupsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TeacherGroupsFragment : Fragment() {

    private var _binding: FragmentTeacherGroupsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TeacherGroupsViewModel by viewModels()

    private lateinit var groupsAdapter: TeacherGroupsAdapter
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupSearchView()
        setupButtons()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        groupsAdapter = TeacherGroupsAdapter { group ->
            navigateToJournal(group)
        }

        historyAdapter = HistoryAdapter { query ->
            viewModel.onHistoryItemClicked(query)
            binding.searchView.setQuery(query, false) // false чтобы не вызывать onQueryTextSubmit
            viewModel.onSearchSubmitted(query) // Сохраняем в историю при клике на элемент истории
            binding.searchView.clearFocus()
        }

        binding.groupsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupsAdapter
        }

        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Сохраняем в историю при подтверждении поиска (Enter или кнопка поиска)
                query?.let { viewModel.onSearchSubmitted(it) }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Только изменяем поисковый запрос, не сохраняем в историю
                viewModel.onSearchQueryChanged(newText ?: "")
                return true
            }
        })

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            viewModel.onSearchFocusChanged(hasFocus)
        }
    }

    private fun setupButtons() {
        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        binding.updateButton.setOnClickListener {
            viewModel.loadGroups()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.groupsState.collect { state ->
                    when (state) {
                        is TeacherGroupsUiState.Loading -> {
                            showLoading()
                        }
                        is TeacherGroupsUiState.Success -> {
                            showSuccess(state.groups)
                        }
                        is TeacherGroupsUiState.Error -> {
                            showError()
                        }
                        is TeacherGroupsUiState.Idle -> {
                            hideAllStates()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchHistory.collect { history ->
                    historyAdapter.submitList(history)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isSearchFocused.collect { isFocused ->
                    binding.historyLayout.visibility = if (isFocused && historyAdapter.itemCount > 0) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorEvent.collect { event ->
                    event.getContentIfNotHandled()?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun navigateToJournal(group: TeacherGroupDto) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val teacherGroup = TeacherGroup(
            groupId = group.groupId,
            subjectId = group.subjectId,
            groupName = group.groupName,
            date = currentDate
        )

        val action = TeacherGroupsFragmentDirections
            .actionTeacherGroupsFragmentToTeacherJournalFragment(teacherGroup)

        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.groupsRecyclerView.visibility = View.GONE
        binding.notFoundLayout.visibility = View.GONE
    }

    private fun showSuccess(groups: List<TeacherGroupDto>) {
        binding.progressBar.visibility = View.GONE

        if (groups.isEmpty()) {
            // Показываем "не найдено" если список пустой (может быть из-за поиска или отсутствия данных)
            binding.groupsRecyclerView.visibility = View.GONE
            binding.notFoundLayout.visibility = View.VISIBLE
        } else {
            // Показываем список групп
            binding.groupsRecyclerView.visibility = View.VISIBLE
            binding.notFoundLayout.visibility = View.GONE
            groupsAdapter.submitList(groups)
        }
    }

    private fun showError() {
        binding.progressBar.visibility = View.GONE
        binding.groupsRecyclerView.visibility = View.GONE
        binding.notFoundLayout.visibility = View.VISIBLE
    }

    private fun hideAllStates() {
        binding.progressBar.visibility = View.GONE
        binding.groupsRecyclerView.visibility = View.GONE
        binding.notFoundLayout.visibility = View.GONE
    }
}