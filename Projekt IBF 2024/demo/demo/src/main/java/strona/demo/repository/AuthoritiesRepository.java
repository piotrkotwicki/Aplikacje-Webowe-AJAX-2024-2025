package strona.demo.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import strona.demo.model.Authorities;

import java.util.List;

@Repository
public interface AuthoritiesRepository extends JpaRepository<Authorities, Long> {
    @Query("SELECT DISTINCT a FROM Authorities a")
    List<Authorities> findAllUsers(PageRequest pageRequest);
    @Query("SELECT a FROM Authorities a JOIN a.user u WHERE u.username = :username")
    List<Authorities> findByUsername(@Param("username") String username);
}