package strona.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
public class HomeController {

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/home")
    public String getMessage(@ApiIgnore Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "Witaj, ";
        } else {
            throw new AccessDeniedException("Nie jesteś zalogowany!");
        }
    }
}
