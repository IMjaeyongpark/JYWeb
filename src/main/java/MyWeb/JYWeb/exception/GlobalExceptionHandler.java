package MyWeb.JYWeb.exception;

import MyWeb.JYWeb.exception.custom.DuplicateLoginIdException;
import MyWeb.JYWeb.exception.custom.UnauthorizedException;
import MyWeb.JYWeb.exception.custom.ValidateLoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {



    //아이디 중복
    @ExceptionHandler(DuplicateLoginIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateLoginId(DuplicateLoginIdException e) {

        log.warn("회원가입 실패: {}", e.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                e.getMessage()
        );


        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    //로그인 실패
    @ExceptionHandler(ValidateLoginException.class)
    public ResponseEntity<ErrorResponse> handleValidateLoginException(ValidateLoginException e){

        log.warn("로그인 실패: {}", e.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e){

        log.warn("리프레시 토큰 인증 실패: {}", e.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

}
