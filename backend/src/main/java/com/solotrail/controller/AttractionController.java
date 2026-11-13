package com.solotrail.controller;

import com.solotrail.entity.Attraction;
import com.solotrail.service.AttractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attractions")
public class AttractionController {

    @Autowired
    private AttractionService attractionService;

    @PostMapping
    public Attraction addAttraction(@RequestBody Attraction attraction) {
        return attractionService.addAttraction(attraction);
    }

    @GetMapping("/city/{city}")
    public List<Attraction> getByCity(@PathVariable String city) {
        return attractionService.getAttractionsByCity(city);
    }

    @GetMapping("/category/{category}")
    public List<Attraction> getByCategory(@PathVariable String category) {
        return attractionService.getAttractionsByCategory(category);
    }
}
