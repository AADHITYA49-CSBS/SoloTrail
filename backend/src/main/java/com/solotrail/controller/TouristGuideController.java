package com.solotrail.controller;

import com.solotrail.entity.TouristGuide;
import com.solotrail.service.TouristGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guides")
public class TouristGuideController {

    @Autowired
    private TouristGuideService guideService;

    @PostMapping
    public TouristGuide addGuide(@RequestBody TouristGuide guide) {
        return guideService.addGuide(guide);
    }

    @GetMapping("/{city}")
    public List<TouristGuide> getGuidesByCity(@PathVariable String city) {
        return guideService.getGuidesByCity(city);
    }
}
