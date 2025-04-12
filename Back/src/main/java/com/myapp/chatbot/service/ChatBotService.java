package com.myapp.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service // 서비스 클래스임을 명시
public class ChatBotService {

    @Value("${openai.api-key}") //application.yml에 저장된 API 키를 불러와서 사용
    private String apiKey;

    private final String API_URL = "https://api.openai.com/v1/chat/completions"; // // OpenAI API 엔드포인트 주소

    // 모드별 대화 저장용 Map
    private final Map<String, List<Map<String, String>>> conversationHistories = new HashMap<>();

    // 프롬프트 설정 메서드
    private String getSystemPrompt(String mode) {
        return switch (mode) {
            case "full" -> "당신은 자기소개서를 처음부터 함께 작성해주는 따뜻한 튜터입니다. 사용자가 부담을 느끼지 않도록 한 번에 하나씩 질문해주세요.";
            case "edit" -> "당신은 문장을 다듬어주는 전문가입니다. 문장을 자연스럽고 설득력 있게 고쳐주세요.";
            case "idea" -> "당신은 자기소개서 아이디어를 함께 브레인스토밍해주는 파트너입니다. 사용자에게 아이디어를 떠올릴 수 있게 질문해주세요.";
            case "custom" -> "당신은 기업 맞춤 자기소개서를 코칭하는 전문가입니다. 지원 기업/직무에 따라 문장을 조정해주세요.";
            default -> "당신은 자기소개서 튜터입니다.";
        };
    }

    // 히스토리 가져오기
    private List<Map<String, String>> getHistoryForMode(String mode) {
        return conversationHistories.computeIfAbsent(mode, k -> {
            List<Map<String, String>> newList = new ArrayList<>();
            newList.add(Map.of("role", "system", "content", getSystemPrompt(mode)));
            return newList;
        });
    }

    public String getChatResponse(String userMessage, String mode) {
        List<Map<String, String>> history = getHistoryForMode(mode); // 대화 기록 가져오기

        // 사용자 메시지 추가
        history.add(Map.of("role", "user", "content", userMessage));

        Map<String, Object> requestBody = new HashMap<>();  // 새 Map 객체 생성

        requestBody.put("model", "gpt-3.5-turbo"); // 사용할 GPT 모델
        requestBody.put("messages", history); // 누적된 대화 전체 전송
        requestBody.put("max_tokens", 800); // 응답 최대 길이 설정

        try {
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

            String content = (String) message.get("content"); // GPT의 실제 응답 텍스트만 리턴

            // GPT 응답 추가
            history.add(Map.of("role", "assistant", "content", content));

            return content;

        } catch (Exception e) {
            // 예외 발생 시 콘솔에 에러 출력
            e.printStackTrace();
            // 사용자에게 보여줄 에러 메시지 반환
            return "⚠️ GPT 응답 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }


}
