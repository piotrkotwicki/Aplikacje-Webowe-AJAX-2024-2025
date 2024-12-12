package strona.demo.controller.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MovieDto {
    private long idmovie;
    private String title;
    private String genre;
    private Date premieredate;
    private String username;
    private String directorName;
    private String posterLocation;
}