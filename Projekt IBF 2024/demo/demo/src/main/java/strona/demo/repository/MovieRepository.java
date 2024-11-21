package strona.demo.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import strona.demo.model.Director;
import strona.demo.model.Movie;

import javax.transaction.Transactional;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    Movie deleteById(long idmovie);

    @Transactional
    @Modifying
    @Query("DELETE FROM Movie m WHERE m.director.iddirector = :iddirector")
    void deleteMoviesByDirectorId(@Param("iddirector") long iddirector);
}
