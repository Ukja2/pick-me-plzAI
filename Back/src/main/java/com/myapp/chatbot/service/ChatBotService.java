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

    private Map<String, Object> createRequestBody(String userMessage, String mode) {
        Map<String, Object> requestBody = new HashMap<>();  // 새 Map 객체 생성

        String systemPrompt = ""; // 모드 변경에 따른 프롬프트 설정

        if ("beginner".equals(mode)) {
            systemPrompt = "당신은 자기소개서 작성을 처음 접하는 사람들을 도와주는 따뜻하고 친절한 튜터입니다. 사용자가 불안하거나 막막한 마음을 가지고 있음을 이해하고, 격려와 공감을 바탕으로 질문을 던지며 자기소개서 내용을 함께 만들어갑니다.\n" +
                    "\n" +
                    "자기소개서는 크게 [지원동기 / 성격의 장단점 / 성장과정 / 입사 후 포부]와 같은 항목으로 나뉘며, 각 항목에서 무엇을 써야 할지 모르는 사용자를 위해 다음과 같은 질문을 던져주세요:\n" +
                    "\n" +
                    "- \"어떤 직무에 지원하고 싶으신가요?\"\n" +
                    "- \"당신의 강점은 무엇이라고 생각하나요?\"\n" +
                    "- \"어떤 경험이 당신을 성장시켰나요?\"\n" +
                    "- \"입사 후 어떤 사람이 되고 싶으신가요?\"\n" +
                    "\n" +
                    "사용자가 답하면 그 답을 바탕으로 초안을 구성해주고, 간단하고 자연스러운 문장으로 변환해 주세요. 너무 어려운 용어나 지적 표현은 자제하고, 마치 상담하듯 대화하면서 글을 완성해 주세요.\n";

        } else if ("advanced".equals(mode)) {
            systemPrompt = "당신은 자기소개서 첨삭 전문 컨설턴트입니다. 사용자가 쓴 문장을 토대로 논리의 흐름, 문장의 표현력, 키워드 사용, 메시지 전달력 등을 분석하고, 더 설득력 있게 개선할 수 있는 방향을 제시해주세요.\n" +
                    "\n" +
                    "- 문장의 구조가 어색하거나 중복되는 부분이 있다면 정확히 지적해 주세요.\n" +
                    "- 더 좋은 단어나 문장이 있다면 대체 예시를 들어주세요.\n" +
                    "- 불필요한 문장이나 모호한 표현이 있다면 그 이유와 함께 제안해 주세요.\n" +
                    "- 전체 글의 구성(도입 → 본론 → 결론) 흐름도 피드백해 주세요.\n" +
                    "\n" +
                    "사용자는 전문가의 날카롭고 솔직한 피드백을 원하므로, 직설적이고 구체적으로 표현해도 괜찮습니다. 단, 무례하지 않게 객관적이고 신뢰감을 주는 어조로 답변해 주세요.\n";
        }


        requestBody.put("model", "gpt-3.5-turbo"); // 사용할 GPT 모델
        requestBody.put("messages", List.of( // 메시지 리스트 생성
                Map.of("role", "system",
                        "content", systemPrompt),
                Map.of("role", "user", "content", userMessage) // 여기서 userMessage 사용됨!
        ));
        requestBody.put("max_tokens", 800); // 응답 최대 길이 설정

        return requestBody; // 생성한 Map 반환

    }

    private String sendRequestToGpt(Map<String, Object> requestBody) {
        try {
            // HTTP 요청을 보낼 수 있는 객체
            RestTemplate restTemplate = new RestTemplate();

            // 요청할 때 필요한 헤더 객체 생성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON); // JSON 형식으로 보낸다고 명시
            headers.setBearerAuth(apiKey); // 인증을 위한 API 키 추가

            System.out.println("🔥 보낼 내용: " + requestBody); // 🔍 요청 확인


            // 본문 내용(Body)와 헤더(header)를 하나로 합쳐서 하나의 객체로 만듦
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // API 호출
            ResponseEntity<Map> response = restTemplate.exchange(
                    API_URL, // 어디로 보낼지 (OpenAI 서버)
                    HttpMethod.POST, // 어떤 방식으로 보낼지 (POST)
                    request, // 어떤 데이터를 보낼지 (본문 + 헤더)
                    Map.class // 응답을 어떤 타입으로 받을지 (Map)
            );

            System.out.println("🔥 응답 내용: " + response.getBody()); // 🔍 응답 확인


            // 응답에서 필요한 메시지를 추출
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

            return (String) message.get("content"); // GPT의 실제 응답 텍스트만 리턴

        } catch (Exception e) {
            // 예외 발생 시 콘솔에 에러 출력
            e.printStackTrace();
            // 사용자에게 보여줄 에러 메시지 반환
            return "⚠️ GPT 응답 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }

    public String getChatResponse(String userMessage, String mode) {
        Map<String, Object> requestBody = createRequestBody(userMessage, mode);
        return sendRequestToGpt(requestBody);
    }
}

