package com.example.myaplicationrecuperar.repositorios;

import com.example.myaplicationrecuperar.model.Estudante;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EstudanteRepositorio {
       @GET("estudantes/")
       Call<List<Estudante>> buscarEstudantes();

       @GET("estudantes/{id}")
       Call<Estudante> buscarEstudantePorId(@Path("id") int id);

       @DELETE("estudantes/{id}")
       Call<Void> excluirEstudante(@Path("id") int id);

       @POST("estudantes/")
       Call<Estudante> cadastrarEstudante(@Body Estudante estudante);
}
