package MyWeb.JYWeb.exception.custom;

//아이디 검증 실패 예외 처리
public class ValidateLoginException extends RuntimeException{
    public ValidateLoginException() {
        super("아이디 또는 비밀번호가 다릅니다.");
    }

    public ValidateLoginException(String e) {
        super(e);
    }
}
