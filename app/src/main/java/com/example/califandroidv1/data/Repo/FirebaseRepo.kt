package com.example.califandroidv1.data.Repo

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseRepo {

    val db = Firebase.firestore
    //val db = FirebaseFirestore.getInstance()
    var creacionCorrecta:Boolean = true
    //LLama a un metodo en Data(Repositorio) ahi setear los datos
    fun setUserData(nombre:String, apellido1:String, apellido2:String, correo:String) {
        val userHashMap = hashMapOf(
            "nombre" to nombre,
            "apellido1" to apellido1,
            "apellido2" to apellido2,
            "correo" to correo)

        //mandamos a Firebase q agregue la colleccion
        db.collection("usuarios").add(userHashMap).addOnCompleteListener{
            creacionCorrecta = it.isSuccessful
        }
    }
}