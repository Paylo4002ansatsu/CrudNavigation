package com.example.myaplicationrecuperar.ui.detalhe;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myaplicationrecuperar.R;
import com.example.myaplicationrecuperar.databinding.FragmentDetalheBinding;

public class DetalheFragment extends Fragment {

    private FragmentDetalheBinding binding;
    private DetalheViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetalheBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DetalheViewModel.class);

        int idEstudante = getArguments() != null ? getArguments().getInt("ID_ESTUDANTE", -1) : -1;

        if (idEstudante == -1) {
            Toast.makeText(getContext(), "Estudante não encontrado", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        // Observadores
        viewModel.getEstudanteLiveData().observe(getViewLifecycleOwner(), estudante -> {
            if (estudante != null) {
                binding.txtNome.setText(estudante.getNome());
            }
        });

        viewModel.getMediaFormatada().observe(getViewLifecycleOwner(), binding.txtMedia::setText);
        viewModel.getPresencaFormatada().observe(getViewLifecycleOwner(), binding.txtPresenca::setText);
        viewModel.getSituacao().observe(getViewLifecycleOwner(), binding.txtSituacao::setText);
        viewModel.getMensagemErro().observe(getViewLifecycleOwner(), erro -> {
            Toast.makeText(getContext(), erro, Toast.LENGTH_SHORT).show();
        });

        viewModel.carregarDetalhes(idEstudante);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}