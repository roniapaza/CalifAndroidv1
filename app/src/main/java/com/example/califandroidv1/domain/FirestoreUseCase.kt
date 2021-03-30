package com.example.califandroidv1.domain

import com.example.califandroidv1.data.Repo.FirebaseRepo

class FirestoreUseCase {

    val repo = FirebaseRepo() //creamos una instancia del repositorio

    //LLama a un metodo en Data(Repositorio) ahi setear los datos
    fun setearUsuarioFirestore(nombre:String,apellido1:String,apellido2:String,correo:String) {
        repo.setUserData(nombre, apellido1, apellido2, correo)
    }
}