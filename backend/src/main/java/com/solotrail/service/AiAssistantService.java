package com.solotrail.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solotrail.dto.ChatResponse;
import com.solotrail.entity.*;
import com.solotrail.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

@Service
public class AiAssistantService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;
    private final AttractionRepository attractionRepository;
    private final HotelRepository hotelRepository;
    private final RestaurantRepository restaurantRepository;
    private final TouristGuideRepository guideRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;
    private final String groqApiKey;
    private final String groqModel;
    private final String openAiApiKey;
    private final String openAiModel;
    private final String genericApiKey;
    private final String genericBaseUrl;
    private final String genericModel;

    public AiAssistantService(
            AttractionRepository attractionRepository,
            HotelRepository hotelRepository,
            RestaurantRepository restaurantRepository,
            TouristGuideRepository guideRepository,
            UserRepository userRepository,
            TripRepository tripRepository,
            BookingRepository bookingRepository,
            @Value("${GROQ_API_KEY:}") String groqApiKey,
            @Value("${GROQ_MODEL:openai/gpt-oss-20b}") String groqModel,
            @Value("${OPENAI_API_KEY:}") String openAiApiKey,
            @Value("${OPENAI_MODEL:gpt-4o-mini}") String openAiModel,
            @Value("${AI_API_KEY:}") String genericApiKey,
            @Value("${AI_BASE_URL:https://api.openai.com/v1}") String genericBaseUrl,
            @Value("${AI_MODEL:gpt-4o-mini}") String genericModel) {
        this.attractionRepository = attractionRepository;
        this.hotelRepository = hotelRepository;
        this.restaurantRepository = restaurantRepository;
        this.guideRepository = guideRepository;
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.bookingRepository = bookingRepository;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        this.groqApiKey = groqApiKey;
        this.groqModel = groqModel;
        this.openAiApiKey = openAiApiKey;
        this.openAiModel = openAiModel;
        this.genericApiKey = genericApiKey;
        this.genericBaseUrl = genericBaseUrl;
        this.genericModel = genericModel;
    }

    public ChatResponse reply(String message, String email) {
        String context = travelContext(email);
        Provider provider = provider();
        if (provider == null) {
            return new ChatResponse(fallback(message, context), "Built-in travel guide");
        }

        try {
            return new ChatResponse(call(provider, message, context), provider.name);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "The configured AI provider is unavailable. Try again or remove the provider key to use built-in guidance.");
        }
    }

    private String call(Provider provider, String message, String context) throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "model", provider.model,
                "temperature", 0.3,
                "messages", new Object[]{
                        Map.of("role", "system", "content",
                                "You are SoloTrail, a concise and practical Chennai travel assistant. "
                                        + "Use only the supplied catalog and user context when recommending options. "
                                        + "Prices and availability are estimates. Never claim a real ticket or payment was issued. "
                                        + "Current SoloTrail data:\n" + context),
                        Map.of("role", "user", "content", message)
                }
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(provider.baseUrl + "/chat/completions"))
                .timeout(Duration.ofSeconds(25))
                .header("Authorization", "Bearer " + provider.apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("AI provider returned HTTP " + response.statusCode());
        }
        JsonNode content = objectMapper.readTree(response.body()).path("choices").path(0).path("message").path("content");
        if (!content.isTextual() || content.asText().isBlank()) {
            throw new IllegalStateException("AI provider returned no message");
        }
        return content.asText();
    }

    private String travelContext(String email) {
        StringJoiner context = new StringJoiner("\n");
        List<Attraction> attractions = attractionRepository.findByCityIgnoreCase("Chennai");
        List<Hotel> hotels = hotelRepository.findByCityIgnoreCase("Chennai");
        List<Restaurant> restaurants = restaurantRepository.findByCityIgnoreCase("Chennai");
        List<TouristGuide> guides = guideRepository.findByCityIgnoreCase("Chennai");
        context.add("Attractions: " + attractions.stream()
                .map(item -> item.getAttractionName() + " (" + item.getCategory() + ", rating " + item.getRating() + ")")
                .limit(6).reduce((a, b) -> a + "; " + b).orElse("none"));
        context.add("Hotels: " + hotels.stream()
                .map(item -> item.getHotelName() + " (₹" + item.getPricePerNight() + "/night, rating " + item.getRating() + ")")
                .limit(6).reduce((a, b) -> a + "; " + b).orElse("none"));
        context.add("Restaurants: " + restaurants.stream()
                .map(item -> item.getRestaurantName() + " (" + item.getCuisine() + ", " + item.getPriceRange() + ")")
                .limit(6).reduce((a, b) -> a + "; " + b).orElse("none"));
        context.add("Guides: " + guides.stream()
                .map(item -> item.getGuideName() + " (₹" + item.getPricePerDay() + "/day, " + item.getSpecialization() + ")")
                .limit(6).reduce((a, b) -> a + "; " + b).orElse("none"));

        userRepository.findByEmail(email).ifPresent(user -> {
            List<Trip> trips = tripRepository.findByUserId(user.getId());
            List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
            context.add("User saved plans: " + trips.stream()
                    .map(item -> item.getSource() + " to " + item.getDestination() + " on " + item.getTravelDate()
                            + " by " + item.getTransportMode())
                    .limit(3).reduce((a, b) -> a + "; " + b).orElse("none"));
            context.add("User demo bookings: " + bookings.stream()
                    .map(item -> item.getBookingReference() + " total ₹" + item.getTotalAmount() + " (" + item.getPaymentStatus() + ")")
                    .limit(3).reduce((a, b) -> a + "; " + b).orElse("none"));
        });
        return context.toString();
    }

    private Provider provider() {
        if (!groqApiKey.isBlank()) {
            return new Provider("Groq", groqApiKey, "https://api.groq.com/openai/v1", groqModel);
        }
        if (!openAiApiKey.isBlank()) {
            return new Provider("OpenAI", openAiApiKey, "https://api.openai.com/v1", openAiModel);
        }
        if (!genericApiKey.isBlank()) {
            return new Provider("OpenAI-compatible provider", genericApiKey, genericBaseUrl.replaceAll("/+$", ""), genericModel);
        }
        return null;
    }

    private String fallback(String message, String context) {
        String normalized = message.toLowerCase(Locale.ROOT);
        if (normalized.contains("place") || normalized.contains("attraction") || normalized.contains("visit")) {
            return "Here are current Chennai places from SoloTrail: " + contextLine(context, "Attractions")
                    + ". Choose one from the Places page for more details.";
        }
        if (normalized.contains("budget") || normalized.contains("cheap") || normalized.contains("cost")) {
            return "For your current SoloTrail options: " + contextLine(context, "Hotels")
                    + ". Transport estimates are bus ₹800, train ₹1,200, car ₹2,500, and flight ₹4,500. "
                    + "The final demo total combines your selected services.";
        }
        if (normalized.contains("food") || normalized.contains("restaurant")) {
            return "Current Chennai food options: " + contextLine(context, "Restaurants")
                    + ". Select one in My trips and it will be included in the demo total.";
        }
        if (normalized.contains("hotel") || normalized.contains("stay") || normalized.contains("room")) {
            return "Current Chennai stays: " + contextLine(context, "Hotels")
                    + ". Select a stay in My trips to include its nightly price.";
        }
        if (normalized.contains("guide") || normalized.contains("person") || normalized.contains("local")) {
            return "Current local guides: " + contextLine(context, "Guides")
                    + ". Select a guide in My trips to include the daily fee.";
        }
        return "I found your current SoloTrail data. " + contextLine(context, "Hotels")
                + ". Ask me for places, hotels, food, guides, budget, or your saved booking.";
    }

    private String contextLine(String context, String label) {
        return java.util.Arrays.stream(context.split("\\n"))
                .filter(line -> line.startsWith(label + ":"))
                .findFirst()
                .map(line -> line.substring(label.length() + 2))
                .orElse("no current options");
    }

    private record Provider(String name, String apiKey, String baseUrl, String model) {
    }
}
