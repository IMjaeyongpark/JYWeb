package MyWeb.JYWeb.service;

import MyWeb.JYWeb.DTO.sporify.ArtistResponseDto;
import MyWeb.JYWeb.Util.SpotifyApiUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SpotifyService {
    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    public ArtistResponseDto searchArtist(String artistName) {
        //Access Token 발급
        String token = SpotifyApiUtil.getAccessToken(clientId, clientSecret);

        //Spotify API로 아티스트 검색
        String url = "https://api.spotify.com/v1/search?q=" + artistName + "&type=artist&limit=1";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class);

        try {
            //JSON 파싱 (최상위 artist 1명 정보만 꺼내기)
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            JsonNode artists = root.path("artists").path("items");
            if (artists.isArray() && artists.size() > 0) {
                JsonNode artist = artists.get(0);
                String name = artist.path("name").asText();
                String id = artist.path("id").asText();
                String imageUrl = "";
                JsonNode images = artist.path("images");
                if (images.isArray() && images.size() > 0) {
                    imageUrl = images.get(0).path("url").asText();
                }
                return new ArtistResponseDto(name, id, imageUrl);
            } else {
                // 검색 결과 없음 처리
                return new ArtistResponseDto("Not Found", "", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArtistResponseDto("Error", "", "");
        }
    }
}
