package br.com.joaopedroafluz.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping()
    public ResponseEntity create(@RequestBody UserModel user) {
        var registeredUser = this.userRepository.findByUsername(user.getUsername());

        if (registeredUser != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username já está em uso");
        }

        String hashedPassword = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());

        user.setPassword(hashedPassword);

        var userRegistered = this.userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userRegistered);
    }

}
