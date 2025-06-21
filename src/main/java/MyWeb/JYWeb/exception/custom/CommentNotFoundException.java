package MyWeb.JYWeb.exception.custom;

public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException(){
        super("댓글이 존재하지 않습니다.");
    }
    public CommentNotFoundException(String e){
        super(e);
    }
}
