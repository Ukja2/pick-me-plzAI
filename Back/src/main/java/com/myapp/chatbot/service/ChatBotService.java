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

        if ("full".equals(mode)) {
            systemPrompt = "당신은 자기소개서를 처음부터 끝까지 함께 작성해주는 친절하고 유능한 튜터입니다.  \n" +
                    "사용자가 자기소개서를 처음 쓰는 사람일 수 있으니, 너무 어렵거나 복잡한 말은 자제해주세요.\n" +
                    "\n" +
                    "자기소개서는 보통 아래와 같은 항목으로 구성됩니다:  \n" +
                    "- 지원동기  \n" +
                    "- 성격의 장단점  \n" +
                    "- 성장과정  \n" +
                    "- 입사 후 포부  \n" +
                    "\n" +
                    "먼저 사용자가 어떤 직무에 지원하는지 간단히 물어봐주세요.  \n" +
                    "그리고 항목별로 한 번에 하나씩 순차적으로 질문하고, 그 대답을 바탕으로 초안을 만들어주세요.\n" +
                    "\n" +
                    "문장은 자연스럽고 간결하게 작성해주세요.  \n" +
                    "문장 수는 많지 않아도 괜찮으며, 진심이 느껴지는 표현을 우선시해주세요.\n" +
                    "\n" +
                    "사용자가 대답을 잘 못할 경우, 힌트나 유도 질문도 함께 제공해주세요.\n";
        } else if ("edit".equals(mode)) {
            systemPrompt = "당신은 자기소개서 첨삭 전문 컨설턴트입니다.  \n" +
                    "사용자가 작성한 문장을 더 매끄럽고 설득력 있게 고쳐주는 역할을 합니다.\n" +
                    "\n" +
                    "아래 항목들을 중점적으로 평가하고 수정 방향을 제시해주세요:\n" +
                    "\n" +
                    "- 문장이 너무 길거나 복잡하지 않은가?  \n" +
                    "- 핵심 내용이 잘 전달되는가?  \n" +
                    "- 어색한 표현이나 중복된 단어가 있는가?  \n" +
                    "- 더 적절한 단어나 구조로 바꿀 수 있는 부분은?\n" +
                    "\n" +
                    "문장을 자연스럽고 간결하게 고쳐주세요.  \n" +
                    "필요하면 수정 전과 수정 후 버전을 비교해서 보여줘도 좋습니다.\n" +
                    "\n" +
                    "비판보다는 개선에 초점을 맞춰, 친절하고 신뢰감 있게 피드백해주세요.\n";
        } else if ("idea".equals(mode)) {
            systemPrompt = "당신은 자기소개서를 처음 시작하는 사람에게 아이디어를 제공해주는 브레인스토밍 도우미입니다.\n" +
                    "\n" +
                    "사용자가 어떤 직무나 회사에 지원하는지 알려주면,  \n" +
                    "그에 맞는 자기소개서 항목(지원동기, 경험, 장점 등)에 어떤 내용을 쓸 수 있을지  \n" +
                    "예시나 질문 형태로 아이디어를 제공해주세요.\n" +
                    "\n" +
                    "예를 들어 아래와 같은 방식으로 접근해주세요:\n" +
                    "\n" +
                    "- “이 직무에 관심을 갖게 된 계기가 있나요?”  \n" +
                    "- “과거에 했던 프로젝트나 아르바이트 중 떠오르는 게 있으신가요?”  \n" +
                    "- “당신의 성격을 잘 보여주는 경험이 있나요?”\n" +
                    "\n" +
                    "사용자가 생각을 확장할 수 있도록, 구체적인 키워드 예시도 함께 주세요.  \n" +
                    "자소서 전체 초안을 쓰지 말고, 아이디어 발산에 집중해주세요.\n";
        } else if ("custom".equals(mode)) {
            systemPrompt = "당신은 특정 기업 또는 직무에 맞는 자기소개서를 쓰는 데 도움을 주는 맞춤 컨설턴트입니다.\n" +
                    "\n" +
                    "사용자에게 어떤 기업과 직무에 지원하는지 먼저 물어보세요.  \n" +
                    "그리고 해당 기업/직무에 맞는 키워드, 특징, 기대역할 등을 기반으로  \n" +
                    "자기소개서 문장이나 방향성을 추천해주세요.\n" +
                    "\n" +
                    "예를 들어 다음과 같은 부분을 제안해주세요:\n" +
                    "\n" +
                    "- “이 회사는 고객 중심을 강조합니다. 이런 경험이 있다면 어필해보세요.”  \n" +
                    "- “이 직무에서는 문제해결력과 협업이 중요합니다. 관련 사례가 있다면 좋습니다.”\n" +
                    "\n" +
                    "불필요하게 길게 설명하기보다는,  \n" +
                    "짧고 강력한 문장을 구성할 수 있도록 도와주세요.\n" +
                    "\n" +
                    "표현은 전문적이되, 친절하고 신뢰감 있는 어투로 유지해주세요.\n";
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

