package com.example.angenda_online;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.DragStartHelper;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPrincipal extends AppCompatActivity {

    Button CerrarSesion;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    TextView NombrePrincipal, CorreoPrincipal;
    ProgressBar progressBarDatos;

    DatabaseReference Usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_principal2);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Agenda Online");
        }


        NombrePrincipal = findViewById(R.id.NombresPrincipal);
        CorreoPrincipal = findViewById(R.id.CorreoPrincipal);
        progressBarDatos = findViewById(R.id.progressBarDatos);

        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");


        CerrarSesion = findViewById(R.id.CerrarSesion);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalirAplicacion();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    @Override
    protected void onStart() {
        ComprobarInicioSesion();
        super.onStart();
    }

    private void ComprobarInicioSesion() {
        if (user != null){
            CargaDeDatos();
        }else {
            //lo dirige al main actiticy si no inicia
            startActivity(new Intent(MenuPrincipal.this,MainActivity.class));
            finish();
        }

    }


    private void CargaDeDatos(){
        Usuarios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // si el usuario existe
                if (snapshot.exists()){
                    progressBarDatos.setVisibility(View.GONE);

                    NombrePrincipal.setVisibility(View.VISIBLE);
                    CorreoPrincipal.setVisibility(View.VISIBLE);



                    //obtener los datos
                    String nombres = ""+snapshot.child("nombres").getValue();
                    String correo = ""+snapshot.child("correo").getValue();


                    //setear los datos en los respectivas textviwe
                    NombrePrincipal.setText(nombres);
                    CorreoPrincipal.setText(correo);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SalirAplicacion() {
        firebaseAuth.signOut();
        startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
        Toast.makeText(this, "Cerraste sesion exitosamente", Toast.LENGTH_SHORT).show();
    }
}