package com.example.califandroidv1

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
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*  //requiere q actives el plugin en build.gradle


class MainActivity : AppCompatActivity() {
    private lateinit var authFireBLog:FirebaseAuth //Para la autenticacion con Firebase
    var intentoLogin:Int = 0 //intento de logeo

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar) //volvemos a usar el tema original
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authFireBLog = FirebaseAuth.getInstance() //Para la autenticacion con Firebase

        click_BotonLogin()
        click_BotonRegistro()
        click_BotonRecuperarPass()
        click_BotonFaceRegistro()
        click_BotonGoogleRegistro()
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
                btnLogin.setText(("espere " + (millisUntilFinished/1000+1)).toString()+" seg")
                btnLogin.setEnabled(false)
            }
            override fun onFinish() {
                btnLogin.setText("LOGIN")
                btnLogin.setEnabled(true)
                intentoLogin = 0

            }
        }.start()
    }

    private fun des_activarBotones(valor:Boolean){
        btnLogin.isEnabled=valor
        btnRegistro.isEnabled=valor
        btnRecuperarPass.isEnabled=valor
        btnGoogle.isEnabled=valor
        btnFacebook.isEnabled=valor
    }

    fun click_BotonLogin(){
        //creamos el Listener q escucha cuando se hace click en el boton login
        btnLogin.setSafeOnClickListener(){
        //btnLogin.setOnClickListener(){

            intentoLogin += 1
            if (intentoLogin >= 5) { //Si se ha intentado 4 veces fallidamente
                Toast.makeText(this,"Tiene 5 intentos fallidos, espere 10 segundos ",Toast.LENGTH_LONG).show()
                conteoRegresivo() //muestra 10 segundo el boton LOGIN desactivado
            }
            else {
                val email = findViewById<EditText>(R.id.etUsuario) // apuntamos al id/email en XML
                val password = findViewById<EditText>(R.id.etPassword) //apuntamos al id en XML
                val loginBarraProg = findViewById<ProgressBar>(R.id.logprogressBar)

                val valor_email = email.text.trim()
                val valor_pass = password.text.trim()

                val emailCorrecto = android.util.Patterns.EMAIL_ADDRESS.matcher(valor_email).matches()
                if (emailCorrecto && !email.text.isNullOrEmpty()) {
                    if (valor_pass.length > 5 && valor_pass.length < 21 && !password.text.isNullOrEmpty()) {
                        //MOSTRAMOS LA BARRA DE PROGRESO
                        loginBarraProg.visibility = android.view.View.VISIBLE
                        des_activarBotones(false)
                        //INICIAMOS EL LOGIN CON USUARIO Y PASSWORD
                        authFireBLog.signInWithEmailAndPassword(
                            valor_email.toString(),
                            valor_pass.toString()
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {
                                //RECUPERAMOS EL USUARIO
                                val user: FirebaseUser? = authFireBLog.currentUser
                                val email_verificado = user?.isEmailVerified

                                //SI EL EMAIL HA SIDO VERIFICADO, ENTRAMOS A LA NUEVA VENTANA
                                if (email_verificado.toString() == "true") {
                                    loginBarraProg.visibility = android.view.View.INVISIBLE //OCULTAMOS la Barra de Progreso
                                    des_activarBotones(true)
                                    //Lanzar el Activity de logeado
                                    val ventanaLogeado: Intent =
                                        Intent(applicationContext, LogeadoAct::class.java).apply {
                                            putExtra("email", valor_email)
                                        }
                                    startActivity(ventanaLogeado) //llamamos a la actividad
                                }
                                else {
                                    loginBarraProg.visibility = android.view.View.INVISIBLE //OCULTAMOS la Barra de Progreso
                                    des_activarBotones(true)
                                    password.setText("")
                                    val builder = AlertDialog.Builder(this)
                                    builder.setTitle("Mensaje")
                                    builder.setMessage("Ingrese a su correo y confirme nuestro mensaje para activar su usuario.")
                                    builder.setPositiveButton("Aceptar", null)
                                    builder.setCancelable(false)
                                    val dialog: AlertDialog = builder.create()
                                    dialog.show()
                                }
                            }
                            else {
                                //Mensaje de Error al autenticarse
                                password.setText("")
                                loginBarraProg.visibility = android.view.View.INVISIBLE //OCULTAMOS la Barra de Progreso
                                des_activarBotones(true)
                                Toast.makeText(
                                    this,
                                    "Credenciales de acceso incorrectas".toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    else {
                        password.setText("")
                        Toast.makeText(
                            this,
                            "El password debe tener más de 6 caracteres".toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else {
                    password.setText("")
                    Toast.makeText(
                        this,
                        "Escriba un password o correo correcto".toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        }
    }

    fun click_BotonRegistro(){
        btnRegistro.setOnClickListener() {
            val ventanaRegistro : Intent = Intent (applicationContext, RegistroAct::class.java)
            startActivity(ventanaRegistro) //llamamos al VAL y lanzamos a la ventana
        }
    }

    fun click_BotonRecuperarPass(){
        btnRecuperarPass.setOnClickListener(){
            val ventanaRecuperarPass : Intent = Intent(applicationContext, RecuperarPassAct::class.java)
            startActivity(ventanaRecuperarPass)
        }
    }

    fun click_BotonFaceRegistro(){
        btnFacebook.setOnClickListener(){
            val ventanaFaceRegistro : Intent = Intent(applicationContext, FaceRegistroAct::class.java)
            startActivity(ventanaFaceRegistro)
        }
    }

    fun click_BotonGoogleRegistro(){
        btnGoogle.setOnClickListener(){
            val ventanaGoogleRegistro : Intent = Intent(applicationContext, GoogleRegistroAct::class.java)
            startActivity(ventanaGoogleRegistro)
        }
    }
}