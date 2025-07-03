package MyWeb.JYWeb.exception.custom;

public class UploadFileNotFoundException extends RuntimeException{

    public UploadFileNotFoundException(){
        super("파일이 존재하지 않습니다.");
    }

    public UploadFileNotFoundException(String e){
        super(e);
    }
}
