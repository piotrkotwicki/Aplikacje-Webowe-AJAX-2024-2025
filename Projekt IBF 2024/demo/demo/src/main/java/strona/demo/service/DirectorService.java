package strona.demo.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import strona.demo.model.Director;
import strona.demo.repository.DirectorRepository;
import strona.demo.repository.UsersRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {


    private static final int PAGE_ELEMENTS = 3;
    private final DirectorRepository  directorRepository;
    private final UsersRepository userRepository;

    public Long countDirectors() {
        return directorRepository.count();
    }
    public List<Director> getDirectors(int page) {
        return directorRepository.findAllDirectors(
                PageRequest.of(page, PAGE_ELEMENTS));
    }
    public Director getDirectorById( long iddirector) {
        Director director = directorRepository.findById(iddirector)
                .orElseThrow();

        return new ResponseEntity<>(director, HttpStatus.OK).getBody();
    }
    public Director addDirector( Director director) {
    return directorRepository.save(director);
    }

    public Director editDirector(Director director){
        return directorRepository.save(director);
    }

}
