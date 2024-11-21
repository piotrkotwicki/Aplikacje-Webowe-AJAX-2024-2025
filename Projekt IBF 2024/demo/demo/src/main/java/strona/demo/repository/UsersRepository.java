package strona.demo.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import strona.demo.model.Users;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    @Query("SELECT DISTINCT u FROM Users u")
    List<Users> findAllUsers(PageRequest pageRequest);

    Optional<Users> findByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users u JOIN authorities a ON u.username = a.username SET u.username = :newUsername, a.authority = :newAuthority WHERE u.id = :userId", nativeQuery = true)
    void updateUsernameAndAuthorities(@Param("newAuthority") String newAuthority, @Param("newUsername") String newUsername, @Param("userId") Long userId);
}