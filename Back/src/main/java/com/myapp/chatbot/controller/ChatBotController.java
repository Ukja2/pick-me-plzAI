package com.myapp.chatbot.controller;

import com.myapp.chatbot.service.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://127.0.0.1:5500")  // 여기서 CORS를 설정, 요청을 허용할 도메인 추가
public class ChatBotController {

    @Autowired
    private ChatBotService chatBotService;

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message"); // 사용자의 메시지를 꺼냄
        String mode = request.get("mode"); // 현재 모드 상태 get
        String gptResponse = chatBotService.getChatResponse(userMessage, mode); // GPT에게 질문하고 응답 받음
        return Map.of("response", gptResponse); // 결과를 다시 사용자에게 JSON으로 반환
    }
}
