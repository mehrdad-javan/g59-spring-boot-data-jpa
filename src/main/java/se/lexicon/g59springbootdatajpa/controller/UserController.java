package se.lexicon.g59springbootdatajpa.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import se.lexicon.g59springbootdatajpa.dto.request.UserRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.response.UserResponseDTO;
import se.lexicon.g59springbootdatajpa.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
// http:localhost:8080/api/v1/users

@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST - http:localhost:8080/api/v1/users
    @PostMapping
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO dto) {
        System.out.println("request body: = " + dto);
        UserResponseDTO response = userService.register(dto);
        System.out.println("response body = " + response);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
     /*
        request body
        {
            "email": "value",
            "fullName": "value"
        }

        response body:
        {
            "id" : 0,
            "email": "value",
            "fullName": "value"
            "createdDate": "value"
        }
         */

    // GET - http:localhost:8080/api/v1/users/1
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable @Positive(message = "id must be a positive number.") Long id) {
        System.out.println("id = " + id);
        UserResponseDTO response = userService.findById(id);
        System.out.println("response = " + response);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }

    // GET - http:localhost:8080/api/v1/users
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findAll());
    }


}
