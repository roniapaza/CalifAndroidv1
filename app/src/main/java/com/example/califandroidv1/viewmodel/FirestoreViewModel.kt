package com.example.califandroidv1.viewmodel

import androidx.lifecycle.ViewModel
import com.example.califandroidv1.domain.FirestoreUseCase

class FirestoreViewModel: ViewModel() { //Extendemos del ViewModel
    val firestoreUseCase = FirestoreUseCase()

    fun crearUsuario(nombre:String, apellido1:String, apellido2:String, correo:String){
        firestoreUseCase.setearUsuarioFirestore(nombre,apellido1, apellido2, correo)
    }
}