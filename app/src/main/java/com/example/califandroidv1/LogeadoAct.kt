package com.example.califandroidv1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LogeadoAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logeado)



    }

    override fun onBackPressed() { //Esta funcion se ejecuta al presionar "atras" en el boton
        //inferior derecho del dispotivo
        //Lo dejamos vacío para q no haga nada.
        //super.onBackPressed()
    }
}