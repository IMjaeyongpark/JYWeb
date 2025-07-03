package MyWeb.JYWeb.service;

import MyWeb.JYWeb.Util.JwtUtil;
import MyWeb.JYWeb.domain.UploadFile;
import MyWeb.JYWeb.exception.custom.UnauthorizedException;
import MyWeb.JYWeb.exception.custom.UploadFileNotFoundException;
import MyWeb.JYWeb.exception.custom.ValidateLoginException;
import MyWeb.JYWeb.repository.UploadFileRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class FileService {
    private final S3Client s3Client;

    private final UploadFileRepository uploadFileRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public FileService(S3Client s3Client, UploadFileRepository uploadFileRepository) {
        this.s3Client = s3Client;
        this.uploadFileRepository = uploadFileRepository;
    }
    @Value("${jwt.secret}")
    private String secretKey;



    //S3 파일 업로
    public String upload(MultipartFile file, String accessToken) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .acl("public-read")
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }

        return "https://" + bucket + ".s3.amazonaws.com/" + fileName;
    }

    //첨부파일 삭제
    public void deleteFile(String fileName, String accessToken) {

        String loginId = JwtUtil.getLoginId(accessToken, secretKey);


        UploadFile file = uploadFileRepository.findByUploadName(fileName)
                .orElseThrow(() -> new UploadFileNotFoundException());

        if (!file.getBoard().getUser().getLoginId().equals(loginId)) {
            throw new UnauthorizedException("삭제 권한이 없습니다.");
        }

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteRequest);
    }


    public String getPresignedUrl(String fileName) {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.AP_NORTHEAST_2)
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(getObjectRequest)
                    .build();

            return presigner.presignGetObject(presignRequest).url().toString();
        }
    }

}
