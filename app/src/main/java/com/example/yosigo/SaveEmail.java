package com.example.yosigo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SaveEmail extends AppCompatActivity {
    private EditText email_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_email);

        //Obtenemos referencia del Edit Text
        email_usuario=(EditText)findViewById(R.id.editTextTextEmailAddress);
        /*Obtenemos la referencia de la clase Shared Preferences, con el nombre de archivo de
        preferencia datos y la forma de creación privada para que solo pueda acceder la aplicación*/
        SharedPreferences preferencias=getSharedPreferences("datos", Context.MODE_PRIVATE);
        //Extraemos del archivo datos el campo email, si este no existe se devuelve un campo vacío
        email_usuario.setText(preferencias.getString("email",""));
    }

    /*Cuando se pulsa el botón de Guardar*/
    public void guardarEmail(View v) {
        //Obtenemos referencia del archivo datos
        SharedPreferences preferencias=getSharedPreferences("datos",Context.MODE_PRIVATE);
        //Creamos un editor para editar el archivo
        SharedPreferences.Editor editor=preferencias.edit();
        //Almacenamos en el editor el valor de email_usuario
        editor.putString("email", email_usuario.getText().toString());
        //Grabamos en el archivo el contenido de email_usuario
        editor.commit();

        //Vamos a la pantalla de introducir contraseña
        Intent intent = new Intent (v.getContext(), LoginActivity.class);
        startActivity(intent);
    }
}