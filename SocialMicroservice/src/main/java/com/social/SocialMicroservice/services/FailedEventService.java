package com.social.SocialMicroservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Hay que implementarlo
 *
 * Servicio para manejar eventos que no pudieron ser enviados a Kafka
 * debido a fallos del Circuit Breaker o agotamiento de reintentos
 */
@Service
public class FailedEventService {

    private static final Logger log = LoggerFactory.getLogger(FailedEventService.class);

    /**
     * Guarda un evento fallido para procesamiento posterior
     * Puedes implementar esto con:
     * - Una tabla en base de datos
     * - Redis
     * - Sistema de colas alternativo
     */
    public void saveFailedFollowRequestEvent(Long eventId, Object event, String errorMessage) {
        log.warn("Guardando FollowRequestEvent fallido. ID: {}, Error: {}", eventId, errorMessage);

        // TODO: Implementar persistencia
        // Ejemplo: failedEventRepository.save(new FailedEvent(eventId, event, errorMessage));
    }

    public void saveFailedFollowAnswerEvent(Long eventId, Object event, String errorMessage) {
        log.warn("Guardando FollowAnswerEvent fallido. ID: {}, Error: {}", eventId, errorMessage);

        // TODO: Implementar persistencia
    }

    /**
     * Reintenta enviar eventos fallidos guardados
     * Este método podría ser llamado por un scheduler
     */
    public void retryFailedEvents() {
        log.info("Reintentando eventos fallidos...");

        // TODO: Recuperar eventos de la persistencia y reintentarlos
    }
}
