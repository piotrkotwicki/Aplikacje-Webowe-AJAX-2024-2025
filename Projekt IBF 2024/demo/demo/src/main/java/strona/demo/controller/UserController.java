package strona.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import strona.demo.model.Users;
import strona.demo.repository.UsersRepository;
import strona.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")

public class UserController {
    private final UserService userService;
    private final UsersRepository usersRepository;

    @GetMapping("users/count")
    public Long countUsers() {
        return userService.countUsers();
    }
    @GetMapping("/users")
    public List<Users> getUsers(@RequestParam(required = false) Integer page) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        return userService.getUsers(pageNumber);
    }
    @GetMapping("/users/{iduser}")
    public Users getUsersById(@PathVariable long iduser) {

        return userService.getUsersById(iduser);
    }
    @PostMapping("/users")
    public Users createUsers(@RequestBody Users user) {
        return userService.createUsers(user);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUsername(@PathVariable Long userId, @RequestBody Map<String, String> requestBody) {
        String newUsername = requestBody.get("newUsername");
        String newAuthority = requestBody.get("newAuthority");
        try {
            usersRepository.updateUsernameAndAuthorities(newAuthority, newUsername, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating username: " + e.getMessage());
        }
    }


    @DeleteMapping("/users/{id}")
    public void deleteUsers (long iduser){
        userService.deleteUsers(iduser);
    }
}
