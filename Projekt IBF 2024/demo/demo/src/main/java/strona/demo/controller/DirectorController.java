package strona.demo.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import strona.demo.model.Director;
import strona.demo.repository.DirectorRepository;
import strona.demo.repository.MovieRepository;
import strona.demo.repository.UsersRepository;
import strona.demo.service.DirectorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class DirectorController {

    private final DirectorService directorService;
    private final DirectorRepository directorRepository;
    private final MovieRepository movieRepository;

    @GetMapping("directors/count")
    public Long countDirectors() {
        return directorService.countDirectors();
    }

    @GetMapping("/directors")
    public List<Director> getDirectors(@RequestParam(required = false) Integer page) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        return directorService.getDirectors(pageNumber);
    }

    @GetMapping("/directors/all")
    public ResponseEntity<List<Director>> getAllDirectors() {
        List<Director> directors = directorService.getAllDirectors();
        return ResponseEntity.ok(directors);
    }

    @GetMapping("/directors/{iddirector}")
    public Director getDirectorById(@PathVariable long iddirector) {

        return directorService.getDirectorById(iddirector);
    }

    @PostMapping("/directors")
    public Director createDirector(@RequestBody Director director) {
        return directorService.addDirector(director);
    }

    @PutMapping("/directors")
    public Director editDirector(@RequestBody Director director){
        return directorService.editDirector(director);
    }

    @DeleteMapping("/directors/{iddirector}")
    public ResponseEntity<HttpStatus> deleteDirector(@PathVariable("iddirector") long iddirector) {
        directorRepository.deleteById(iddirector);
        movieRepository.deleteMoviesByDirectorId(iddirector);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}



