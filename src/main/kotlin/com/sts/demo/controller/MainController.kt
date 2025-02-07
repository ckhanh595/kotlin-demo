package com.sts.demo.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MainController {

    @RequestMapping("/hello")
    fun hello(): String {
        return "Hello, World!"
    }

}