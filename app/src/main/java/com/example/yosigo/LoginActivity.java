package com.example.yosigo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "Guardar en firebase";
    private String email;
    private String password = "";
    Button btn_rewrite;
    String Uid;

    //Control de botones accionados
    boolean camera, cassete, cat, woman, phone, pumpkin, santa, witch, flor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Reiniciamos botones a falso
        camera = cassete = cat = woman = phone = pumpkin = santa = witch = flor = false;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_rewrite = findViewById(R.id.login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //Recuperamos el email
        obtenerEmail();
        //guardarSesion(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private void obtenerEmail(){
        //Obtenemos la referencia al archvivo datos
        SharedPreferences preferencias=getSharedPreferences("datos", Context.MODE_PRIVATE);
        //Extraemos del archivo datos el campo email, si este no existe se devuelve un campo vacío
        email = preferencias.getString("email","");
    }

    public void guardarSesion(String uid) {
        //Obtenemos referencia del archivo datos
        SharedPreferences preferencias=getSharedPreferences("datos",Context.MODE_PRIVATE);
        //Creamos un editor para editar el archivo
        SharedPreferences.Editor editor=preferencias.edit();
        //Almacenamos en el editor el valor de la sesion
        editor.putString("sesion", uid);
        //Grabamos en el archivo el contenido de la sesion
        editor.commit();
    }

    /*BOTONES*/
    public void LogIn(View v){
        password = "123456";
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Se ha accedido correctamente
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Inicio correcto",
                                    Toast.LENGTH_SHORT).show();
                            //Obtenermos usuario actual
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Guardamos ID usuario
                            //Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            //guardarSesion(Uid);

                            Intent intent = new Intent (LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            //No se ha podido acceder correctamente
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Error",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Reiniciar constraseña
    public void borrarContraseña(View v){
        //Reiniciamos botones a falso
        camera = cassete = cat = woman = phone = pumpkin = santa = witch = flor = false;
        password="";
    }

    //Contraseña
    public void setCamera(View v){
        if(!camera){
            camera = true;
            password += "c4M";
        }
    }

    public void setCassete(View v){
        if(!cassete){
            cassete = true;
            password += "c3s";
        }
    }

    public void setCat(View v){
        if(!cat){
            cat = true;
            password += "Ca7";
        }
    }

    public void setWoman(View v){
        if(!woman){
            woman = true;
            password += "wMn";
        }
    }

    public void setPhone(View v){
        if(!phone){
            phone = true;
            password += "7fn";
        }
    }

    public void setPumpkin(View v){
        if(!cat){
            cat = true;
            password += "9kN";
        }
    }

    public void setSanta(View v){
        if(!santa){
            santa = true;
            password += "5at";
        }
    }

    public void setWitch(View v){
        if(!witch){
            witch = true;
            password += "w2H";
        }
    }

    public void setFlor(View v){
        if(!flor){
            flor = true;
            password += "8Er";
        }
    }
}