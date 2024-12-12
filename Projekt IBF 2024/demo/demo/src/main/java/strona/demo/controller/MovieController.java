package strona.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import strona.demo.controller.dto.MovieDto;
import strona.demo.model.Director;
import strona.demo.model.Movie;
import strona.demo.model.Users;
import strona.demo.repository.DirectorRepository;
import strona.demo.repository.MovieRepository;
import strona.demo.repository.UsersRepository;
import strona.demo.service.MovieService;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MovieController {
    private final MovieService movieService;
    private final MovieRepository movieRepository;

    @GetMapping("movies/count")
    public Long countMovies() {
        return movieService.countMovies();
    }

    @GetMapping("/movies")
    public List<MovieDto> getMovies(@RequestParam(required = false) Integer page) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        return movieService.getMovies(pageNumber);
    }

    @GetMapping("/movies/{idmovie}")
    public Movie getMovieById(@PathVariable long idmovie) {

        return movieService.getMovieById(idmovie);
    }

    @PostMapping("/movies")
    public ResponseEntity<MovieDto> createMovie(@RequestBody Map<String, Object> movieData) {
        String username = (String) movieData.get("username");
        Long iddir = Long.valueOf((Integer) movieData.get("iddir"));

        MovieDto createdMovie = movieService.createMovie(
                (String) movieData.get("title"),
                (String) movieData.get("genre"),
                Date.valueOf((String) movieData.get("premieredate")),
                (String) movieData.get("posterLocation"),
                iddir,
                username
        );

        return ResponseEntity.ok(createdMovie);
    }

    @PutMapping("/movies/{idmovie}")
    public ResponseEntity<Movie> updateMovie(
            @PathVariable long idmovie,
            @RequestBody Map<String, Object> movieData) {

        String title = (String) movieData.get("title");
        String genre = (String) movieData.get("genre");
        Date premieredate = Date.valueOf((String) movieData.get("premieredate"));
        Integer iddir = (int) movieData.get("iddir");
        System.out.println(iddir);
        String username = (String) movieData.get("username");

        Movie updatedMovie = movieService.editMovie(idmovie, title, genre, premieredate, iddir, username);

        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/movies/{idmovie}")
    public ResponseEntity<HttpStatus> deleteDirector(@PathVariable("idmovie") long idmovie) {
        movieRepository.deleteById(idmovie);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
