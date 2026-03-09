package se.lexicon.g59springbootdatajpa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ProblemDetail handelDataNotFoundException(DataNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // IllegalArgumentException
    // DuplicateEntryException
    // MethodArgumentNotValidException

    // GLobal Exception Handler (Exception)
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalExceptions(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error - contact support"
        );
        problemDetail.setProperty("timestamp", Instant.now());
        ex.printStackTrace(); // used for debugging
        return problemDetail;
    }
}
