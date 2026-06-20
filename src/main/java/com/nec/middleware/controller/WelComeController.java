package com.nec.middleware.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController()
@RequestMapping()
public class WelComeController {

    @GetMapping()
    public String wish() {
        return "NEC Middleware Service is UP and Running.....";
    }
}
