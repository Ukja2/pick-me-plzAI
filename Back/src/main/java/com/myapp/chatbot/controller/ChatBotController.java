package com.myapp.chatbot.controller;

import com.myapp.chatbot.service.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatBotController {

    @Autowired
    private ChatBotService chatBotService;

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message"); // 사용자의 메시지를 꺼냄
        String gptResponse = chatBotService.getChatResponse(userMessage); // GPT에게 질문하고 응답 받음
        return Map.of("response", gptResponse); // 결과를 다시 사용자에게 JSON으로 반환
    }
}
