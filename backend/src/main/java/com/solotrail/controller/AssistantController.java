package com.solotrail.controller;

import com.solotrail.dto.ChatRequest;
import com.solotrail.dto.ChatResponse;
import com.solotrail.service.AiAssistantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    @Autowired
    private AiAssistantService assistantService;

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return assistantService.reply(request.message());
    }
}
