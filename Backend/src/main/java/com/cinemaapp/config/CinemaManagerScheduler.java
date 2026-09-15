package com.cinemaapp.config;

import com.cinemaapp.service.CinemaManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CinemaManagerScheduler {

    private static final Logger log = LoggerFactory.getLogger(CinemaManagerScheduler.class);

    private final CinemaManagerService cinemaManagerService;

    public CinemaManagerScheduler(CinemaManagerService cinemaManagerService) {
        this.cinemaManagerService = cinemaManagerService;
    }

    // Roda diariamente às 03:00 por padrão; ajustável via CINEMA_SCHEDULER_CRON
    @Scheduled(cron = "${app.cinema.scheduler.cron:0 0 3 * * *}")
    public void executarRotinaDiaria() {
        try {
            cinemaManagerService.executar();
        } catch (Exception e) {
            log.error("Falha ao executar o gestor de cinema automático", e);
        }
    }
}
