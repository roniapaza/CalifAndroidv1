package com.example.califandroidv1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_registro.*

class RegistroAct : AppCompatActivity() {

    //lateinit var viewModel: FirestoreViewModel //Instanciamos FirestoreViewModel 1

    private lateinit var authFireB:FirebaseAuth //Para la autenticacion con Firebase
    var intentoRegistro:Int = 0 //intento de registro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        //viewModel = ViewModelProviders.of(this).get(FirestoreViewModel::class.java) 2

        authFireB = FirebaseAuth.getInstance() //Para la autenticacion con Firebase


        click_BotonRegistrar()
        click_BotonRegistroVolver()
    }

    //Esta funcion llama a la clase q creamos "SafeClickListener" q evita 2 clicks seguidos en 1 seg
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

    private fun conteoRegresivo(){ //Cuenta regresiva en el boton por 4 intentos fallidos
        val conteo = object:  CountDownTimer(10000,1000){
            override fun onTick(millisUntilFinished: Long) {
                btnConfirmar.setText(("espere " + (millisUntilFinished/1000+1)).toString()+" seg")
                btnConfirmar.setEnabled(false)
            }
            override fun onFinish() {
                btnConfirmar.setText("CONFIRMAR DATOS")
                btnConfirmar.setEnabled(true)
                intentoRegistro = 0

            }
        }.start()
    }

    private fun des_activarBotones(valor:Boolean){
        btnConfirmar.isEnabled=valor
        btnRegresar.isEnabled=valor
    }

    //REGISTRO FUNCION (MOVER LUEGO CON VIEWMODEL)
    fun setUserData(nombre:String, apellido1:String, apellido2:String, correo:String):Boolean {
        var creacionFirestoreOK:Boolean=true
        val db = Firebase.firestore//MOVER LUEGO CON VIEW MODEL
        val userHashMap = hashMapOf(
            "nombre" to nombre,
            "apellido1" to apellido1,
            "apellido2" to apellido2,
            "correo" to correo)

        db.collection("usuarios").add(userHashMap).addOnCompleteListener{
            creacionFirestoreOK = it.isSuccessful
        }
        return creacionFirestoreOK
    }


    fun click_BotonRegistrar(){//CONFIRMAR DATOS
        btnConfirmar.setSafeOnClickListener(){
            //Apuntamos los ID del XML:

            val nombre = findViewById<EditText>(R.id.regNombre)
            val apellido1 = findViewById<EditText>(R.id.regApellido1)
            val apellido2 = findViewById<EditText>(R.id.regApellido2)
            val correo = findViewById<EditText>(R.id.regCorreo)
            val password1 = findViewById<EditText>(R.id.regPassword1)
            val password2 = findViewById<EditText>(R.id.regPassword2)
            val barraProgreso = findViewById<ProgressBar>(R.id.regProgressBar)

            //val valor_nombre = regNombre.text.toString().trim() asi debería estar, mejorar
            val valor_nombre = nombre.text.trim()
            val valor_apellido1 = apellido1.text.trim()
            val valor_apellido2 = apellido2.text.trim()
            val valor_correo = correo.text.trim()

            //val emailCorrecto = android.util.Patterns.EMAIL_ADDRESS.matcher(valor_correo).matches()

            intentoRegistro += 1
            if (intentoRegistro >= 5) { //Si se ha intentado 4 veces fallidamente
                Toast.makeText(this,"Tiene 5 intentos fallidos, espere 10 segundos ",Toast.LENGTH_LONG).show()
                conteoRegresivo() //muestra 10 segundo el boton LOGIN desactivado
            }
            else {
                if(!nombre.text.isNullOrEmpty() && !apellido1.text.isNullOrEmpty() && !apellido2.text.isNullOrEmpty() && !correo.text.isNullOrEmpty()){
                    if (password1.text.toString() == password2.text.toString() && !password1.text.isNullOrEmpty()){
                        //MOSTRAMOS LA BARRA DE PROGRESO
                        barraProgreso.visibility = android.view.View.VISIBLE
                        des_activarBotones(false)

                        //CREAMOS EL USUARIO EN FIREBASE:
                        authFireB.createUserWithEmailAndPassword(valor_correo.toString(),
                            password1.text.toString()).addOnCompleteListener{
                            if (it.isSuccessful){ //USUARIO CREADO CORRECTAMENTE

                                if(setUserData(valor_nombre.toString(),valor_apellido1.toString(),valor_apellido2.toString(),valor_correo.toString())) {
                                    val user:FirebaseUser? = authFireB.currentUser //Recuperamos el usuario creado
                                    //ENVIAMOS EL CORREO AL USUARIO
                                    user?.sendEmailVerification()?.addOnCompleteListener(this) {
                                        if (it.isSuccessful) {
                                            barraProgreso.visibility = android.view.View.INVISIBLE
                                            des_activarBotones(true)
                                            Toast.makeText(
                                                this,
                                                "Gracias ha sido registrado. Revise su correo y active su usuario",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            val ventanaLogin1: Intent =
                                                Intent(applicationContext, MainActivity::class.java)
                                            startActivity(ventanaLogin1)
                                        }
                                        else {
                                            user.delete().addOnCompleteListener{
                                                if (it.isSuccessful){
                                                    Toast.makeText(
                                                        this,
                                                        "Error al registrarse, por favor pruebe nuevamente",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                else {
                                                    Toast.makeText(
                                                        this,
                                                        "Error! Por favor comuniquese con roniapaza@gmail.com o con su profesor!",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }

                                            barraProgreso.visibility = android.view.View.INVISIBLE
                                            des_activarBotones(true)
                                            //Lanzar el Toat de Error al enviar correo
                                            password1.setText("")
                                            password2.setText("")
                                            Toast.makeText(
                                                this,
                                                "Error al registrarse, por favor pruebe nuevamente",
                                                Toast.LENGTH_LONG
                                            ).show()

                                        }
                                    }
                                }
                                else{
                                    //Borrar el registro
                                    barraProgreso.visibility = android.view.View.INVISIBLE
                                    des_activarBotones(true)
                                    //Lanzar el Toat de Error al enviar correo
                                    password1.setText("")
                                    password2.setText("")
                                    Toast.makeText(this,"Error al registrarse, por favor pruebe nuevamente",Toast.LENGTH_LONG).show()

                                }

                                    //RECUPERAMOS EL USUARIO CREADO
                                //val user:FirebaseUser? = authFireB.currentUser
                                //ENVIAMOS EL CORREO AL USUARIO
                                /*user?.sendEmailVerification()?.addOnCompleteListener(this){
                                    if(it.isSuccessful){
                                        barraProgreso.visibility = android.view.View.INVISIBLE
                                        //CREAMOS EL USUARIO EN LA BASE DE DATOS:
                                        //firestoreViewModel.crearUsuario(valor_nombre.toString(),valor_apellido1.toString(),valor_apellido2.toString(),valor_correo.toString())
                                        if(setUserData(valor_nombre.toString(),valor_apellido1.toString(),valor_apellido2.toString(),valor_correo.toString())) {
                                            des_activarBotones(true)
                                            //Lanzar el Toast de Registrado
                                            Toast.makeText(this,"Gracias ha sido registrado. Revise su correo y active su usuario",Toast.LENGTH_LONG).show()
                                            val ventanaLogin1 : Intent = Intent(applicationContext, MainActivity::class.java)
                                            startActivity(ventanaLogin1)
                                        }
                                        else{
                                            //Borrar el registro
                                            barraProgreso.visibility = android.view.View.INVISIBLE
                                            des_activarBotones(true)
                                            //Lanzar el Toat de Error al enviar correo
                                            password1.setText("")
                                            password2.setText("")
                                            Toast.makeText(this,"Error al registrarse, por favor pruebe nuevamente",Toast.LENGTH_LONG).show()

                                        }
                                    }
                                    else {
                                        barraProgreso.visibility = android.view.View.INVISIBLE
                                        des_activarBotones(true)
                                        //Lanzar el Toat de Error al enviar correo
                                        password1.setText("")
                                        password2.setText("")
                                        Toast.makeText(this,"Error al enviarle el correo para confirmar su registro, pruebe nuevamente",Toast.LENGTH_LONG).show()
                                    }
                                }*/
                            }
                            else { //USUARIO AL CREAR EL USUARIO
                                barraProgreso.visibility = android.view.View.INVISIBLE
                                des_activarBotones(true)
                                password1.setText("")
                                password2.setText("")
                                println("el log es: "+ it.result)
                                Toast.makeText(this,"Error al crear el usuario, pruebe nuevamente",Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    else {
                        password1.setText("")
                        password2.setText("")
                        Toast.makeText(this,"Los password no coinciden",Toast.LENGTH_SHORT).show()

                    }
                }
                else{
                    password1.setText("")
                    password2.setText("")
                    Toast.makeText(this,"Revise que esten correctos todos los campos",Toast.LENGTH_SHORT).show()
                }
            }


        }
    }

    fun click_BotonRegistroVolver(){
        btnRegresar.setOnClickListener(){
            val ventanaLogin2 : Intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(ventanaLogin2)
        }
    }
}