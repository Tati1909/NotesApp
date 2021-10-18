package com.example.notes.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.databinding.FragmentNotesListBinding

/**
 * A fragment representing a list of Items.
 */
class NotesListFragment : Fragment() {

    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNotesListBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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