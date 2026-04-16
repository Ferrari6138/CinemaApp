package com.cinemaapp.controllers;

import com.cinemaapp.models.Filme;
import com.cinemaapp.models.Genero;
import com.cinemaapp.models.Usuario;
import com.cinemaapp.repository.GeneroRepository;
import com.cinemaapp.service.FilmeService;
import com.cinemaapp.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final GeneroRepository generoRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public FilmeController(FilmeService filmeService, UsuarioService usuarioService, GeneroRepository generoRepository) {
        this.filmeService = filmeService;
        this.usuarioService = usuarioService;
        this.generoRepository = generoRepository;
    }

    private Usuario getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioService.findByEmail(auth.getName()).orElse(null);
    }

    @GetMapping
    public String listarFilmes(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long generoId,
            Model model) {
        Usuario usuario = getAuthenticatedUser();
        model.addAttribute("user", usuario);

        List<Filme> filmes;
        if (generoId != null) {
            filmes = filmeService.findByGenero(generoId);
        } else if (q != null && !q.isBlank()) {
            filmes = filmeService.search(q);
        } else {
            filmes = filmeService.findAll();
        }

        model.addAttribute("filmes", filmes);
        model.addAttribute("generos", generoRepository.findAll());
        model.addAttribute("q", q);
        model.addAttribute("generoId", generoId);
        return "filmes/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String mostrarFormularioNovoFilme(Model model) {
        model.addAttribute("user", getAuthenticatedUser());
        model.addAttribute("filme", new Filme());
        model.addAttribute("generos", generoRepository.findAll());
        return "filmes/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/novo")
    public String salvarFilme(
            @ModelAttribute Filme filme,
            @RequestParam("file") MultipartFile file,
            @RequestParam("preco") String precoStr,
            @RequestParam(value = "generoIds", required = false) List<Long> generoIds,
            RedirectAttributes ra) throws IOException {

        filme.setPreco(parseBigDecimal(precoStr));

        if (!file.isEmpty()) {
            String nomeArquivo = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path caminho = Paths.get(uploadDir + nomeArquivo);
            Files.createDirectories(caminho.getParent());
            file.transferTo(caminho);
            filme.setImagem(nomeArquivo);
        }

        if (generoIds != null) {
            filme.setGeneros(generoRepository.findAllById(generoIds));
        }

        filmeService.save(filme);
        ra.addFlashAttribute("success", "Filme cadastrado com sucesso!");
        return "redirect:/filmes";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicao(@PathVariable Long id, Model model) {
        model.addAttribute("user", getAuthenticatedUser());
        Filme filme = filmeService.findById(id).orElse(null);
        if (filme == null) return "redirect:/filmes";
        model.addAttribute("filme", filme);
        model.addAttribute("generos", generoRepository.findAll());
        return "filmes/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/editar/{id}")
    public String atualizarFilme(
            @PathVariable Long id,
            @ModelAttribute Filme filmeAtualizado,
            @RequestParam("file") MultipartFile file,
            @RequestParam("preco") String precoStr,
            @RequestParam(value = "generoIds", required = false) List<Long> generoIds,
            RedirectAttributes ra) throws IOException {

        Filme existente = filmeService.findById(id).orElse(null);
        if (existente == null) return "redirect:/filmes";

        existente.setTitulo(filmeAtualizado.getTitulo());
        existente.setDescricao(filmeAtualizado.getDescricao());
        existente.setAno(filmeAtualizado.getAno());
        existente.setDuracao(filmeAtualizado.getDuracao());
        existente.setClassificacao(filmeAtualizado.getClassificacao());
        existente.setPreco(parseBigDecimal(precoStr));

        if (!file.isEmpty()) {
            String nomeArquivo = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path caminho = Paths.get(uploadDir + nomeArquivo);
            Files.createDirectories(caminho.getParent());
            file.transferTo(caminho);
            existente.setImagem(nomeArquivo);
        }

        if (generoIds != null) {
            existente.setGeneros(generoRepository.findAllById(generoIds));
        } else {
            existente.setGeneros(List.of());
        }

        filmeService.save(existente);
        ra.addFlashAttribute("success", "Filme atualizado com sucesso!");
        return "redirect:/filmes/" + id;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/deletar")
    public String deletarFilme(@PathVariable Long id, RedirectAttributes ra) {
        filmeService.deleteById(id);
        ra.addFlashAttribute("success", "Filme removido.");
        return "redirect:/filmes";
    }

    @GetMapping("/{id}")
    public String detalhesFilme(@PathVariable Long id, Model model) {
        Filme filme = filmeService.findById(id).orElse(null);
        if (filme == null) return "redirect:/filmes";
        model.addAttribute("user", getAuthenticatedUser());
        model.addAttribute("filme", filme);
        return "filmes/detalhes";
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            return new BigDecimal(value.replace(",", "."));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
