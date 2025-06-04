package com.example.myaplicationrecuperar.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Estudante {

    private int id;
    private String nome;
    private int idade;
    private List<Double> notas=new ArrayList<>();
    private List<Boolean> presenca=new ArrayList<>();

    public Estudante(){}
    public Estudante(String nome, int idade) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
    }
    public Estudante(int id, String nome, int idade) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public List<Double> getNotas() {
        return notas;
    }


    public List<Boolean> getPresenca() {
        return presenca;
    }

    public void setNotas(List<Double> notas) {
        this.notas = notas;
    }

    public void setPresenca(List<Boolean> presenca) {
        this.presenca = presenca;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Estudante estudante = (Estudante) o;
        return id == estudante.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Estudante{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", idade=" + idade +
                ", notas=" + notas +
                ", presenca=" + presenca +
                '}';
    }
}
