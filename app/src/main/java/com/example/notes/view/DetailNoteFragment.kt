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
import com.example.notes.R
import com.example.notes.databinding.FragmentDetailItemBinding
import com.example.notes.room.NoteEntity
import com.example.notes.room.NotesApplication
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * [DetailNoteFragment] displays the details of the selected item.
 */
class DetailNoteFragment : Fragment() {

    private val navigationArgs: DetailNoteFragmentArgs by navArgs()

    private var _binding: FragmentDetailItemBinding? = null
    private val binding get() = _binding!!

    //Мы будем использовать это свойство для хранения информации об одной Entity
    //При переходе из списка в детальный экран, нам нужно связать данные,
    //которые будут передаваться с 1 экрана с данными экрана с деталями
    lateinit var noteEntity: NoteEntity

    //Привяжем данные ViewModel к TextView на экране « Сведения об элементе»
    //(чтобы при переходе из списка к детальному экрану наши данные отображались).
    //Используйте by делегат, чтобы передать инициализацию viewModel классу activityViewModels
    //application делаем принадлежащим NotesApplication
    private val viewModel: NotesViewModel by activityViewModels {
        NotesViewModelFactory(
            (activity?.application as NotesApplication).database.noteDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Это будет наш !!!!!аргумент в навигации к DetailFragment из NotesListFragment
        //(этот аргумент мы просто будем передавать в DetailFragment -
        // мы будем использовать эту id переменную для получения сведений об элементе)
        val id = navigationArgs.itemId

        //Присоединяем наблюдателя к возвращаемому значению Item(Entity) из метода retrieveItem
        viewModel.retrieveItem(id)
            .observe(this@DetailNoteFragment.viewLifecycleOwner) { selectedItem ->
                noteEntity = selectedItem
                bind(noteEntity)
                //Внутри лямбды передаем selectedItem в качестве параметра, который содержит Item объект,
                //полученный из базы данных. В теле лямбда-функции присвоим selectedItem значение item.
            }
    }

    /**
    Этот метод отображает диалоговое окно с предупреждением, чтобы получить подтверждение пользователя
    перед удалением заметки, и вызывает deleteItem() функцию при нажатии положительной кнопки
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteItem()
            }
            .show()
    }

    /**
    метод для вызова функции удаления заметки
    и обработки навигации.
     */
    private fun deleteItem() {
        viewModel.deleteItem(noteEntity)
        findNavController().navigateUp()
    }

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //функция для установки названия заметки и описания
    //такая же функция есть в NotesAdapter
    //И слушатели для кнопок
    private fun bind(noteEntity: NoteEntity) {
        binding.apply {
            noteTitle.text = noteEntity.title
            noteDescription.text = noteEntity.description
            //слушатель на кнопку удалить
            deleteButton.setOnClickListener { showConfirmationDialog() }
            //слушатель на кнопку редактировать
            //переходим к экрану редактирования
            //editButton.setOnClickListener { editItem() }
        }
    }


}