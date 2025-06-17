package MyWeb.JYWeb.exception.custom;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("리프레시 토큰이 유효하지 않습니다.");
    }
}
