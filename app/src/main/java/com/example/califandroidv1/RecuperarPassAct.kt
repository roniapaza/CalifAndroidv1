package com.example.califandroidv1

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_recuperar_pass.*

class RecuperarPassAct : AppCompatActivity() {

    private lateinit var authFireBRecup: FirebaseAuth//1:Creamos el objeto del tipo FireBaseAuth
    var intentoRecuperar:Int = 0 //intento de registro
    private lateinit var mDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_pass)

        authFireBRecup = FirebaseAuth.getInstance() //2: Instanciamos el objeto
        click_BotonRecuperarVolver()
        click_BotonRecuperarContra()
    }

    private fun conteoRegresivo(){ //Cuenta regresiva en el boton por 4 intentos fallidos
        val conteo = object:  CountDownTimer(10000,1000){
            override fun onTick(millisUntilFinished: Long) {
                btnRecuperarContra.setText(("espere " + (millisUntilFinished/1000+1)).toString()+" seg")
                btnRecuperarContra.setEnabled(false)
            }
            override fun onFinish() {
                btnRecuperarContra.setText("CONFIRMAR DATOS")
                btnRecuperarContra.setEnabled(true)
                intentoRecuperar = 0

            }
        }.start()
    }

    private fun click_BotonRecuperarContra() {
        btnRecuperarContra.setSafeOnClickListener {
            val email = findViewById<EditText>(R.id.etRecuperarCorreo)
            val valor_email = email.text.trim()

            intentoRecuperar += 1
            if (intentoRecuperar >= 5) { //Si se ha intentado 4 veces fallidamente
                Toast.makeText(this,"Tiene 5 intentos fallidos, espere 10 segundos ", Toast.LENGTH_LONG).show()
                conteoRegresivo() //muestra 10 segundo el boton LOGIN desactivado
            }
            else {
                val emailCorrecto = android.util.Patterns.EMAIL_ADDRESS.matcher(valor_email).matches()
                val RegisterBarraProg = findViewById<ProgressBar>(R.id.regProgressBar)

                if (emailCorrecto && !email.text.isNullOrEmpty()) {
                    //MOSTRAMOS LA BARRA DE PROGRESO
                    RegisterBarraProg.visibility = android.view.View.VISIBLE
                    btnRecuperarContra.isEnabled = false
                    //INICIAMOS LA RECUPERACION DEL PASSWORD
                    authFireBRecup.setLanguageCode("es")
                    authFireBRecup.sendPasswordResetEmail(valor_email.toString()).addOnCompleteListener{ task->
                        if (task.isSuccessful){
                            //REGISTRO EXITOSO
                            btnRecuperarContra.isEnabled = true
                            email.setText("")
                            RegisterBarraProg.visibility = android.view.View.INVISIBLE

                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("Mensaje")
                            builder.setMessage("Hola "+ valor_email.toString()+" hemos enviado a su correo un mensaje para recuperar su contraseña.")
                            builder.setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                                val ventanaLogin2 : Intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(ventanaLogin2)
                            })
                            builder.setCancelable(false)
                            val dialog: AlertDialog = builder.create()
                            dialog.show()

                            //val ventanaLogin2 : Intent = Intent(applicationContext, MainActivity::class.java)
                            //startActivity(ventanaLogin2)
                        }
                    }
                }
                else {
                    email.setText("")
                    Toast.makeText(
                            this,
                            "Escriba un correo correcto".toString(),
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun click_BotonRecuperarVolver(){
        btnRegresar2.setSafeOnClickListener(){
            val ventanaLogin2 : Intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(ventanaLogin2)
        }
    }

    //Esta funcion llama a la clase q creamos "SafeClickListener" q evita 2 clicks seguidos en 1 seg
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}