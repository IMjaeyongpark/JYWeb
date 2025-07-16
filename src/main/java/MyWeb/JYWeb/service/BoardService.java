package MyWeb.JYWeb.service;


import MyWeb.JYWeb.DTO.board.BoardCreateRequest;
import MyWeb.JYWeb.DTO.board.BoardDetailResponse;
import MyWeb.JYWeb.DTO.board.BoardResponse;
import MyWeb.JYWeb.DTO.board.BoardUpdateRequest;
import MyWeb.JYWeb.Util.JwtUtil;
import MyWeb.JYWeb.domain.Board;
import MyWeb.JYWeb.domain.BoardDocument;
import MyWeb.JYWeb.domain.UploadFile;
import MyWeb.JYWeb.domain.User;
import MyWeb.JYWeb.exception.custom.BoardNotFoundException;
import MyWeb.JYWeb.exception.custom.UnauthorizedException;
import MyWeb.JYWeb.exception.custom.ValidateLoginException;
import MyWeb.JYWeb.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import java.time.LocalDateTime;
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final BoardRepository boardRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    private final FileService fileService;
    private final UploadFileRepository uploadFileRepository;

    private final BoardElasticsearchRepository boardEsRepository;


    //게시글 등록
    public Long createBoard(BoardCreateRequest boardCreateRequest, List<MultipartFile> files, String accessToken) {

        //사용자 검증
        String loginId = JwtUtil.getLoginId(accessToken, secretKey);

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ValidateLoginException("사용자 없음"));

        Board board = Board.from(boardCreateRequest, user);

        Board saved = boardRepository.save(board);

        //파일 저장
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String uploadFileName = fileService.upload(file);
                UploadFile uploadFile = new UploadFile(file.getOriginalFilename(), uploadFileName, saved);
                uploadFileRepository.save(uploadFile);
            }
        }

        // ES 인덱싱
        BoardDocument esDoc = BoardDocument.builder()
                .boardId(saved.getBoardId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .build();
        boardEsRepository.save(esDoc);

        log.info("게시글 등록 : {}", user.getUserId());

        return board.getBoardId();
    }


    //게시글 삭제
    public void deleteBoard(Long boardId, String accessToken) {

        //사용자 검증
        String loginId = JwtUtil.getLoginId(accessToken, secretKey);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException());

        if (!board.getUser().getLoginId().equals(loginId)) {
            throw new UnauthorizedException("삭제 권한이 없습니다.");
        }

        //소프트 삭제
        if (board.getDeletedAt() == null) {
            board.setDeletedAt(LocalDateTime.now());
            commentRepository.softDeleteAllByBoard(boardId, LocalDateTime.now());
            boardRepository.save(board);
        }

        boardEsRepository.deleteById(boardId);

        log.info("게시글 삭제 완료 : {}", board.getBoardId());
    }

    //게시물 조회
    public Page<BoardResponse> getBoard(int pageNum, int pageSize, String sort, Sort.Direction dir) {


        Pageable pageable;

        switch (sort) {
            case "viewCount":
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(dir, "viewCount"));
                break;
//            case "popular":
//                pageable = PageRequest.of(pageNum, pageSize, Sort.by(dir, "likeCount"));
//                break;
            default:
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(dir, "createdAt"));
                break;
        }

        //페이지 단위로 게시글 조회
        Page<BoardResponse> boardResponses = boardRepository.findAllByDeletedAtIsNull(pageable);


        return boardResponses;
    }

    //게시물 내용 조회
    public BoardDetailResponse getBoardDetail(Long boardId) {

        // Board 정보만 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("게시물 없음"));

        // 파일 조회
        List<UploadFile> files = uploadFileRepository.findByBoard_BoardId(boardId);

        // 파일 URL 추출
        List<String> fileUrls = files.stream()
                .map(f -> fileService.getPresignedUrl(f.getUploadName()))
                .collect(Collectors.toList());

        // DTO 생성
        BoardDetailResponse boardDetailResponse = new BoardDetailResponse(
                board.getBoardId(), board.getTitle(), board.getContent(),
                board.getUser().getNickname(), board.getUser().getLoginId(),
                board.getViewCount(), board.getCreatedAt(), board.getUpdatedAt(),
                board.getDeletedAt(), fileUrls
        );

        return boardDetailResponse;
    }

    //게시글 조회수 증가
    public void increaseViewCount(Long boardId) {
        int updatedRows = boardRepository.incrementViewCount(boardId);
        if (updatedRows == 0) {
            throw new IllegalStateException("조회수 수정 실패");
        }
    }


    //사용자 게시글 목록 가져오기
    public Page<BoardResponse> getUserBoard(String accessToken, int pageNum, int pageSize) {

        //사용자 검증
        String loginId = JwtUtil.getLoginId(accessToken, secretKey);

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ValidateLoginException("사용자 없음"));

        //사용자 게시글 조회
        Page<BoardResponse> boardResponses = boardRepository.findAllByUserIdAndDeletedAtIsNull(user.getUserId(),
                PageRequest.of(pageNum, pageSize, Sort.by("createdAt").descending()));

        return boardResponses;
    }


    //게시글 수정
    public void updateBoard(BoardUpdateRequest boardUpdateRequest, List<MultipartFile> newFiles, List<String> deleteFileNames, String accessToken) {
        String loginId = JwtUtil.getLoginId(accessToken, secretKey);

        Board board = boardRepository.findById(boardUpdateRequest.getBoardId())
                .orElseThrow(() -> new BoardNotFoundException("게시물 없음"));

        // 권한 확인
        if (!board.getUser().getLoginId().equals(loginId)) {
            throw new UnauthorizedException("수정 권한 없음");
        }

        // 게시글 내용 수정
        board.setTitle(boardUpdateRequest.getTitle());
        board.setContent(boardUpdateRequest.getContent());

        //es 수정
        BoardDocument esDoc = boardEsRepository.findById(board.getBoardId()).get();
        esDoc.setContent(boardUpdateRequest.getTitle());
        esDoc.setTitle(boardUpdateRequest.getContent());

        boardEsRepository.save(esDoc);

        //기존 파일 중 삭제 요청된 파일 삭제
        if (deleteFileNames != null) {
            for (String fileName : deleteFileNames) {
                UploadFile file = uploadFileRepository.findByUploadName(fileName)
                        .orElseThrow(() -> new RuntimeException("파일 없음"));

                fileService.deleteFile(file.getUploadName()); // S3 삭제
                uploadFileRepository.delete(file); // DB 삭제
            }
        }

        //새로운 파일 업로드
        if (newFiles != null && !newFiles.isEmpty()) {
            for (MultipartFile file : newFiles) {
                String storedName = fileService.upload(file);
                UploadFile uploadFile = new UploadFile(file.getOriginalFilename(), storedName, board);
                uploadFileRepository.save(uploadFile);
            }
        }

        log.info("게시글 수정 완료: {}", board.getBoardId());

    }
}
