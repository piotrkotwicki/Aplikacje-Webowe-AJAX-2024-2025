package strona.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import strona.demo.config.RegisterCredentials;
import strona.demo.service.RegisterService;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping("/register")
    public void register(@RequestBody RegisterCredentials credentials) {
        registerService.register(credentials.getUsername(), credentials.getPassword());
    }
}
