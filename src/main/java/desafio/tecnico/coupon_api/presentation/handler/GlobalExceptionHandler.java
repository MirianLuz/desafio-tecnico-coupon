package desafio.tecnico.coupon_api.presentation.handler;

import desafio.tecnico.coupon_api.domain.exceptions.BusinessException;
import desafio.tecnico.coupon_api.domain.exceptions.CouponAlreadyDeletedException;
import desafio.tecnico.coupon_api.domain.exceptions.CouponNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            CouponNotFoundException exception
    ) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        exception.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler({
            BusinessException.class,
            CouponAlreadyDeletedException.class
    })
    public ResponseEntity<ErrorResponse> handleBusiness(
            RuntimeException exception
    ) {

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        exception.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception
    ) {

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        "Dados de entrada inválidos",
                        LocalDateTime.now()
                ));
    }

    public record ErrorResponse(
            String message,
            LocalDateTime timestamp
    ) {
    }
}