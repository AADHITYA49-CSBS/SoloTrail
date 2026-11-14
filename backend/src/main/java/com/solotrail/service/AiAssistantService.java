package com.solotrail.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solotrail.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;

@Service
public class AiAssistantService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;
    private final String groqApiKey;
    private final String groqModel;
    private final String openAiApiKey;
    private final String openAiModel;
    private final String genericApiKey;
    private final String genericBaseUrl;
    private final String genericModel;

    public AiAssistantService(
            @Value("${GROQ_API_KEY:}") String groqApiKey,
            @Value("${GROQ_MODEL:openai/gpt-oss-20b}") String groqModel,
            @Value("${OPENAI_API_KEY:}") String openAiApiKey,
            @Value("${OPENAI_MODEL:gpt-4o-mini}") String openAiModel,
            @Value("${AI_API_KEY:}") String genericApiKey,
            @Value("${AI_BASE_URL:https://api.openai.com/v1}") String genericBaseUrl,
            @Value("${AI_MODEL:gpt-4o-mini}") String genericModel) {
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        this.groqApiKey = groqApiKey;
        this.groqModel = groqModel;
        this.openAiApiKey = openAiApiKey;
        this.openAiModel = openAiModel;
        this.genericApiKey = genericApiKey;
        this.genericBaseUrl = genericBaseUrl;
        this.genericModel = genericModel;
    }

    public ChatResponse reply(String message) {
        Provider provider = provider();
        if (provider == null) {
            return new ChatResponse(fallback(message), "Built-in travel guide");
        }

        try {
            return new ChatResponse(call(provider, message), provider.name);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "The configured AI provider is unavailable. Try again or remove the provider key to use built-in guidance.");
        }
    }

    private String call(Provider provider, String message) throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "model", provider.model,
                "temperature", 0.3,
                "messages", new Object[]{
                        Map.of("role", "system", "content",
                                "You are SoloTrail, a concise and practical Chennai travel assistant. "
                                        + "Give safe, budget-aware advice. Be clear that prices and availability are estimates."),
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

    private Provider provider() {
        if (!groqApiKey.isBlank()) {
            return new Provider("Groq", groqApiKey, "https://api.groq.com/openai/v1", groqModel);
        }
        if (!openAiApiKey.isBlank()) {
            return new Provider("OpenAI", openAiApiKey, "https://api.openai.com/v1", openAiModel);
        }
        if (!genericApiKey.isBlank()) {
            return new Provider("OpenAI-compatible provider", genericApiKey, genericBaseUrl, genericModel);
        }
        return null;
    }

    private String fallback(String message) {
        String normalized = message.toLowerCase(Locale.ROOT);
        if (normalized.contains("budget") || normalized.contains("cheap")) {
            return "For a budget Chennai plan, choose bus or train, a mid-range hotel, and local restaurants. "
                    + "Start with your total rupee budget, then keep transport and accommodation within about 70% of it.";
        }
        if (normalized.contains("food") || normalized.contains("restaurant")) {
            return "For food, choose a restaurant by cuisine and price range. Keep a little extra budget for local meals "
                    + "and check opening hours before you travel.";
        }
        if (normalized.contains("hotel") || normalized.contains("stay")) {
            return "Choose a stay near the places you want to visit. Compare nightly price, rating, and availability "
                    + "before confirming your demo booking.";
        }
        if (normalized.contains("flight") || normalized.contains("bus") || normalized.contains("train") || normalized.contains("car")) {
            return "Transport is estimated for this demo: bus ₹800, train ₹1,200, car ₹2,500, and flight ₹4,500. "
                    + "Actual provider prices can change.";
        }
        return "I can help with Chennai travel plans, transport, hotels, restaurants, guides, budgets, and your demo checkout. "
                + "Ask me what you would like to plan.";
    }

    private record Provider(String name, String apiKey, String baseUrl, String model) {
    }
}
