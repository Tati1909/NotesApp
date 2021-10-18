package com.example.notes.room

import android.app.Application

//Создадим экземпляр базы данных в классе Application.
//Используйте lazy делегат, чтобы экземпляр database создавался лениво при первом обращении к ссылке
// (а не при запуске приложения). Это создаст базу данных (физическую базу данных на диске) при первом доступе.
//Вы будете использовать этот database экземпляр
class NotesApplication : Application() {
    val database: NoteRoomDataBase by lazy { NoteRoomDataBase.getDatabase(this) }
}