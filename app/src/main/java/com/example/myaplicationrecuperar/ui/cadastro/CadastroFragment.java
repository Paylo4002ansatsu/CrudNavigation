package com.example.myaplicationrecuperar.ui.cadastro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myaplicationrecuperar.databinding.FragmentCadastroBinding;
import com.example.myaplicationrecuperar.model.Estudante;

public class CadastroFragment extends Fragment {

    private FragmentCadastroBinding binding;
    private CadastroViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCadastroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CadastroViewModel.class);

        setupObservers();
        setupListeners();
    }

    private void setupObservers() {
        viewModel.getSucesso().observe(getViewLifecycleOwner(), sucesso -> {
            if (sucesso) {
                Toast.makeText(requireContext(), "Estudante cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        binding.btnCadastrar.setOnClickListener(v -> {
            String nome = binding.edtNome.getText().toString().trim();
            String idadeStr = binding.edtIdade.getText().toString().trim();

            if (nome.isEmpty()) {
                binding.edtNome.setError("Nome é obrigatório");
                return;
            }

            if (idadeStr.isEmpty()) {
                binding.edtIdade.setError("Idade é obrigatória");
                return;
            }

            int idade;
            try {
                idade = Integer.parseInt(idadeStr);
            } catch (NumberFormatException e) {
                binding.edtIdade.setError("Idade inválida");
                return;
            }

            Estudante estudante = new Estudante();
            estudante.setNome(nome);
            estudante.setIdade(idade);

            viewModel.cadastrarEstudante(estudante);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}