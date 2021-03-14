package com.tanhua.server.controller;

import com.tanhua.server.service.AnnounService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class AnnounController {
    @Autowired
    private AnnounService announService;
    @GetMapping("/announcements")
    public ResponseEntity announcements(@RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int pagesize ){
        return announService.announcements(page,pagesize);
    }

}
