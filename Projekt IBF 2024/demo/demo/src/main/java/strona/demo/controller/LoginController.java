package strona.demo.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import strona.demo.config.LoginCredentials;
import strona.demo.model.Authorities;
import strona.demo.service.AuthoritiesService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
public class LoginController {
private final AuthoritiesService authoritiesService;

    @PostMapping("/login")
    public void login(@RequestBody LoginCredentials credentials) {
    }
    @DeleteMapping("/authority/{id}")
    public void delUsers(@PathVariable("id") long userid) {
        authoritiesService.delUsers(userid);
    }
    @GetMapping("authority/count")
    public Long countUsers() {
        return authoritiesService.countUsers();
    }
    @GetMapping("/authority")
    public List<Authorities> getAuthority(@RequestParam(required = false) Integer page) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        return authoritiesService.getUsers(pageNumber);
    }
    @GetMapping("/authority/{username}")
    public Authorities getAuthorityByUsername(@PathVariable("username") String username) {
        return authoritiesService.getAuthorityByUsername(username);
    }
}