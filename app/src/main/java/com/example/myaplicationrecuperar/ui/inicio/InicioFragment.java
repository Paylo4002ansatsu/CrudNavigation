package com.example.myaplicationrecuperar.ui.inicio;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.myaplicationrecuperar.R;
import com.example.myaplicationrecuperar.databinding.FragmentInicioBinding;
import com.example.myaplicationrecuperar.ui.EstudanteAdapter;

public class InicioFragment extends Fragment {

    private InicioViewModel mViewModel;
    private FragmentInicioBinding binding;
    private EstudanteAdapter adapter;

    public static InicioFragment newInstance() {
        return new InicioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInicioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(InicioViewModel.class);

        setupRecyclerView();

        mViewModel.getEstudantes().observe(getViewLifecycleOwner(), estudantes -> {
            if (estudantes != null && !estudantes.isEmpty()) {
                adapter.atualizarLista(estudantes);
                binding.recyclerView.setVisibility(View.VISIBLE);
            };

            int total = (estudantes != null) ? estudantes.size() : 0;
            binding.alunosCount.setText(String.valueOf(total));
        });

        mViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                binding.textError.setText(error);
                binding.textError.setVisibility(View.VISIBLE);
            } else {
                binding.textError.setVisibility(View.GONE);
            }


        });

        mViewModel.carregarEstudantes();
    }

    private void setupRecyclerView() {
        adapter = new EstudanteAdapter(
                estudante -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID_ESTUDANTE", estudante.getId());
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.action_inicioFragment_to_detalheFragment, bundle);
                },
                estudante -> {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Confirmar exclusão")
                            .setMessage("Deseja realmente excluir " + estudante.getNome() + "?")
                            .setPositiveButton("Sim", (dialog, which) -> {
                                mViewModel.deletarEstudante(estudante.getId());
                            })
                            .setNegativeButton("Não", null)
                            .show();
                },
                estudante -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID_ESTUDANTE", estudante.getId());
                    NavController navController = Navigation.findNavController(requireView());
                   // navController.navigate(R.id.action_inicioFragment_to_editarFragment, bundle);
                }
        );

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

//    private void atualizarListaEstudantes(List<Estudante> estudantes) {
//        if (estudantes != null && !estudantes.isEmpty()) {
//            adapter.atualizarLista(estudantes);
//            binding.recyclerView.setVisibility(View.VISIBLE);
//            binding.textEmpty.setVisibility(View.GONE);
//        } else {
//            binding.recyclerView.setVisibility(View.GONE);
//            binding.textEmpty.setVisibility(View.VISIBLE);
//        }
//
//
//
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}