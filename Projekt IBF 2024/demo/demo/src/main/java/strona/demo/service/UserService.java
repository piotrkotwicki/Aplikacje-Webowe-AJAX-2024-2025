package strona.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import strona.demo.model.Users;
import strona.demo.repository.UsersRepository;

import java.util.List;

@Service
@RequiredArgsConstructor

public class UserService {
    private static final int PAGE_ELEMENTS = 3;
    private final UsersRepository userRepository;

    public Long countUsers() {
        return userRepository.count();
    }
    public List<Users> getUsers(int page) {
        return userRepository.findAllUsers(
                PageRequest.of(page, PAGE_ELEMENTS));
    }
    public Users getUsersById( long iduser) {
        Users user = userRepository.findById(iduser)
                .orElseThrow();

        return new ResponseEntity<>(user, HttpStatus.OK).getBody();
    }
    public Users createUsers( Users user) {
        return userRepository.save(user);
    }

    public void deleteUsers(long iduser){
        userRepository.deleteById(iduser);
    }
}
