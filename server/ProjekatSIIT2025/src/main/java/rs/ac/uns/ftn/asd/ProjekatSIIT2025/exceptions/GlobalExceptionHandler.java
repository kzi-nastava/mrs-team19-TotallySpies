package rs.ac.uns.ftn.asd.ProjekatSIIT2025.exceptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //standardizes how errors look in HTTP responses
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handle(ResponseStatusException ex) {
        Map<String, Object> body = Map.of(
                "message", ex.getReason()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

}
