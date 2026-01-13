package com.app.ecom;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users")
    //@RequestMapping(value = "/api/users" , method = RequestMethod.GET)//It will work same as Get mapping.It is at method level
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.fetchAllUsers(),HttpStatus.OK);//this is the first one
        //There are two ways to do it this is the second one //return ResponseEntity.ok(userService.fetchAllUsers());
    }

    @GetMapping("/api/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.fetchUser(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()).getBody();
    }


    @PostMapping("/api/users")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        userService.addUser(user);
        return ResponseEntity.ok("User added Successfully");
    }


    @PutMapping ("/api/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id,@RequestBody User updatedUser) {
        boolean updated = userService.updateUser(id,updatedUser);
        if(updated)
            return ResponseEntity.ok("User updated successfuly");
        return ResponseEntity.notFound().build();
    }
}
