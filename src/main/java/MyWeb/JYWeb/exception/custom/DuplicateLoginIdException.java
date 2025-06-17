package MyWeb.JYWeb.exception.custom;

//아이디 중복 예외 처리
public class DuplicateLoginIdException extends RuntimeException{
    public DuplicateLoginIdException() {
        super("이미 사용 중인 아이디입니다.");
    }

}
