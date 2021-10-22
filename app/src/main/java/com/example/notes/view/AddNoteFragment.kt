package com.example.notes.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
                //получаем текущее время и сохраняем его в базу в формате Long
                dataOfCreation = Calendar.getInstance().timeInMillis
            )
            val action = AddNoteFragmentDirections.actionAddItemFragmentToItemListFragment()
            //импортируем import androidx.navigation.fragment.findNavController
            findNavController().navigate(action)
        }
    }

    //Эту функцию вызываем после нажатия кнопки 'сохранить', когда редактировали заметку.
    //if условие для проверки ввода пользователя(если текстовые поля заполнены,
    //то обновляем наши новые данные с помощью DAO метода suspend fun update(item: Item))
    //Используем itemId из аргументов навигации для передачи его в AddItemFragment(!но фрагмент редактирования)
    private fun updateNote() {
        if (isEntryValid()) {
            viewModel.updateNote(
                this@AddNoteFragment.navigationArgs.noteId,
                this@AddNoteFragment.binding.noteTitle.text.toString(),
                this@AddNoteFragment.binding.noteDescription.text.toString()
            )
            val action = AddNoteFragmentDirections.actionAddItemFragmentToItemListFragment()
            findNavController().navigate(action)
        }
    }

    //Вызывается при создании View.
    //      * Аргумент itemId Navigation определяет элемент редактирования или добавление нового элемента.
    //      * Если itemId положительный, этот метод извлекает информацию из базы данных и
    //      * позволяет пользователю обновлять его.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //получаем  itemId из аргументов навигации(ложили в ItemDetailFragment в методе editItem)
        val id = navigationArgs.noteId

        //Добавьте if-else блок с условием, чтобы проверить, id больше ли нуля,
        //и переместите прослушиватель нажатия кнопки « Сохранить» в else блок.
        //Внутри if блока извлеките объект с помощью id и добавьте к нему наблюдателя.
        //Внутри наблюдателя получите выбранный продукт(item) и вызовите bind() передавая данные этого продукта в текстовые поля фрагмента.
        //Полная функция предназначена для копирования и вставки. Это просто и легко понять;
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                noteEntity = selectedItem
                bind(noteEntity)
            }
        } else {
            //кнопка сохранить при добавлении новой заметки
            binding.saveButton.setOnClickListener {
                addNewItem()
            }
        }
    }

    //эта функция нужна для РЕДАКТИРОВАНИЯ заметки.
    //функция для привязки текстовых полей c деталями Entity
    //Когда мы переходим на экран редактирования, то текстовые поля должны быть заполнены
    //Реализация bind()функции очень похожа на то, что вы делали ранее в ItemDetailFragment
    private fun bind(itemEntity: NoteEntity) {

        binding.apply {
            noteTitle.setText(itemEntity.title, TextView.BufferType.SPANNABLE)
            noteDescription.setText(itemEntity.description, TextView.BufferType.SPANNABLE)
            //обработка кнопки сохранить после ее редактирования
            saveButton.setOnClickListener { updateNote() }
        }
    }
}