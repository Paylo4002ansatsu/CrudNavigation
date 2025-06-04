package com.example.myaplicationrecuperar.ui.cadastro;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myaplicationrecuperar.SSL.Certificado;
import com.example.myaplicationrecuperar.model.Estudante;
import com.example.myaplicationrecuperar.repositorios.EstudanteRepositorio;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CadastroViewModel extends ViewModel {

    private final MutableLiveData<Boolean> sucesso = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private EstudanteRepositorio repositorio;

    public CadastroViewModel() {
        configurarRetrofit();
    }

    private void configurarRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new Certificado().obterOkHttpClientInseguro())
                .build();

        repositorio = retrofit.create(EstudanteRepositorio.class);
    }

    public LiveData<Boolean> getSucesso() {
        return sucesso;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void cadastrarEstudante(Estudante estudante) {
        Call<Estudante> chamada = repositorio.cadastrarEstudante(estudante);

        chamada.enqueue(new Callback<Estudante>() {
            @Override
            public void onResponse(Call<Estudante> call, Response<Estudante> response) {
                if (response.isSuccessful()) {
                    sucesso.setValue(true);
                } else {
                    error.setValue("Erro ao cadastrar: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Estudante> call, Throwable t) {
                error.setValue("Falha na conexão: " + t.getMessage());
            }
        });
    }
}