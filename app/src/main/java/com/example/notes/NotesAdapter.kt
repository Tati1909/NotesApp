package com.example.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.databinding.ItemNoteBinding
import com.example.notes.room.NoteEntity
import com.example.notes.room.getFormattedData

//см 5.1.2.8
//Функция onItemClicked будет использоваться для обработки навигации, когда выбран элемент списка
//onItemClicked принимает данные Entity, т.к. будем переходить по id нашей Entity
class NotesAdapter(private val onItemClicked: (NoteEntity) -> Unit) :
    ListAdapter<NoteEntity, NotesAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private var binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //функция для установки названия продукта, цены и количества на складе
        fun bind(itemEntity: NoteEntity) {
            binding.apply {
                titleTextView.text = itemEntity.title
                //Получите цену в денежном формате с помощью getFormattedPrice() функции расширения
                descriptionTextView.text = itemEntity.description
                dateOfCreation.text = itemEntity.getFormattedData().toString()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNoteBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )

    }

    /**
    Заменяет старые данные существующей View новыми данными с пом метода bind
    //Получите текущий элемент, используя метод getItem(), передав позицию.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        //слушатель для вызова onItemClicked() элемента в текущей позиции
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        //привязываем данные к выбранному элементу списка
        holder.bind(current)
    }

    companion object {
        //DiffCallback класс, который вы указали для ListAdapter? Это просто объект,
        //который помогает ListAdapter определить, какие элементы в новом и старом списках отличаются при обновлении списка.
        private val DiffCallback = object : DiffUtil.ItemCallback<NoteEntity>() {

            override fun areItemsTheSame(
                oldItemEntity: NoteEntity,
                newItemEntity: NoteEntity
            ): Boolean {
                return oldItemEntity == newItemEntity
            }

            //проверка по названию продукта
            override fun areContentsTheSame(
                oldItemEntity: NoteEntity,
                newItemEntity: NoteEntity
            ): Boolean {
                return oldItemEntity.title == newItemEntity.title
            }
        }
    }

}