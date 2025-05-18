package com.example.angenda_online;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registro extends AppCompatActivity {

    EditText NombreET, CorreoET, ContrasenaET, ConfirmarContrasenaET;
    Button RegistrarUsuario;
    TextView TengoUnaCuentaTXT;

    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    String nombre = "", correo = "", password = "", confirmarpassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Registrar");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        NombreET = findViewById(R.id.NombreET);
        CorreoET = findViewById(R.id.CorreoET);
        ContrasenaET = findViewById(R.id.ContrasenaET);
        ConfirmarContrasenaET = findViewById(R.id.ConfirmarContrasenaET);
        RegistrarUsuario = findViewById(R.id.RegistrarUsuario);
        TengoUnaCuentaTXT = findViewById(R.id.TengoUnaCuentaTXT);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        RegistrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidarDatos();
            }
        });

        TengoUnaCuentaTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registro.this, Login.class));
            }
        });
    }

    private void ValidarDatos() {
        nombre = NombreET.getText().toString().trim();
        correo = CorreoET.getText().toString().trim();
        password = ContrasenaET.getText().toString().trim();
        confirmarpassword = ConfirmarContrasenaET.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(this, "Ingrese nombre", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Ingrese contraseña", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmarpassword)) {
            Toast.makeText(this, "Confirme la contraseña", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmarpassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        } else {
            CrearCuenta();
        }
    }

    private void CrearCuenta() {
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(correo, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        GuardarInformacion();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Registro.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void GuardarInformacion() {
        String uid = firebaseAuth.getUid();

        HashMap<String, String> Datos = new HashMap<>();
        Datos.put("uid", uid);
        Datos.put("correo", correo);
        Datos.put("nombres", nombre);
        Datos.put("password", password); // ⚠️ Guardar contraseñas en texto plano NO es seguro

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        databaseReference.child(uid)
                .setValue(Datos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Registro.this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Registro.this, MenuPrincipal.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Registro.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return super.onSupportNavigateUp();
    }
}






