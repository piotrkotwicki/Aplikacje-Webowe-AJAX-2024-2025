package strona.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import strona.demo.model.Director;
import strona.demo.model.Movie;
import strona.demo.repository.DirectorRepository;
import strona.demo.repository.MovieRepository;
import strona.demo.service.DirectorService;
import strona.demo.service.MovieService;

import java.util.List;

@RestController
//@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MovieController {
    private final MovieService movieService;
    private final MovieRepository movieRepository;
    private final DirectorRepository directorRepository;

    @GetMapping("movies/count")
    public Long countMovies() {
        return movieService.countMovies();
    }

    @GetMapping("/movies")
    public List<Movie> getMovies(@RequestParam(required = false) Integer page) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        return movieService.getMovies(pageNumber);
    }

    @GetMapping("/movies/{id}")
    public Movie getMovieById(long idmovie) {

        return movieService.getMovieById(idmovie);
    }

    @PostMapping("/movies/{iddirector}")
    public Movie createMovie(@PathVariable("iddirector") long iddirector, @RequestBody Movie movie) {
        Director director = directorRepository.findById(iddirector).orElse(null);
        movie.setDirector(director);
        return movieService.createMovie(movie);
    }

    @PutMapping("/movies")
    public Movie editMovie(@RequestBody Movie movie, long iddirector){
        return movieService.editMovie(movie, iddirector);
    }

    @DeleteMapping("/movies/{idmovie}")
    public ResponseEntity<HttpStatus> deleteDirector(@PathVariable("idmovie") long idmovie) {
        movieRepository.deleteById(idmovie);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
