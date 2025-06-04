package com.example.myaplicationrecuperar.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myaplicationrecuperar.R;
import com.example.myaplicationrecuperar.model.Estudante;

import java.util.List;

public class EstudanteAdapter extends RecyclerView.Adapter<EstudanteAdapter.EstudanteViewHolder> {

    private List<Estudante> estudantes;
    private OnItemClickListener listener;

    private OnDeleteClickListener deleteListener;
    private OnEditClickListener editListener;



    public interface OnItemClickListener {
        void onItemClick(Estudante estudante);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Estudante estudante);
    }

    public interface OnEditClickListener {
        void onEditClick(Estudante estudante);
    }

    public EstudanteAdapter(OnItemClickListener listener, OnDeleteClickListener deleteListener, OnEditClickListener editListener) {
        this.listener = listener;
        this.deleteListener = deleteListener;
        this.editListener = editListener;

    }

    public void atualizarLista(List<Estudante> novosEstudantes) {
        this.estudantes = novosEstudantes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EstudanteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_estudante, parent, false);
        return new EstudanteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EstudanteViewHolder holder, int position) {
        Estudante estudante = estudantes.get(position);
        holder.bind(estudante);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(estudante);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(estudante);
            }
        });

        holder.editButton.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEditClick(estudante);
            }
        });
    }

    @Override
    public int getItemCount() {
        return estudantes != null ? estudantes.size() : 0;
    }

    static class EstudanteViewHolder extends RecyclerView.ViewHolder {
        private final TextView textNome;
        private final TextView textIdade;
        private final ImageView deleteButton;
        private final ImageView editButton;

        public EstudanteViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.text_nome);
            textIdade = itemView.findViewById(R.id.text_idade);
            deleteButton = itemView.findViewById(R.id.buttonExcluir); // Adicione esta linha
            editButton = itemView.findViewById(R.id.buttonEditar); // Adicionado botão de editar

        }


        public void bind(Estudante estudante) {
            textNome.setText(estudante.getNome());
            textIdade.setText(String.format("Idade: %d", estudante.getIdade()));
        }
    }
}