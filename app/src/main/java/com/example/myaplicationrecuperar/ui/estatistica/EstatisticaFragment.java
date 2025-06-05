package com.example.myaplicationrecuperar.ui.estatistica;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myaplicationrecuperar.databinding.FragmentEstatisticaBinding;
import com.example.myaplicationrecuperar.model.Estudante;
import java.util.List;




public class EstatisticaFragment extends Fragment {

    private FragmentEstatisticaBinding binding;
    private EstatisticaViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEstatisticaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EstatisticaViewModel.class);

        // Carregar dados
        viewModel.carregarEstatisticas();

        // Configurar observadores
        configurarObservadores();
    }

    private void configurarObservadores() {
        viewModel.getMediaGeral().observe(getViewLifecycleOwner(), media -> {
            binding.textMediaGeral.setText(String.format("Média geral: %.1f", media));
        });

        viewModel.getMaiorNota().observe(getViewLifecycleOwner(), maiorNota -> {
            binding.textMaiorNota.setText("Maior nota: " + maiorNota);
        });

        viewModel.getMenorNota().observe(getViewLifecycleOwner(), menorNota -> {
            binding.textMenorNota.setText("Menor nota: " + menorNota);
        });

        viewModel.getMediaIdade().observe(getViewLifecycleOwner(), mediaIdade -> {
            binding.textMediaIdade.setText(String.format("Média de idade: %.1f anos", mediaIdade));
        });

        viewModel.getAprovados().observe(getViewLifecycleOwner(), aprovados -> {
            if (aprovados == null || aprovados.isEmpty()) {
                binding.textAprovados.setText("Nenhum aluno aprovado");
            } else {
                binding.textAprovados.setText("Aprovados: " + String.join(", ", aprovados));
            }
        });

        viewModel.getReprovados().observe(getViewLifecycleOwner(), reprovados -> {
            binding.textReprovados.setText("Reprovados: " + String.join(", ", reprovados));
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}