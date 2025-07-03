package MyWeb.JYWeb.controller;

import MyWeb.JYWeb.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Operation(
            summary = "S3 파일 업로드",
            description = "게시글의 첨부파일을 S3에 업로드합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "업로드 성공")
            }
    )
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 없음");
        }

        String fileUrl = fileService.upload(file, accessToken);
        return ResponseEntity.ok(fileUrl);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName, HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 없음");
        }


        fileService.deleteFile(fileName, accessToken);

        return ResponseEntity.ok("삭제 완료");
    }

    @GetMapping("/get/{fileName}")
    public ResponseEntity<String> getFile(@PathVariable String fileName) {


        String presignerUrl = fileService.getPresignedUrl(fileName);

        return ResponseEntity.ok(presignerUrl);
    }
}
