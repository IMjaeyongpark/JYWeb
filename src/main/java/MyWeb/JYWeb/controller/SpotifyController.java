package MyWeb.JYWeb.controller;

import MyWeb.JYWeb.DTO.sporify.ArtistResponseDto;
import MyWeb.JYWeb.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spotify")
@Slf4j
@RequiredArgsConstructor
public class SpotifyController {

    private final SpotifyService spotifyService;


    @GetMapping("/search")
    public ResponseEntity<ArtistResponseDto> searchArtist(@RequestParam("artist") String artist) {
        return ResponseEntity.ok(spotifyService.searchArtist(artist));
    }
}
