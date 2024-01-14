package com.sriharyi.security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

// @RestController
// public class LoggingController {

//     @GetMapping("/logging")
//     public ResponseEntity<String> logging() {
//         return new ResponseEntity<>("logging/baeldung", HttpStatus.OK);
//     }

// }
@RestController
@Slf4j
public class LoggingController {
 
    @RequestMapping("/logging")
    public String index() {
        log.trace("A TRACE Message");
        log.debug("A DEBUG Message");
        log.info("An INFO Message");
        log.warn("A WARN Message");
        log.error("An ERROR Message");
 
        return "Howdy! Check out the Logs to see the output...";
    }
}