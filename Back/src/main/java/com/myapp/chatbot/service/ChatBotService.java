package com.myapp.chatbot.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service // 서비스 클래스임을 명시
public class ChatBotService {

    @Value("${openai.api-key}") //application.yml에 저장된 API 키를 불러와서 사용
    private String apiKey;

    private final String API_URL = "https://api.openai.com/v1/chat/completions"; // // OpenAI API 엔드포인트 주소

    private Map<String, Object> createRequestBody(String userMessage) {
        Map<String, Object> requestBody = new HashMap<>();  // 새 Map 객체 생성

        requestBody.put("model", "gpt-3.5-turbo"); // 사용할 GPT 모델
        requestBody.put("messages", List.of( // 메시지 리스트 생성
                Map.of("role", "system", "content", "You are a helpful assistant."),
                Map.of("role", "user", "content", userMessage) // 여기서 userMessage 사용됨!
        ));
        requestBody.put("max_tokens", 800); // 응답 최대 길이 설정

        return requestBody; // 생성한 Map 반환

    }


    private String sendRequestToGpt(Map<String,Object> requestBody){

        // HTTP 요청을 보낼 수 있는 객체
        RestTemplate restTemplate = new RestTemplate();

        // 요청할 때 필요한 헤더 객체 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // JSON 형식으로 보낸다고 명시
        headers.setBearerAuth(apiKey); // 인증을 위한 API 키 추가

        // 본문 내용(Body)와 헤더(header)를 하나로 합쳐서 하나의 객체로 만듦
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);


        // API 호출
        ResponseEntity<Map> response = restTemplate.exchange(
                API_URL, // 어디로 보낼지 (OpenAI 서버)
                HttpMethod.POST, // 어떤 방식으로 보낼지 (POST)
                request, // 어떤 데이터를 보낼지 (본문 + 헤더)
                Map.class // 응답을 어떤 타입으로 받을지 (Map)
        );

        // 응답에서 필요한 메시지를 추출
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        return (String) message.get("content"); // GPT의 실제 응답 텍스트만 리턴

    }

    public String getChatResponse(String userMessage) {
        Map<String, Object> requestBody = createRequestBody(userMessage); // 본문 만들기
        return sendRequestToGpt(requestBody); // 실제 요청 보내기
    }


}

