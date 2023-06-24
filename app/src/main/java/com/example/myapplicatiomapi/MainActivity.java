package com.example.myapplicatiomapi;

import android.app.AlertDialog;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.myapplicatiomapi.Interfaz.Jsonpaceahollder;
import com.example.myapplicatiomapi.modelo.Posts;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtUser, txtTitle, txtBody, txtSearch;
    private Button btnEnviar, btnBuscar;
    private Spinner spinnerOptions;

    private Jsonpaceahollder jsonpaceahollder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        btnEnviar.setOnClickListener(this);
        btnBuscar.setOnClickListener(this);

        setupSpinner();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonpaceahollder = retrofit.create(Jsonpaceahollder.class);
    }

    private void initializeViews() {
        txtUser = findViewById(R.id.txtUser);
        txtTitle = findViewById(R.id.txtTitle);
        txtBody = findViewById(R.id.txtBody);
        btnEnviar = findViewById(R.id.btnEnviar);
        spinnerOptions = findViewById(R.id.spinnerOptions);
        txtSearch = findViewById(R.id.txtSearch);
        btnBuscar = findViewById(R.id.btnBuscar);
    }

    private void setupSpinner() {
        List<String> options = new ArrayList<>();
        options.add("Elija");
        options.add("Leer");
        options.add("Ingresar");
        options.add("Actualizar");
        options.add("Eliminar");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerOptions.setAdapter(adapter);
        spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                switch (selectedItem) {
                    case "Leer":
                        leerInformacion();
                        break;
                    case "Ingresar":
                        txtUser.setText("");
                        txtTitle.setText("");
                        txtBody.setText("");
                        break;
                    case "Actualizar":
                        txtUser.setText("");
                        txtTitle.setText("");
                        txtBody.setText("");
                        break;
                    case "Eliminar":
                        txtUser.setText("");
                        txtTitle.setText("");
                        txtBody.setText("");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se seleccionó ninguna opción
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnEnviar) {
            String selectedItem = spinnerOptions.getSelectedItem().toString();

            switch (selectedItem) {
                case "Leer":
                    leerInformacion();
                    break;
                case "Ingresar":
                    enviarInformacion();
                    break;
                case "Actualizar":
                    actualizarInformacion();
                    break;
                case "Eliminar":
                    eliminarInformacion();
                    break;
            }
        } else if (view.getId() == R.id.btnBuscar) {
            buscarInformacion();
        }
    }

    private void leerInformacion() {
        Call<List<Posts>> call = jsonpaceahollder.getPost();
        call.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Error al obtener la información", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Posts> postList = response.body();
                StringBuilder result = new StringBuilder();
                for (Posts post : postList) {
                    String userId = String.valueOf(post.getUserId());
                    String title = post.getTitle();
                    String body = post.getBody();

                    result.append("User ID: ").append(userId).append("\n");
                    result.append("Title: ").append(title).append("\n");
                    result.append("Body: ").append(body).append("\n\n");
                }
                showAlert("Información", result.toString());
            }

            @Override
            public void onFailure(Call<List<Posts>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error al obtener la información", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarInformacion() {
        String user = txtUser.getText().toString().trim();
        String title = txtTitle.getText().toString().trim();
        String body = txtBody.getText().toString().trim();

        if (user.isEmpty() || title.isEmpty() || body.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Posts post = new Posts(Integer.parseInt(user), title, body);

        Call<Posts> call = jsonpaceahollder.createPost(post);
        call.enqueue(new Callback<Posts>() {
            @Override
            public void onResponse(Call<Posts> call, Response<Posts> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Error al enviar la información", Toast.LENGTH_SHORT).show();
                    return;
                }

                Posts createdPost = response.body();
                showAlert("Información enviada", "ID: " + createdPost.getId());
            }

            @Override
            public void onFailure(Call<Posts> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error al enviar la información", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarInformacion() {
        String user = txtUser.getText().toString().trim();
        String title = txtTitle.getText().toString().trim();
        String body = txtBody.getText().toString().trim();

        if (user.isEmpty() || title.isEmpty() || body.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String postIdText = txtSearch.getText().toString().trim();
        if (postIdText.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor, ingrese un ID de publicación", Toast.LENGTH_SHORT).show();
            return;
        }

        int postId;
        try {
            postId = Integer.parseInt(postIdText);
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "El ID de publicación no es válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Posts post = new Posts(Integer.parseInt(user), title, body);

        Call<Posts> call = jsonpaceahollder.updatePost(postId, post);
        call.enqueue(new Callback<Posts>() {
            @Override
            public void onResponse(Call<Posts> call, Response<Posts> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Error al actualizar la información", Toast.LENGTH_SHORT).show();
                    return;
                }

                showAlert("Información actualizada", "ID: " + postId);
            }

            @Override
            public void onFailure(Call<Posts> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error al actualizar la información", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void eliminarInformacion() {
        String postId = txtSearch.getText().toString().trim();

        if (postId.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor, ingrese un ID de publicación", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Void> call = jsonpaceahollder.deletePost(Integer.parseInt(postId));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Error al eliminar la información", Toast.LENGTH_SHORT).show();
                    return;
                }

                showAlert("Información eliminada", "ID: " + postId);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error al eliminar la información", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarInformacion() {
        String postId = txtSearch.getText().toString().trim();

        if (postId.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor, ingrese un ID de publicación", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Posts> call = jsonpaceahollder.getPostById(Integer.parseInt(postId));
        call.enqueue(new Callback<Posts>() {
            @Override
            public void onResponse(Call<Posts> call, Response<Posts> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "No se encontró la información", Toast.LENGTH_SHORT).show();
                    return;
                }

                Posts post = response.body();
                String userId = String.valueOf(post.getUserId());
                String title = post.getTitle();
                String body = post.getBody();

                StringBuilder result = new StringBuilder();
                result.append("User ID: ").append(userId).append("\n");
                result.append("Title: ").append(title).append("\n");
                result.append("Body: ").append(body).append("\n");

                showAlert("Información encontrada", result.toString());
            }

            @Override
            public void onFailure(Call<Posts> call, Throwable t) {
                Toast.makeText(MainActivity.this, "No se encontró la información", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Aceptar", null)
                .create()
                .show();
    }
}