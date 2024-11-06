package strona.demo.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import strona.demo.model.Director;

import java.util.List;

public interface DirectorRepository extends JpaRepository<Director, Long> {

    @Query("SELECT DISTINCT d FROM Director d")
    List<Director> findAllDirectors(PageRequest page);

    Director deleteById(long iddirector);

//    List<Director> findByIdMovie(Long idmovie);
}
