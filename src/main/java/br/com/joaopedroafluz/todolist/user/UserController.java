package br.com.joaopedroafluz.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        var userRegistered = this.userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userRegistered);
    }

}
