package com.cinemaapp.controllers;

import com.cinemaapp.models.Filme;
import com.cinemaapp.models.Usuario;
import com.cinemaapp.service.FilmeService;
import com.cinemaapp.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/filmes")
public class FilmeController {

    private final FilmeService filmeService;
    private final UsuarioService usuarioService;

    public FilmeController(FilmeService filmeService, UsuarioService usuarioService) {
        this.filmeService = filmeService;
        this.usuarioService = usuarioService;
    }

    // Método para obter o usuário autenticado
    private Usuario getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioService.findByEmail(email).orElse(null);
    }

    // Listar todos os filmes
    @GetMapping
    public String listarFilmes(Model model) {
        Usuario usuario = getAuthenticatedUser();
        model.addAttribute("user", usuario);
        List<Filme> filmes = filmeService.findAll();
        model.addAttribute("filmes", filmes);
        return "filmes/list";
    }

    // Formulário para novo filme
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String mostrarFormularioNovoFilme(Model model) {
        Usuario usuario = getAuthenticatedUser();
        model.addAttribute("user", usuario);
        model.addAttribute("filme", new Filme());
        return "filmes/form";
    }

    // Salvar novo filme
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/novo")
    public String salvarFilme(@ModelAttribute Filme filme, @RequestParam("file") MultipartFile file, @RequestParam("preco") String precoStr) throws IOException {
        BigDecimal preco = convertToBigDecimal(precoStr);
        filme.setPreco(preco);

        if (!file.isEmpty()) {
            String nomeArquivo = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path caminho = Paths.get("src/main/resources/static/uploads/" + nomeArquivo);
            Files.createDirectories(caminho.getParent());
            file.transferTo(caminho);
            filme.setImagem(nomeArquivo);
        }

        filmeService.save(filme);
        return "redirect:/filmes";
    }

    // Formulário de edição de filme
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicao(@PathVariable Long id, Model model) {
        Usuario usuario = getAuthenticatedUser();
        model.addAttribute("user", usuario);
        Filme filme = filmeService.findById(id).orElse(null);
        if (filme == null) {
            model.addAttribute("error", "Filme não encontrado para edição!");
            return "redirect:/filmes";
        }
        model.addAttribute("filme", filme);
        return "filmes/form";
    }

    // Atualizar filme existente
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/editar/{id}")
    public String atualizarFilme(@PathVariable Long id, @ModelAttribute Filme filmeAtualizado, @RequestParam("file") MultipartFile file, @RequestParam("preco") String precoStr) throws IOException {
        Filme filmeExistente = filmeService.findById(id).orElse(null);
        if (filmeExistente == null) return "redirect:/filmes";

        BigDecimal preco = convertToBigDecimal(precoStr);
        filmeExistente.setPreco(preco);
        filmeExistente.setTitulo(filmeAtualizado.getTitulo());
        filmeExistente.setDescricao(filmeAtualizado.getDescricao());
        filmeExistente.setHorario(filmeAtualizado.getHorario());
        filmeExistente.setDuracao(filmeAtualizado.getDuracao());
        filmeExistente.setClassificacao(filmeAtualizado.getClassificacao());

        if (!file.isEmpty()) {
            String nomeArquivo = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path caminho = Paths.get("src", "main", "resources", "static", "uploads", nomeArquivo);
            Files.createDirectories(caminho.getParent());
            file.transferTo(caminho);
            filmeExistente.setImagem(nomeArquivo);
        }

        filmeService.save(filmeExistente);
        return "redirect:/filmes";
    }

    // Deletar filme
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/deletar")
    public String deletarFilme(@PathVariable Long id) {
        filmeService.deleteById(id);
        return "redirect:/filmes";
    }

    // Detalhes do filme
    @GetMapping("/{id}")
    public String detalhesFilme(@PathVariable Long id, Model model) {
        Usuario usuario = getAuthenticatedUser();
        model.addAttribute("user", usuario);
        Filme filme = filmeService.findById(id).orElse(null);
        if (filme == null) {
            model.addAttribute("error", "Filme não encontrado!");
            return "redirect:/filmes";
        }
        model.addAttribute("filme", filme);
        return "filmes/detalhes";
    }

    // Método auxiliar para converter para BigDecimal
    private BigDecimal convertToBigDecimal(String value) {
        try {
            return new BigDecimal(value.replace(",", "."));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
