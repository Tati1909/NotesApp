package com.example.notes.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notes.NotesAdapter
import com.example.notes.NotesViewModel
import com.example.notes.NotesViewModelFactory
import com.example.notes.R
import com.example.notes.databinding.FragmentNotesListBinding
import com.example.notes.room.NotesApplication

/**
 * A fragment representing a list of Items.
 */
class NotesListFragment : Fragment() {

    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!

    //привязываем viewModel с методами Dao к нашему фрагменту со списком заметок
    private val viewModel: NotesViewModel by activityViewModels {
        // про InventoryViewModelFactory см. BusScheduler 5/1/2/6
        NotesViewModelFactory(
            (activity?.application as NotesApplication).database.noteDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Добавили обработчик щелчка к RecyclerView для навигации в к Item Details
        val adapter = NotesAdapter {
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(this.context, 2)

        //Прикрепим наблюдателя observe к объекту allItems, чтобы отслеживать изменения данных.
        //allItems - получить все столбцы таблицы.
        //Внутри наблюдателя observe, вызоваем submitList() и передаем новый список с обновленными данными.
        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }

        binding.createANoteButton.setOnClickListener {
            val action = NotesListFragmentDirections.actionNotesListFragmentToAddItemFragment4(
                getString(R.string.new_note)
            )
            this.findNavController().navigate(action)
        }


        //binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

        //указываем списку, что нужно использовать адаптер
        //создаем новый объект Adapter(new не указываем)
        // binding.recyclerView.adapter = NotesAdapter(requireContext(), affirmationData)
    }

}