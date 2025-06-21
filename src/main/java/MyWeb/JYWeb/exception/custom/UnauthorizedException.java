package MyWeb.JYWeb.exception.custom;

//토큰 인증 실패
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("리프레시 토큰이 유효하지 않습니다.");
    }

    public UnauthorizedException(String e) {
        super(e);
    }
}
