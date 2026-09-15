package com.cinemaapp.config;

import com.cinemaapp.models.*;
import com.cinemaapp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository,
                                   GeneroRepository generoRepository,
                                   FilmeRepository filmeRepository,
                                   SessaoRepository sessaoRepository,
                                   ReservaRepository reservaRepository,
                                   CinemaRepository cinemaRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            // Criar os cinemas (duas unidades da mesma rede na cidade)
            if (cinemaRepository.count() == 0) {
                cinemaRepository.saveAll(List.of(
                        new Cinema("Shopping Campinas 1", "Shopping Campinas 1"),
                        new Cinema("Shopping Campinas 2", "Shopping Campinas 2")
                ));
                System.out.println("Cinemas criados: Shopping Campinas 1, Shopping Campinas 2");
            }

            // Backfill: sessões criadas antes do campo cinema existir ficam sem cinema;
            // associa todas à primeira unidade para manter a exibição consistente.
            List<Sessao> semCinema = sessaoRepository.findByCinemaIsNull();
            if (!semCinema.isEmpty()) {
                Cinema cinemaPadrao = cinemaRepository.findAll().stream().findFirst().orElse(null);
                if (cinemaPadrao != null) {
                    for (Sessao s : semCinema) {
                        s.setCinema(cinemaPadrao);
                    }
                    sessaoRepository.saveAll(semCinema);
                    System.out.println(semCinema.size() + " sessão(ões) antiga(s) associada(s) a " + cinemaPadrao.getNome());
                }
            }

            // Criar usuários iniciais
            if (usuarioRepository.count() == 0) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador");
                admin.setEmail("admin@cinema.com");
                admin.setSenha(passwordEncoder.encode("Admin123"));
                admin.setRole("ADMIN");

                Usuario cliente = new Usuario();
                cliente.setNome("Cliente Teste");
                cliente.setEmail("cliente@cinema.com");
                cliente.setSenha(passwordEncoder.encode("Cliente123"));
                cliente.setRole("USER");

                usuarioRepository.saveAll(List.of(admin, cliente));
                System.out.println("Usuários criados: admin@cinema.com / Admin123");
            }

            // Criar gêneros padrão
            List<String> generosPadrao = Arrays.asList(
                "Ação", "Aventura", "Animação", "Comédia", "Drama",
                "Fantasia", "Ficção Científica", "Horror", "Romance",
                "Suspense", "Terror", "Thriller", "Documentário"
            );
            for (String nome : generosPadrao) {
                if (!generoRepository.existsByNome(nome)) {
                    generoRepository.save(new Genero(nome));
                }
            }

            // Criar filmes de teste
            if (filmeRepository.count() == 0) {
                Genero acao       = generoRepository.findByNome("Ação").orElseThrow();
                Genero aventura   = generoRepository.findByNome("Aventura").orElseThrow();
                Genero ficcao     = generoRepository.findByNome("Ficção Científica").orElseThrow();
                Genero drama      = generoRepository.findByNome("Drama").orElseThrow();
                Genero comedia    = generoRepository.findByNome("Comédia").orElseThrow();
                Genero thriller   = generoRepository.findByNome("Thriller").orElseThrow();
                Genero animacao   = generoRepository.findByNome("Animação").orElseThrow();
                Genero terror     = generoRepository.findByNome("Terror").orElseThrow();
                Genero romance    = generoRepository.findByNome("Romance").orElseThrow();

                Filme f1 = new Filme();
                f1.setTitulo("Duna: Parte Dois");
                f1.setDescricao("Paul Atreides se une aos Fremen para vingar a destruição de sua família e impedir um futuro que apenas ele pode prever.");
                f1.setAno(2024);
                f1.setDuracao(166);
                f1.setClassificacao("14");
                f1.setImagem("https://image.tmdb.org/t/p/w500/czembW0Rk1Ke7lCJGahbOhdCuhV.jpg");
                f1.setPreco(new BigDecimal("32.00"));
                f1.setGeneros(List.of(acao, aventura, ficcao));

                Filme f2 = new Filme();
                f2.setTitulo("Oppenheimer");
                f2.setDescricao("A história do físico J. Robert Oppenheimer e seu papel no desenvolvimento da bomba atômica durante a Segunda Guerra Mundial.");
                f2.setAno(2023);
                f2.setDuracao(180);
                f2.setClassificacao("14");
                f2.setImagem("https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg");
                f2.setPreco(new BigDecimal("28.00"));
                f2.setGeneros(List.of(drama, thriller));

                Filme f3 = new Filme();
                f3.setTitulo("Guardiões da Galáxia Vol. 3");
                f3.setDescricao("Os Guardiões embarcam em uma missão para proteger Rocket e descobrir seu passado misterioso.");
                f3.setAno(2023);
                f3.setDuracao(150);
                f3.setClassificacao("12");
                f3.setImagem("https://image.tmdb.org/t/p/w500/r2J02Z2OpNTctfOSN1Ydgii51I3.jpg");
                f3.setPreco(new BigDecimal("30.00"));
                f3.setGeneros(List.of(acao, aventura, comedia));

                Filme f4 = new Filme();
                f4.setTitulo("Elementos");
                f4.setDescricao("Em uma cidade onde fogo, água, terra e ar convivem, Faísca e Gota descobrem algo surpreendente em comum.");
                f4.setAno(2023);
                f4.setDuracao(101);
                f4.setClassificacao("L");
                f4.setImagem("https://image.tmdb.org/t/p/w500/4Y1WNkd88JXmGfhtWR7dmDAo1T2.jpg");
                f4.setPreco(new BigDecimal("25.00"));
                f4.setGeneros(List.of(animacao, romance, aventura));

                Filme f5 = new Filme();
                f5.setTitulo("Alien: Romulus");
                f5.setDescricao("Um grupo de jovens colonizadores do espaço se depara com a forma de vida mais aterrorizante do universo.");
                f5.setAno(2024);
                f5.setDuracao(119);
                f5.setClassificacao("16");
                f5.setImagem("https://image.tmdb.org/t/p/w500/b33nnKl1GSFbao4l3fZDDqsMx0F.jpg");
                f5.setPreco(new BigDecimal("32.00"));
                f5.setGeneros(List.of(terror, ficcao, thriller));

                Filme f6 = new Filme();
                f6.setTitulo("Deadpool & Wolverine");
                f6.setDescricao("Deadpool é recrutado pela Autoridade de Variação Temporal e acaba formando uma improvável parceria com Wolverine.");
                f6.setAno(2024);
                f6.setDuracao(127);
                f6.setClassificacao("16");
                f6.setImagem("https://image.tmdb.org/t/p/w500/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg");
                f6.setPreco(new BigDecimal("35.00"));
                f6.setGeneros(List.of(acao, comedia));

                Filme f7 = new Filme();
                f7.setTitulo("Pobres Criaturas");
                f7.setDescricao("Bella Baxter é trazida de volta à vida por um cientista excêntrico e foge com um advogado libertino para aventuras pelo mundo.");
                f7.setAno(2023);
                f7.setDuracao(141);
                f7.setClassificacao("18");
                f7.setImagem("https://image.tmdb.org/t/p/w500/kCGlIMHnOm8JPXNbM7HL1ZnIQ8s.jpg");
                f7.setPreco(new BigDecimal("28.00"));
                f7.setGeneros(List.of(drama, romance, comedia));

                Filme f8 = new Filme();
                f8.setTitulo("Furiosa: Uma Saga Mad Max");
                f8.setDescricao("A origem da guerreira Furiosa antes de seu encontro com Max Rockatansky no apocalipse.");
                f8.setAno(2024);
                f8.setDuracao(148);
                f8.setClassificacao("16");
                f8.setImagem("https://image.tmdb.org/t/p/w500/iADOJ8Zymht2JPMoy3R7xceZprc.jpg");
                f8.setPreco(new BigDecimal("32.00"));
                f8.setGeneros(List.of(acao, aventura));

                filmeRepository.saveAll(List.of(f1, f2, f3, f4, f5, f6, f7, f8));
                System.out.println("Filmes criados: 8 filmes de teste");

                // Criar sessões para cada filme, distribuídas entre as duas unidades
                LocalDateTime base = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);
                Cinema campinas1 = cinemaRepository.findByNome("Shopping Campinas 1").orElseThrow();
                Cinema campinas2 = cinemaRepository.findByNome("Shopping Campinas 2").orElseThrow();

                List<Sessao> sessoes = List.of(
                    sessao(f1, campinas1, base.withHour(14), "Sala 1", 100),
                    sessao(f1, campinas2, base.withHour(18), "Sala 1", 100),
                    sessao(f1, campinas1, base.plusDays(1).withHour(20), "Sala 2", 100),

                    sessao(f2, campinas2, base.withHour(15), "Sala 2", 100),
                    sessao(f2, campinas1, base.withHour(19), "Sala 3", 80),
                    sessao(f2, campinas2, base.plusDays(2).withHour(17), "Sala 1", 100),

                    sessao(f3, campinas1, base.withHour(13), "Sala 3", 80),
                    sessao(f3, campinas2, base.withHour(16), "Sala 2", 100),
                    sessao(f3, campinas1, base.plusDays(1).withHour(21), "Sala 1", 100),

                    sessao(f4, campinas2, base.withHour(11), "Sala 4", 100),
                    sessao(f4, campinas1, base.withHour(15), "Sala 4", 100),
                    sessao(f4, campinas2, base.plusDays(1).withHour(13), "Sala 4", 100),

                    sessao(f5, campinas1, base.withHour(20), "Sala 1", 100),
                    sessao(f5, campinas2, base.withHour(22), "Sala 3", 80),
                    sessao(f5, campinas1, base.plusDays(2).withHour(21), "Sala 2", 100),

                    sessao(f6, campinas2, base.withHour(14), "Sala 2", 100),
                    sessao(f6, campinas1, base.withHour(17), "Sala 1", 100),
                    sessao(f6, campinas2, base.plusDays(1).withHour(19), "Sala 3", 80),

                    sessao(f7, campinas1, base.withHour(16), "Sala 3", 80),
                    sessao(f7, campinas2, base.plusDays(1).withHour(18), "Sala 2", 100),

                    sessao(f8, campinas1, base.withHour(18), "Sala 1", 100),
                    sessao(f8, campinas2, base.withHour(21), "Sala 2", 100),
                    sessao(f8, campinas1, base.plusDays(2).withHour(20), "Sala 3", 80)
                );

                sessaoRepository.saveAll(sessoes);
                System.out.println("Sessões criadas: " + sessoes.size() + " sessões de teste");

                // Criar reservas de teste para o cliente
                Usuario cliente = usuarioRepository.findByEmail("cliente@cinema.com").orElseThrow();

                Reserva r1 = new Reserva();
                r1.setUsuario(cliente);
                r1.setSessao(sessoes.get(0));
                r1.setQuantidade(2);
                r1.setAssentos("A1,A2");
                r1.setStatus("CONFIRMADA");
                r1.setValorTotal(f1.getPreco().multiply(new BigDecimal("2")));
                r1.setDataReserva(LocalDateTime.now().minusDays(1));

                Reserva r2 = new Reserva();
                r2.setUsuario(cliente);
                r2.setSessao(sessoes.get(3));
                r2.setQuantidade(1);
                r2.setAssentos("C5");
                r2.setStatus("CONFIRMADA");
                r2.setValorTotal(f2.getPreco());
                r2.setDataReserva(LocalDateTime.now().minusHours(3));

                Reserva r3 = new Reserva();
                r3.setUsuario(cliente);
                r3.setSessao(sessoes.get(6));
                r3.setQuantidade(3);
                r3.setAssentos("B2,B3,B4");
                r3.setStatus("CANCELADA");
                r3.setValorTotal(f3.getPreco().multiply(new BigDecimal("3")));
                r3.setDataReserva(LocalDateTime.now().minusDays(3));

                reservaRepository.saveAll(List.of(r1, r2, r3));
                System.out.println("Reservas criadas: 3 reservas de teste");
            }
        };
    }

    private Sessao sessao(Filme filme, Cinema cinema, LocalDateTime dataHora, String sala, int capacidade) {
        Sessao s = new Sessao();
        s.setFilme(filme);
        s.setCinema(cinema);
        s.setDataHora(dataHora);
        s.setSala(sala);
        s.setCapacidade(capacidade);
        s.setAtiva(true);
        return s;
    }
}
