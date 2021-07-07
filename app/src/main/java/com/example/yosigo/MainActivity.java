package com.example.yosigo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Comprobando rol";
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    boolean facilitador;
    public ImageView toolbar_image;
    public static String sesion;
    private String email;
    private String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtenemos referencia del archivo datos
        SharedPreferences preferencias=getSharedPreferences("datos", Context.MODE_PRIVATE);
        //Extraemos del archivo datos el campo email, si este no existe se devuelve un campo vacío
        email = preferencias.getString("email","");
        //Extraemos del archivo datos el campo sesion, si este no existe se devuelve un campo vacío
        sesion = preferencias.getString("sesion","");

        if(email.equals("")){
            Log.d(TAG, "No hay email");
            Intent intent = new Intent (MainActivity.this, SaveEmail.class);
            startActivity(intent);
            finish();
        } else if(sesion.equals("")){
            Log.d(TAG, "No hay sesión");
            Intent intent = new Intent (MainActivity.this, SaveEmail.class);
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "sesion" + sesion);
            DocumentReference docRef = FirebaseFirestore.getInstance()
                    .collection("users").document(sesion);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String rol = document.getData().get("Rol").toString();

                            if(rol.equals("Facilitador")){
                                Log.d(TAG, "Puede ir a inicio facilitador");
                                setFacilitador();
                            }
                            else if(rol.equals("Persona")){
                                Log.d(TAG, "Puede ir a inicio persona");
                                setPersona();
                            }
                            else{
                                Log.d(TAG, "Debe darse de alta como facilitador o persona");
                                Toast.makeText( getApplicationContext(),
                                        "Pide que te de el administrador de alta", Toast.LENGTH_LONG);
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Log.d(TAG, "No such document");
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        String TAG = "Ha ocurrido un error";
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        /*
         * GUARDADO PARA DESARROLLO
         *

        facilitador = true;
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        if(facilitador) {
            mAuth.signInWithEmailAndPassword("e.monicordovilla@go.ugr.es", "c4Mc3sCa7").addOnCompleteListener(
                    this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            guardarSesion(mAuth.getCurrentUser().getUid());
                            sesion=mAuth.getCurrentUser().getUid().toString();
                            setFacilitador();
                        }
                    }
            );

        } else {
            mAuth.signInWithEmailAndPassword("monicordovilla@correo.ugr.es", "c4Mc3sCa7").addOnCompleteListener(
                    this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            guardarSesion(mAuth.getCurrentUser().getUid());
                            sesion=mAuth.getCurrentUser().getUid();
                            setPersona();
                        }
                    }
            );
        }*/
    }

    private void setFacilitador(){
        setContentView(R.layout.activity_main_facilitador);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_activity, R.id.nav_forums,
                R.id.nav_category, R.id.nav_goals, R.id.nav_group)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void setPersona(){
        setContentView(R.layout.activity_main_persona);
        Toolbar toolbar = findViewById(R.id.toolbar_persona);
        //toolbar_image = (ImageView) findViewById(R.id.image_toolbar_persona);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_persona);
        NavigationView navigationView = findViewById(R.id.nav_view_persona);
        navigationView.setItemIconTintList(null);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_calendar, R.id.nav_forums)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_persona);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }


    public void setToolbar_image(int image){
        toolbar_image.setImageResource(image);
    }

    public void guardarSesion(String uid) {
        //Obtenemos referencia del archivo datos
        SharedPreferences preferencias=getSharedPreferences("datos", Context.MODE_PRIVATE);
        //Creamos un editor para editar el archivo
        SharedPreferences.Editor editor=preferencias.edit();
        //Almacenamos en el editor el valor de la sesion
        editor.putString("sesion", uid);
        //Grabamos en el archivo el contenido de la sesion
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(facilitador) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        } else {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_persona);
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        }
    }
}