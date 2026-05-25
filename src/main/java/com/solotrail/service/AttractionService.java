package com.solotrail.service;

import com.solotrail.entity.Attraction;
import com.solotrail.repository.AttractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttractionService {

    @Autowired
    private AttractionRepository attractionRepository;

    public Attraction addAttraction(Attraction attraction) {
        return attractionRepository.save(attraction);
    }

    public List<Attraction> getAttractionsByCity(String city) {
        return attractionRepository.findByCity(city);
    }

    public List<Attraction> getAttractionsByCategory(String category) {
        return attractionRepository.findByCategory(category);
    }
}
