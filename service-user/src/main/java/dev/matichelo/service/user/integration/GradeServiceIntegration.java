package dev.matichelo.service.user.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import dev.matichelo.service.user.entity.Grade;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeServiceIntegration {
    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "gradeServiceBreaker", fallbackMethod = "fallbackGetGrades")
    public List<Grade> getGradesForUser(String userId) {
        log.info("Llamando al servicio de calificaciones para el usuario: {}", userId);
        
        Grade[] grades = restTemplate.getForObject(
                "http://service-grade/api/v1/grades/users/" + userId, 
                Grade[].class
        );
        
        return grades != null ? Arrays.asList(grades) : new ArrayList<>();
    }

    // El método Fallback DEBE tener LA MISMA FIRMA y un parámetro extra para la Excepción
    public List<Grade> fallbackGetGrades(String userId, Throwable ex) {
        log.error("Fallo al obtener calificaciones para el usuario {}. Retornando lista vacía. Error: {}", userId, ex.getMessage());
        return new ArrayList<>();
    }
}
