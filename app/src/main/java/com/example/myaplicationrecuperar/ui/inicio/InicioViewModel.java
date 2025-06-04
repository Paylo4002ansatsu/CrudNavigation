package com.example.myaplicationrecuperar.ui.inicio;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.myaplicationrecuperar.SSL.Certificado;
import com.example.myaplicationrecuperar.model.Estudante;
import com.example.myaplicationrecuperar.repositorios.EstudanteRepositorio;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InicioViewModel  extends ViewModel {

    private static final String TAG = "InicioViewModel";

    private final MutableLiveData<List<Estudante>> estudantes = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private EstudanteRepositorio repositorio;

    private Certificado certificado = new Certificado();

    public InicioViewModel() {
        configurarRetrofit();
    }

    private void configurarRetrofit() {
        Log.d(TAG, "Configurando Retrofit...");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.0.2.2:8080/")  // Barra no final é crucial
                .addConverterFactory(GsonConverterFactory.create())
                .client(certificado.obterOkHttpClientInseguro())
                .build();


        repositorio = retrofit.create(EstudanteRepositorio.class);
        Log.d(TAG, "Retrofit configurado com sucesso");
    }

    public LiveData<List<Estudante>> getEstudantes() {
        return estudantes;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void carregarEstudantes() {
        Log.d(TAG, "Iniciando carregamento de estudantes...");
        Call<List<Estudante>> chamada = repositorio.buscarEstudantes();
        Log.d(TAG, "Requisição para: " + chamada.request().url());

        chamada.enqueue(new Callback<List<Estudante>>() {
            @Override
            public void onResponse(Call<List<Estudante>> call, Response<List<Estudante>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) {
                        estudantes.setValue(response.body());
                    } else {
                        error.setValue("Lista de estudantes vazia");
                    }
                } else {
                    String errorMsg = "Erro " + response.code();
                    try {
                        errorMsg += ": " + response.errorBody().string();
                    } catch (IOException e) {
                        errorMsg += " (Não foi possível ler mensagem de erro)";
                    }
                    error.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<List<Estudante>> call, Throwable t) {
                String errorMsg = "Falha na conexão: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                error.setValue(errorMsg);
            }
        });
    }

    public void deletarEstudante(int id) {
        Call<Void> chamada = repositorio.excluirEstudante(id);

        chamada.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    carregarEstudantes();
                } else {
                    error.setValue("Erro ao deletar: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue("Falha na conexão: " + t.getMessage());
            }
        });
    }


}