package strona.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import strona.demo.model.Director;
import strona.demo.model.Movie;
import strona.demo.model.Users;
import strona.demo.repository.DirectorRepository;
import strona.demo.repository.MovieRepository;
import strona.demo.repository.UsersRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {
    private static final int PAGE_ELEMENTS = 3;
    private final MovieRepository movieRepository;
    private final DirectorRepository  directorRepository;
    private final UsersRepository userRepository;

    public Long countMovies() {
        return movieRepository.count();
    }
    public List<Movie> getMovies(int page) {
        return movieRepository.findAllMovies(
                PageRequest.of(page, PAGE_ELEMENTS));
    }
    public Movie getMovieById(long idmovie) {
        Movie movie = movieRepository.findById(idmovie)
                .orElseThrow();

        return new ResponseEntity<>(movie, HttpStatus.OK).getBody();
    }
    public Movie createMovie( Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie editMovie(Movie movie, long iddirector) {
        Director director = directorRepository.findById(iddirector)
                .orElseThrow(() -> new RuntimeException("Director not found with id " + iddirector));

        movie.setDirector(director);
        return movieRepository.save(movie);
    }

}
