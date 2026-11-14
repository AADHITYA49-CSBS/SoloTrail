package com.solotrail.config;

import com.solotrail.entity.Attraction;
import com.solotrail.entity.Hotel;
import com.solotrail.entity.Restaurant;
import com.solotrail.entity.TouristGuide;
import com.solotrail.repository.AttractionRepository;
import com.solotrail.repository.HotelRepository;
import com.solotrail.repository.RestaurantRepository;
import com.solotrail.repository.TouristGuideRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DemoCatalogSeeder implements CommandLineRunner {

    private final AttractionRepository attractionRepository;
    private final HotelRepository hotelRepository;
    private final RestaurantRepository restaurantRepository;
    private final TouristGuideRepository guideRepository;

    public DemoCatalogSeeder(
            AttractionRepository attractionRepository,
            HotelRepository hotelRepository,
            RestaurantRepository restaurantRepository,
            TouristGuideRepository guideRepository) {
        this.attractionRepository = attractionRepository;
        this.hotelRepository = hotelRepository;
        this.restaurantRepository = restaurantRepository;
        this.guideRepository = guideRepository;
    }

    @Override
    public void run(String... args) {
        if (attractionRepository.count() == 0) {
            attractionRepository.saveAll(List.of(
                    new Attraction(null, "Marina Beach", "Chennai", "Beach", 4.5, true,
                            "A relaxed shoreline for sunrise walks and evening sunsets."),
                    new Attraction(null, "Kapaleeshwarar Temple", "Chennai", "Culture", 4.7, true,
                            "A historic Dravidian temple in the heart of Mylapore.")
            ));
        }
        if (hotelRepository.count() == 0) {
            hotelRepository.saveAll(List.of(
                    new Hotel(null, "Bay View Residency", "Chennai", 2200.0, 4.3, true,
                            "A comfortable stay close to the city and the coast."),
                    new Hotel(null, "Mylapore Courtyard", "Chennai", 1800.0, 4.1, true,
                            "A quiet, simple base for exploring local neighbourhoods.")
            ));
        }
        if (restaurantRepository.count() == 0) {
            restaurantRepository.saveAll(List.of(
                    new Restaurant(null, "South Street Kitchen", "Chennai", "South Indian", "₹₹", 4.4, true,
                            "Fresh dosas, thalis, and local filter coffee."),
                    new Restaurant(null, "Marina Spice House", "Chennai", "Seafood", "₹₹₹", 4.2, true,
                            "Coastal flavours and a relaxed dinner setting.")
            ));
        }
        if (guideRepository.count() == 0) {
            guideRepository.saveAll(List.of(
                    new TouristGuide(null, "Kavya", "Chennai", "English, Tamil", 5, 1200.0,
                            "Culture and food walks", true, "Demo contact"),
                    new TouristGuide(null, "Arun", "Chennai", "English, Hindi, Tamil", 8, 1500.0,
                            "History and neighbourhoods", true, "Demo contact")
            ));
        }
    }
}
