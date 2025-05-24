package com.likhachev.e_journal.presentation.ui.teacher_journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.likhachev.e_journal.databinding.FragmentTeacherJournalBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherJournalFragment: Fragment() {

    private var _binding: FragmentTeacherJournalBinding? = null
    private val binding get() = _binding!!

    private val args: TeacherJournalFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Обработка системной кнопки "назад"
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*// Используем переданные аргументы
        val groupId = args.groupId
        val groupName = args.groupName
        val subjectId = args.subjectId
        val subjectName = args.subjectName

        // Настройка UI с полученными данными
        setupJournal(groupId, groupName, subjectId, subjectName)*/
    }

    private fun setupJournal(groupId: String, groupName: String, subjectId: Int, subjectName: String) {
        // Здесь можно настроить журнал с полученными данными
        // Например, загрузить список студентов группы, оценки и т.д.

        // Пример использования данных (можно удалить после реализации)
        // binding.someTextView.text = "Журнал группы: $groupName"
        // if (subjectName.isNotEmpty()) {
        //     binding.subjectTextView.text = "Предмет: $subjectName"
        // }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}