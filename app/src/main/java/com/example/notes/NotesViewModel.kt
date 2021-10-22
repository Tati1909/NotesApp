package com.example.notes

import androidx.lifecycle.*
import com.example.notes.room.NoteDao
import com.example.notes.room.NoteEntity
import kotlinx.coroutines.launch
import java.util.*

class NotesViewModel(private val noteDao: NoteDao) : ViewModel() {

    //Переменная allItems в виде LiveData хранит в себе данные базы
    //Функция getItems() возвращает Flow поток с нашими данными Entity из базы данных,
    //упорядоченные в порядке возрастания.
    // Чтобы использовать данные как LiveData значение, используем asLiveData() функцию.
    val allItems: LiveData<List<NoteEntity>> = noteDao.getItems().asLiveData()

    //Добавляем в базу данных новый продукт
    //Функция будет вызываться из фрагмента AddNoteFragment
    fun addNewItem(itemName: String, itemDescription: String, dataOfCreation: Long) {
        val newItem = getNewItemEntry(itemName, itemDescription, dataOfCreation)
        insertItem(newItem)
    }
    //Обратите внимание, что вы не использовали viewModelScope.launch для addNewItem(), но это необходимо только в insertItem(),
    //когда мы напрямую отправляем  запросы в базу данных, т. е. вызываем методы DAO.
    // В Dao suspend функции и их разрешено вызывать только из сопрограммы или другой функции приостановки

    //функция принимает объект Item (продукт) и добавляет данные в базу данных неблокирующим способом.
    private fun insertItem(noteEntity: NoteEntity) {
        //ViewModelScope - это свойство ViewModel, которое автоматически отменяет свои дочерние сопрограммы при уничтожении ViewModel .
        viewModelScope.launch {
            noteDao.insert(noteEntity)
        }
    }

    //функция для обновления Entity(при удалении продукта
    // и при сохранении после редактирования заметки)
    private fun updateNoteAfterDelete(noteEntity: NoteEntity) {
        viewModelScope.launch {
            noteDao.update(noteEntity)
        }
    }

    //Экран « Добавить элемент» содержит три текстовых поля для получения сведений об элементе от пользователя.
//На этом шаге вы добавите функцию, чтобы проверить, не является ли текст в текстовых полях пустым.
//Вы будете использовать эту функцию для проверки ввода данных пользователем перед добавлением или обновлением объекта в базе данных.
//Эта проверка должна выполняться во ViewModel фрагменте, а не во фрагменте.
    fun isEntryValid(itemName: String, itemDescription: String): Boolean {
        if (itemName.isBlank() || itemDescription.isBlank()) {
            return false
        }
        return true
    }

    //В текущей задаче мы используем три строки в качестве входных данных (то, что мы будем вводить) и конвертируем их в Item entity
    // То есть 'Арбуз' конвертируется в @ColumnInfo(name = "name")
    //                                 val itemName: String
    private fun getNewItemEntry(
        titleNote: String,
        descriptionNote: String,
        dataOfCreation: Long
    ): NoteEntity {
        return NoteEntity(
            title = titleNote,
            description = descriptionNote,
            creationDate = dataOfCreation
        )
    }

    //Когда мы нажимаем на элемент списка, то переходим на детальное отображение заметки(fragment_detail_item.xml)
    //Именно это отображение заметки будет хранить в себе retrieveItem, которую мы вызываем в ItemDetailFragment
    //retrieveItem - получить элемент (по Id)
    fun retrieveItem(id: Int): LiveData<NoteEntity> {
        return noteDao.getItem(id).asLiveData()
        //Функция возвращает Flow. Чтобы использовать Flow как функцию LiveData вызываем asLiveData()
        //Т. е. asLiveData конвертирует itemDao.getItem(id) в LiveData<Item>
    }

    // функция для удаления объекта из базы данных
    fun deleteItem(noteEntity: NoteEntity) {
        viewModelScope.launch {
            noteDao.delete(noteEntity)
        }
    }

    //5.2.2.5
    //Функция нужна для обновления Entity (для сохранения заметки после ее редактирования)
    //getUpdatedItemEntry() конвертирует входящие параметры в данные Entity, переводит в нужный тип
    //и возвращает обновленную заметку
    private fun getUpdatedItemEntry(
        itemId: Int,
        noteName: String,
        noteDescription: String,
    ): NoteEntity {
        return NoteEntity(
            id = itemId,
            title = noteName,
            description = noteDescription,
            creationDate = Calendar.getInstance().timeInMillis
        )
    }

    //Обновляем отредактированную заметку в базе данных
    //Для сохранения заметки после ее редактирования
    fun updateNote(
        itemId: Int,
        noteName: String,
        noteDescription: String
    ) {
        //getUpdatedItemEntry() возвращает обновленную заметку
        val updatedNote = getUpdatedItemEntry(itemId, noteName, noteDescription)
        //И мы эту заметку обновляем в базе данных с помощью метода Dao (noteDao.update(noteEntity))
        updateNoteAfterDelete(updatedNote)
    }
}

//InventoryViewModelFactory класс для создания InventoryViewModel экземпляра
//Реализуйте create()метод. Проверьте, modelClass совпадает ли InventoryViewModel класс с классом,
// и верните его экземпляр. В противном случае выбросить исключение.
class NotesViewModelFactory(private val noteDao: NoteDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(noteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}