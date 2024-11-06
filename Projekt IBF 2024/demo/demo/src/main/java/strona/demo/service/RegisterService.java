package strona.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import strona.demo.service.Interface.RegisterInterface;

@Service
@RequiredArgsConstructor
public class RegisterService implements RegisterInterface {

    private final UserDetailsManager userDetailsManager;

    @Override
    public void register(String username, String password) {
        userDetailsManager.createUser(User.withUsername(username)
                .password("{bcrypt}" + new BCryptPasswordEncoder().encode(password))
                .authorities("USER")
                .build());
    }
}
