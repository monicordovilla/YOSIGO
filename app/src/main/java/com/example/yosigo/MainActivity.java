package com.example.yosigo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    boolean facilitador;
    public ImageView toolbar_image;
    public static String sesion;
    private int secondLeft = 6;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        facilitador = false;
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        if(facilitador) {
            mAuth.signInWithEmailAndPassword("e.monicordovilla@go.ugr.es", "123456").addOnCompleteListener(
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
            mAuth.signInWithEmailAndPassword("monicordovilla@correo.ugr.es", "123456").addOnCompleteListener(
                    this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            guardarSesion(mAuth.getCurrentUser().getUid());
                            sesion=mAuth.getCurrentUser().getUid();
                            setPersona();
                        }
                    }
            );
        }
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


    public void begin(View view) {
        timer.schedule(task, 1000, 1000);
    }

    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    secondLeft--;
                    Log.d("TIMERTASK","quedan: " + secondLeft);
                    if (secondLeft < 0) {
                        timer.cancel();
                        Log.d("TIMERTASK","Fin de la cuenta regresiva: " + secondLeft);
                    }
                }
            });
        }
    };

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