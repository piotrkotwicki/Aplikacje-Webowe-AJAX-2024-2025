package strona.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import strona.demo.controller.dto.MovieDto;
import strona.demo.model.Director;
import strona.demo.model.Movie;
import strona.demo.model.Users;
import strona.demo.repository.DirectorRepository;
import strona.demo.repository.MovieRepository;
import strona.demo.repository.UsersRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final DirectorRepository  directorRepository;
    private final UsersRepository userRepository;

    public Long countMovies() {
        return movieRepository.count();
    }
    public List<MovieDto> getMovies(int page, int size) {
        return movieRepository.findAll(
                        PageRequest.of(page, size)
                ).stream()
                .map(movie -> {
                    MovieDto dto = new MovieDto();
                    dto.setIdmovie(movie.getIdmovie());
                    dto.setTitle(movie.getTitle());
                    dto.setGenre(movie.getGenre());
                    dto.setPremieredate(movie.getPremieredate());
                    dto.setPosterLocation(movie.getPosterLocation());
                    dto.setUsername(movie.getUser().getUsername());
                    dto.setDirectorName(movie.getDirector().getName() + " " + movie.getDirector().getSurname());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Movie getMovieById(long idmovie) {
        Movie movie = movieRepository.findById(idmovie)
                .orElseThrow();

        return new ResponseEntity<>(movie, HttpStatus.OK).getBody();
    }

    public MovieDto createMovie(String title, String genre, Date premieredate, String posterLocation, Long iddir, String username) {
        Director director = directorRepository.findById(iddir)
                .orElseThrow(() -> new RuntimeException("Director not found with ID: " + iddir));

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setPremieredate(premieredate);
        movie.setPosterLocation(posterLocation);
        movie.setDirector(director);
        movie.setUser(user);

        Movie savedMovie = movieRepository.save(movie);

        return mapToDto(savedMovie);
    }

    private MovieDto mapToDto(Movie movie) {
        MovieDto dto = new MovieDto();
        dto.setIdmovie(movie.getIdmovie());
        dto.setTitle(movie.getTitle());
        dto.setGenre(movie.getGenre());
        dto.setPremieredate(movie.getPremieredate());
        dto.setPosterLocation(movie.getPosterLocation());
        dto.setUsername(movie.getUser().getUsername());
        dto.setDirectorName(movie.getDirector().getName() + " " + movie.getDirector().getSurname());
        return dto;
    }

    public Movie editMovie(long idmovie, String title, String genre, Date premieredate, long iddir, String username) {
        Movie movie = movieRepository.findById(idmovie)
                .orElseThrow(() -> new RuntimeException("Movie not found with id " + idmovie));

        Director director = directorRepository.findById(iddir)
                .orElseThrow(() -> new RuntimeException("Director not found with id " + iddir));

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username " + username));

        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setPremieredate(premieredate);
        movie.setDirector(director);
        movie.setUser(user);

        return movieRepository.save(movie);
    }


}
