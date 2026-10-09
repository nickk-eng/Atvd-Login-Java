package br.com.conectasaude.loginseguro.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CadastroUsuarioForm {

    @NotBlank(message = "Informe seu nome.")
    @Size(max = 80, message = "O nome deve ter no maximo 80 caracteres.")
    private String nome;

    @NotBlank(message = "Informe seu e-mail.")
    @Email(message = "Informe um e-mail valido.")
    @Size(max = 150, message = "O e-mail deve ter no maximo 150 caracteres.")
    private String email;

    @NotBlank(message = "Informe uma senha.")
    @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
            message = "A senha precisa ter uma letra maiuscula, um numero e um caractere especial.")
    private String senha;

    @NotBlank(message = "Confirme a senha.")
    private String confirmarSenha;

    @NotBlank(message = "Escolha um perfil.")
    @Pattern(regexp = "ALUNO|PROFESSOR", message = "Escolha aluno ou professor.")
    private String perfil;

    @AssertTrue(message = "E necessario aceitar os termos para criar a conta.")
    private boolean aceitouTermos;

    @AssertTrue(message = "As senhas informadas devem ser iguais.")
    public boolean isSenhasIguais() {
        return senha != null && senha.equals(confirmarSenha);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public boolean isAceitouTermos() {
        return aceitouTermos;
    }

    public void setAceitouTermos(boolean aceitouTermos) {
        this.aceitouTermos = aceitouTermos;
    }
}
