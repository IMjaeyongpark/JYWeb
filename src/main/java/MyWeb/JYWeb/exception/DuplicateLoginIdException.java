package MyWeb.JYWeb.exception;

public class DuplicateLoginIdException extends RuntimeException{
    public DuplicateLoginIdException() {
        super("이미 사용 중인 아이디입니다.");
    }

    public DuplicateLoginIdException(String message) {
        super(message);
    }
}
