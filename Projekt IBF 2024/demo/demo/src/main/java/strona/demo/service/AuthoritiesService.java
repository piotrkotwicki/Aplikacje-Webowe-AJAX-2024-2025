package strona.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import strona.demo.model.Authorities;
import strona.demo.repository.AuthoritiesRepository;
import strona.demo.repository.UsersRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthoritiesService {
    private static final int PAGE_SIZE = 3;
    private final AuthoritiesRepository authoritiesRepository;
    private final UsersRepository usersRepository;

    public Long countUsers() {
        return authoritiesRepository.count();
    }
    public void delUsers(long userId) {
        usersRepository.deleteById(userId);
    }
    public List<Authorities> getUsers(int page) {
        return authoritiesRepository.findAllUsers(PageRequest.of(page, PAGE_SIZE));
    }
    public Authorities getAuthorityByUsername(String username) {
        List<Authorities> authoritiesList = authoritiesRepository.findByUsername(username);
        if (authoritiesList.isEmpty()) {
            throw new EntityNotFoundException("User not found with username: " + username);
        }
        return authoritiesList.get(0);
    }
}
