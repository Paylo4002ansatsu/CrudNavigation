package com.example.myaplicationrecuperar.ui.detalhe;

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

public class DetalheViewModel extends ViewModel {
    private static final String TAG = "DetalheViewModel";
    private static final double MEDIA_APROVACAO = 6.0;
    private static final double PRESENCA_MINIMA = 75.0;

    private final MutableLiveData<Estudante> estudanteLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mediaFormatada = new MutableLiveData<>();
    private final MutableLiveData<String> presencaFormatada = new MutableLiveData<>();
    private final MutableLiveData<String> situacao = new MutableLiveData<>();
    private final MutableLiveData<String> mensagemErro = new MutableLiveData<>();
    private Certificado certificado = new Certificado();

    private EstudanteRepositorio repositorio;

    public DetalheViewModel() {
        configurarRetrofit();
    }

    private void configurarRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(certificado.obterOkHttpClientInseguro())
                .build();

        repositorio = retrofit.create(EstudanteRepositorio.class);
    }

    public LiveData<Estudante> getEstudanteLiveData() { return estudanteLiveData; }
    public LiveData<String> getMediaFormatada() { return mediaFormatada; }
    public LiveData<String> getPresencaFormatada() { return presencaFormatada; }
    public LiveData<String> getSituacao() { return situacao; }
    public LiveData<String> getMensagemErro() { return mensagemErro; }

    public void carregarDetalhes(int id) {
        Call<Estudante> chamada = repositorio.buscarEstudantePorId(id);

        chamada.enqueue(new Callback<Estudante>() {
            @Override
            public void onResponse(Call<Estudante> call, Response<Estudante> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Estudante estudante = response.body();
                    estudanteLiveData.setValue(estudante);

                    double media = calcularMedia(estudante.getNotas());
                    double percentualPresenca = calcularPercentualPresenca(estudante.getPresenca());
                    int totalAulas = estudante.getPresenca() != null ? estudante.getPresenca().size() : 0;

                    mediaFormatada.setValue(formatarMedia(media));
                    presencaFormatada.setValue(formatarPresenca(percentualPresenca, totalAulas));
                    situacao.setValue(verificarSituacao(media, percentualPresenca));

                } else {
                    try {
                        mensagemErro.setValue("Erro: " + response.code() + " - " + response.errorBody().string());
                    } catch (IOException e) {
                        mensagemErro.setValue("Erro desconhecido ao processar erro");
                    }
                }
            }

            @Override
            public void onFailure(Call<Estudante> call, Throwable t) {
                mensagemErro.setValue("Erro de conexão: " + t.getMessage());
                Log.e(TAG, "Falha: ", t);
            }
        });
    }

    private double calcularMedia(List<Double> notas) {
        if (notas == null || notas.isEmpty()) return 0.0;
        double soma = 0.0;
        for (Double nota : notas) {
            if (nota != null) soma += nota;
        }
        return soma / notas.size();
    }

    private double calcularPercentualPresenca(List<Boolean> presencas) {
        if (presencas == null || presencas.isEmpty()) return 0.0;
        int totalPresencas = 0;
        for (Boolean presente : presencas) {
            if (presente != null && presente) totalPresencas++;
        }
        return (totalPresencas * 100.0) / presencas.size();
    }

    private String formatarMedia(double media) {
        return String.format("Média: %.1f", media);
    }

    private String formatarPresenca(double percentual, int totalAulas) {
        int presentes = (int) ((percentual * totalAulas) / 100);
        return String.format("Presença: %.1f%% (%d/%d aulas)", percentual, presentes, totalAulas);
    }

    private String verificarSituacao(double media, double presenca) {
        boolean aprovadoNotas = media >= MEDIA_APROVACAO;
        boolean aprovadoPresenca = presenca >= PRESENCA_MINIMA;

        if (aprovadoNotas && aprovadoPresenca) return "Aprovado";
        if (!aprovadoNotas && !aprovadoPresenca) return "Reprovado por nota e frequência";
        if (!aprovadoNotas) return "Reprovado por nota";
        return "Reprovado por frequência";
    }
}