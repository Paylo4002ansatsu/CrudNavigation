package com.example.myaplicationrecuperar.ui.estatistica;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.AtomicReference;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myaplicationrecuperar.SSL.Certificado;
import com.example.myaplicationrecuperar.model.Estudante;
import com.example.myaplicationrecuperar.repositorios.EstudanteRepositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EstatisticaViewModel extends ViewModel {

    private final MutableLiveData<List<Estudante>> estudantes = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Double> mediaGeral = new MutableLiveData<>();
    private final MutableLiveData<String> maiorNota = new MutableLiveData<>();
    private final MutableLiveData<String> menorNota = new MutableLiveData<>();
    private final MutableLiveData<Double> mediaIdade = new MutableLiveData<>();
    private final MutableLiveData<List<String>> aprovados = new MutableLiveData<>();
    private final MutableLiveData<List<String>> reprovados = new MutableLiveData<>();
    private final MutableLiveData<Boolean> carregando = new MutableLiveData<>(false);
    private Certificado certificado = new Certificado();

    private EstudanteRepositorio repositorio;

    public EstatisticaViewModel() {
        configurarRetrofit();
    }

    private void configurarRetrofit() {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.0.2.2:8080/")
                .client(certificado.obterOkHttpClientInseguro()) // Use o client já configurado
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        repositorio = retrofit.create(EstudanteRepositorio.class);
    }

    public void carregarEstatisticas() {
        carregando.setValue(true);

        Call<List<Estudante>> chamadaLista = repositorio.buscarEstudantes();
        chamadaLista.enqueue(new Callback<List<Estudante>>() {
            @Override
            public void onResponse(Call<List<Estudante>> call, Response<List<Estudante>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Estudante> listaBasica = response.body();
                    if (listaBasica.isEmpty()) {
                        error.setValue("Nenhum estudante encontrado");
                        carregando.setValue(false);
                    } else {
                        carregarDetalhesCompletos(listaBasica);
                    }
                } else {
                    error.setValue("Erro ao carregar lista: " + response.code());
                    carregando.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<List<Estudante>> call, Throwable t) {
                error.setValue("Falha na conexão: " + t.getMessage());
                carregando.setValue(false);
            }
        });
    }

    private void carregarDetalhesCompletos(List<Estudante> estudantesBasicos) {
        List<Estudante> estudantesCompletos = new ArrayList<>();
        AtomicInteger chamadasCompletas = new AtomicInteger(0);
        int totalEstudantes = estudantesBasicos.size();

        Log.d("EstatisticaViewModel", "Iniciando carregamento de detalhes para " + totalEstudantes + " estudantes");

        for (Estudante basico : estudantesBasicos) {
            repositorio.buscarEstudantePorId(basico.getId()).enqueue(new Callback<Estudante>() {
                @Override
                public void onResponse(Call<Estudante> call, Response<Estudante> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Estudante completo = response.body();
                        estudantesCompletos.add(completo);
                        Log.d("EstatisticaViewModel", "Dados completos carregados para: " + completo.getNome());
                    }

                    int completas = chamadasCompletas.incrementAndGet();
                    if (completas == totalEstudantes) {
                        processarCarregamentoCompleto(estudantesCompletos);
                    }
                }

                @Override
                public void onFailure(Call<Estudante> call, Throwable t) {
                    int completas = chamadasCompletas.incrementAndGet();
                    Log.e("EstatisticaViewModel", "Erro ao carregar estudante: " + t.getMessage());

                    if (completas == totalEstudantes) {
                        processarCarregamentoCompleto(estudantesCompletos);
                    }
                }
            });
        }
    }

    private void processarCarregamentoCompleto(List<Estudante> estudantesCompletos) {
        carregando.setValue(false);

        if (estudantesCompletos.isEmpty()) {
            error.setValue("Não foi possível carregar dados completos de nenhum estudante");
        } else {
            estudantes.setValue(estudantesCompletos);
            calcularEstatisticas(estudantesCompletos);
            Log.i("EstatisticaViewModel", "Estatísticas calculadas para " + estudantesCompletos.size() + " estudantes");
        }
    }

    private void calcularEstatisticas(List<Estudante> estudantes) {
        if (estudantes == null || estudantes.isEmpty()) {
            error.setValue("Nenhum dado disponível para cálculo");
            return;
        }

        double mediaGeralValue = calcularMediaGeral(estudantes);
        Estudante maiorNotaEstudante = getAlunoMaiorNota(estudantes);
        Estudante menorNotaEstudante = getAlunoMenorNota(estudantes);
        double mediaIdadeValue = calcularMediaIdade(estudantes);
        List<String> aprovadosList = getAprovados(estudantes);
        List<String> reprovadosList = getReprovados(estudantes);

        mediaGeral.postValue(mediaGeralValue);

        if (maiorNotaEstudante != null) {
            maiorNota.postValue(maiorNotaEstudante.getNome() + " (" + calcularMedia(maiorNotaEstudante.getNotas()) + ")");
        }

        if (menorNotaEstudante != null) {
            menorNota.postValue(menorNotaEstudante.getNome() + " (" + calcularMedia(menorNotaEstudante.getNotas()) + ")");
        }

        mediaIdade.postValue(mediaIdadeValue);
        aprovados.postValue(aprovadosList);
        reprovados.postValue(reprovadosList);
    }

    // Métodos auxiliares de cálculo
    public double calcularMedia(List<Double> notas) {
        if (notas == null || notas.isEmpty()) return 0.0;

        double soma = 0.0;
        int count = 0;
        for (Double nota : notas) {
            if (nota != null) {
                soma += nota;
                count++;
            }
        }
        return count > 0 ? soma / count : 0.0;
    }

    public double calcularMediaGeral(List<Estudante> estudantes) {
        if (estudantes == null || estudantes.isEmpty()) return 0.0;

        double somaMedias = 0.0;
        int count = 0;
        for (Estudante estudante : estudantes) {
            if (estudante != null && estudante.getNotas() != null) {
                double media = calcularMedia(estudante.getNotas());
                somaMedias += media;
                count++;
            }
        }
        return count > 0 ? somaMedias / count : 0.0;
    }

    public Estudante getAlunoMaiorNota(List<Estudante> estudantes) {
        if (estudantes == null || estudantes.isEmpty()) return null;

        Estudante maiorNota = null;
        double maiorMedia = -1;

        for (Estudante estudante : estudantes) {
            if (estudante != null && estudante.getNotas() != null) {
                double mediaAtual = calcularMedia(estudante.getNotas());
                if (maiorNota == null || mediaAtual > maiorMedia) {
                    maiorMedia = mediaAtual;
                    maiorNota = estudante;
                }
            }
        }
        return maiorNota;
    }

    public Estudante getAlunoMenorNota(List<Estudante> estudantes) {
        if (estudantes == null || estudantes.isEmpty()) return null;

        Estudante menorNota = null;
        double menorMedia = Double.MAX_VALUE;

        for (Estudante estudante : estudantes) {
            if (estudante != null && estudante.getNotas() != null) {
                double mediaAtual = calcularMedia(estudante.getNotas());
                if (menorNota == null || mediaAtual < menorMedia) {
                    menorMedia = mediaAtual;
                    menorNota = estudante;
                }
            }
        }
        return menorNota;
    }

    public double calcularMediaIdade(List<Estudante> estudantes) {
        if (estudantes == null || estudantes.isEmpty()) return 0.0;

        int soma = 0;
        int count = 0;
        for (Estudante estudante : estudantes) {
            if (estudante != null) {
                soma += estudante.getIdade();
                count++;
            }
        }
        return count > 0 ? (double) soma / count : 0.0;
    }

    public List<String> getAprovados(List<Estudante> estudantes) {
        List<String> aprovados = new ArrayList<>();
        if (estudantes != null) {
            for (Estudante estudante : estudantes) {
                if (estudante != null && estudante.getNotas() != null && estudante.getPresenca() != null) {
                    double media = calcularMedia(estudante.getNotas());
                    double presenca = calcularPercentualPresenca(estudante.getPresenca());
                    if (media >= 6.0 && presenca >= 75.0) {
                        aprovados.add(estudante.getNome());
                    }
                }
            }
        }
        return aprovados;
    }

    public List<String> getReprovados(List<Estudante> estudantes) {
        List<String> reprovados = new ArrayList<>();
        if (estudantes != null) {
            for (Estudante estudante : estudantes) {
                if (estudante != null && estudante.getNotas() != null && estudante.getPresenca() != null) {
                    double media = calcularMedia(estudante.getNotas());
                    double presenca = calcularPercentualPresenca(estudante.getPresenca());
                    if (media < 6.0 || presenca < 75.0) {
                        reprovados.add(estudante.getNome());
                    }
                }
            }
        }
        return reprovados;
    }

    private double calcularPercentualPresenca(List<Boolean> presencas) {
        if (presencas == null || presencas.isEmpty()) return 0.0;

        int presentes = 0;
        for (Boolean p : presencas) {
            if (p != null && p) presentes++;
        }
        return (presentes * 100.0) / presencas.size();
    }

    // Getters para LiveData
    public LiveData<List<Estudante>> getEstudantes() {
        return estudantes;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Double> getMediaGeral() {
        return mediaGeral;
    }

    public LiveData<String> getMaiorNota() {
        return maiorNota;
    }

    public LiveData<String> getMenorNota() {
        return menorNota;
    }

    public LiveData<Double> getMediaIdade() {
        return mediaIdade;
    }

    public LiveData<List<String>> getAprovados() {
        return aprovados;
    }

    public LiveData<List<String>> getReprovados() {
        return reprovados;
    }

    public LiveData<Boolean> getCarregando() {
        return carregando;
    }
}