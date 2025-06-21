package MyWeb.JYWeb.exception.custom;

public class BoardNotFoundException extends RuntimeException{

    public BoardNotFoundException(){
        super("게시글이 존재하지 않습니다.");
    }

    public BoardNotFoundException(String e){
        super(e);
    }
}
