package br.com.conectasaude.loginseguro.model;

public enum Role {
    ALUNO("Aluno"),
    PROFESSOR("Professor"),
    ADMIN("Administrador");

    private final String descricao;

    Role(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
