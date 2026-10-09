package br.com.conectasaude.loginseguro.controller;

import br.com.conectasaude.loginseguro.dto.CadastroUsuarioForm;
import br.com.conectasaude.loginseguro.service.CadastroUsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final CadastroUsuarioService cadastroUsuarioService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(
            CadastroUsuarioService cadastroUsuarioService,
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository) {
        this.cadastroUsuarioService = cadastroUsuarioService;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        if (!model.containsAttribute("cadastroUsuarioForm")) {
            model.addAttribute("cadastroUsuarioForm", new CadastroUsuarioForm());
        }
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @Valid @ModelAttribute CadastroUsuarioForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "cadastro";
        }

        try {
            cadastroUsuarioService.cadastrar(form);
        } catch (IllegalArgumentException erro) {
            bindingResult.rejectValue("email", "usuario.email.duplicado", erro.getMessage());
            return "cadastro";
        }

        if ("ALUNO".equals(form.getPerfil())) {
            String email = form.getEmail().trim().toLowerCase();
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(email, form.getSenha()));
            request.getSession(true);
            request.changeSessionId();
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
            return "redirect:/home";
        }

        redirectAttributes.addFlashAttribute("sucesso", "Conta criada com sucesso. Entre com seu e-mail e senha.");
        return "redirect:/login";
    }
}
