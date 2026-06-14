package com.gc.projeto.shared.presentation;

import com.gc.projeto.modules.companies.domain.exceptions.CompanyNameAlreadyExistsException;
import com.gc.projeto.modules.companies.domain.exceptions.CompanyNotFoundException;
import com.gc.projeto.shared.domain.exceptions.BusinessException;
import com.gc.projeto.shared.presentation.dtos.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // --- TRATAMENTOS DE VALIDAÇÃO E REQUISIÇÃO (HTTP 400) ---

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        log.warn("[VALIDATION] {} erro(s) de validação -> {}", errors.size(), errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "Requisição inválida. Corrija os campos informados.",
                        Instant.now(),
                        errors
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnreadable(HttpMessageNotReadableException ex) {
        log.warn("[MALFORMED JSON] Corpo da requisição inválido ou ausente.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "Corpo da requisição inválido ou ausente.",
                        Instant.now()
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("[TYPE MISMATCH] Parâmetro '{}' com tipo ou formato inválido.", ex.getName());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "Parâmetro '" + ex.getName() + "' com formato inválido.",
                        Instant.now()
                ));
    }

    // --- TRATAMENTOS DE NEGÓCIO E DOMÍNIO ---

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(CompanyNotFoundException ex) {
        log.warn("[NOT FOUND EXCEPTION] {} -> {}", ex.getClass().getSimpleName(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        Instant.now()
                ));
    }

    @ExceptionHandler(CompanyNameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflictException(CompanyNameAlreadyExistsException ex) {
        log.warn("[CONFLICT EXCEPTION] {} -> {}", ex.getClass().getSimpleName(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage(),
                        Instant.now()
                ));
    }

    /* Fallback para qualquer outra BusinessException que não tenha um handler específico.
       Normalmente mapeado para 400 (Bad Request) ou 422 (Unprocessable Entity). */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessException(BusinessException ex) {
        log.warn("[BUSINESS EXCEPTION] {} -> {}", ex.getClass().getSimpleName(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        Instant.now()
                ));
    }

    // --- TRATAMENTO GENÉRICO DE FALHAS INESPERADAS (HTTP 500) ---

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        log.error("[EXCEPTION] Erro inesperado -> {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Erro interno do servidor.",
                        Instant.now()
                ));
    }
}