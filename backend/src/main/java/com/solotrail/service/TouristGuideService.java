package com.solotrail.service;

import com.solotrail.entity.TouristGuide;
import com.solotrail.repository.TouristGuideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TouristGuideService {

    @Autowired
    private TouristGuideRepository guideRepository;

    public List<TouristGuide> getGuidesByCity(String city) {
        return guideRepository.findByCityIgnoreCase(city);
    }

    public TouristGuide addGuide(TouristGuide guide) {
        return guideRepository.save(guide);
    }
}
