package MyWeb.JYWeb.DTO.sporify;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtistResponseDto {
    private String name;
    private String id;
    private String imageUrl;
}
