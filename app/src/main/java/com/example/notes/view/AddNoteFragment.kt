package com.example.notes.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notes.NotesViewModel
import com.example.notes.NotesViewModelFactory
import com.example.notes.databinding.FragmentAddItemBinding
import com.example.notes.room.NoteEntity
import com.example.notes.room.NotesApplication
import java.util.*

class AddNoteFragment : Fragment() {

    private val navigationArgs: AddNoteFragmentArgs by navArgs()

    lateinit var noteEntity: NoteEntity

    // Привязка экземпляра объекта, соответствующего макету fragment_add_item.xml
    // Это свойство не равно нулю между обратными вызовами жизненного цикла onCreateView () и onDestroyView (),
    // когда к фрагменту прикреплена иерархия представления
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    //Внутри лямбда вызовите NotesViewModelFactory()конструктор и передайте noteDao экземпляр.
    //Используйте database экземпляр, который вы создали в одной из предыдущих задач, для вызова noteDao конструктора.
    private val viewModel: NotesViewModel by activityViewModels {
        NotesViewModelFactory(
            (activity?.application as NotesApplication).database
                .noteDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddItemBinding.inflate(inflater, container, false)

        return binding.root
    }

    //Возвращает true, если EditTexts не пустые
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.noteTitle.text.toString(),
            binding.noteDescription.text.toString()
        )
    }

    //Вставляет новый элемент в базу данных и переходит к фрагменту списка.
    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                binding.noteTitle.text.toString(),
                binding.noteDescription.text.toString(),
                dataOfCreation = Calendar.getInstance().timeInMillis
            )
            val action = AddNoteFragmentDirections.actionAddItemFragmentToItemListFragment()
            //импортируем import androidx.navigation.fragment.findNavController
            findNavController().navigate(action)
        }
    }

    //Вызывается при создании View.
    //      * Аргумент itemId Navigation определяет элемент редактирования или добавление нового элемента.
    //      * Если itemId положительный, этот метод извлекает информацию из базы данных и
    //      * позволяет пользователю обновлять его.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //кнопка сохранить при добавлении нового продукта
        binding.saveButton.setOnClickListener {
            addNewItem()
        }
    }
}