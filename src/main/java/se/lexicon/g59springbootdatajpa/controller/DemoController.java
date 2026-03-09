package se.lexicon.g59springbootdatajpa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
// this class handels http requests and returns responses
@RequestMapping("/api/v1/demo")
public class DemoController {

    @GetMapping()
    public ResponseEntity<String> index() {
        System.out.println("### index method called ###");
        String responseBody = "Hello World!";
        return ResponseEntity.ok(responseBody); // 200
    }
    // add more get operations here as needed


    @PostMapping()
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> requestBody) {
        System.out.println("### create method called ###");
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("id", 1);
        responseBody.put("name", requestBody.get("name"));
        responseBody.put("age", requestBody.get("age"));
        responseBody.put("createDate", LocalDate.now());
        // id: 1
        // name: "Student Name
        // createDate: 2026-03-09
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

}
