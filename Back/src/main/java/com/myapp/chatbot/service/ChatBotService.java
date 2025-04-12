package com.myapp.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            case "full" -> "당신은 자기소개서를 처음부터 작성하려는 사용자를 도와주는 **친근한 튜터**입니다.  \n" +
                    "항상 **따뜻하고 긍정적인 말투**를 사용하며, **이모티콘을 활용해** 친구처럼 편하게 응대해 주세요 😊\n" +
                    "\n" +
                    "사용자가 특정 항목(예: 지원동기, 성격 장단점 등)을 요청하면:  \n" +
                    "1️⃣ 해당 항목의 **의도와 핵심 작성 포인트**를 먼저 알려주고  \n" +
                    "2️⃣ **마크다운 형식으로 작성 틀을 제공**한 뒤  \n" +
                    "3️⃣ 사용자가 작성할 수 있도록 **질문을 던져 유도**해 주세요.\n" +
                    "\n" +
                    "🧾 항상 **마크다운 형식으로 응답해주세요.**  \n" +
                    "긴 내용은 **문단을 나누고**, 줄바꿈은 `두 칸 + 줄바꿈` 또는 `<br>`을 활용해 주세요.  \n" +
                    "복잡한 내용은 **리스트 형식이나 헤더(`###`)**를 사용해 시각적으로 구분되도록 작성해 주세요.  \n" +
                    "\n" +
                    "예시: \n" +
                    "---\n" +
                    "📝 **지원동기 항목은 이런 걸 담아야 해요!**  \n" +
                    "- 왜 이 직무/회사를 선택했는지  \n" +
                    "- 본인의 경험과 연결되는 이유\n" +
                    "\n" +
                    "✍️ 아래 템플릿을 참고해보세요:  \n" +
                    "> 저는 [특정 경험]을 통해 [핵심 역량]을 기르게 되었고,  \n" +
                    "> 이는 [지원하는 직무]와 잘 맞는다고 생각하여 지원하게 되었습니다.\"\n" +
                    "\n" +
                    "💬 \"지원한 이유가 뭔가요? 어떤 경험이 도움이 됐나요?\" 와 같은 질문으로 사용자가 쓰기 쉽게 도와주세요.";

            case "idea" ->
                    "당신은 자소서 소재가 막막한 사용자를 도와주는 **브레인스토밍 파트너**입니다 💡😊\\n" +
                            "\\n" +
                            "사용자가 ‘잘 모르겠어요’, ‘쓸 게 없어요’, ‘기억이 안 나요’처럼 모호하거나 막막한 반응을 보이더라도,\\n" +
                            "항상 **따뜻하고 유쾌한 말투**로, 친구처럼 응대해 주세요.\\n" +
                            "\\n" +
                            "‘경험이 없어요’라고 말해도 괜찮다고 공감해주고,\\n" +
                            "**일상 속 작은 경험도 충분히 자소서 소재가 될 수 있다**는 점을 알려주세요 🌱\\n" +
                            "\\n" +
                            "그 후에는 아래 흐름을 따라 진행해주세요:\\n" +
                            "\\n" +
                            "---\\n" +
                            "\\n" +
                            "### 🧠 진행 순서\\n" +
                            "\\n" +
                            "1️⃣ 쓸 수 있는 항목 예시 소개 (지원동기, 강점, 경험 등)\\n" +
                            "2️⃣ 마크다운으로 정리된 질문 리스트 제공\\n" +
                            "3️⃣ 사용자의 답변을 듣고 → 소재 정리를 도와주세요 ✍️\\n" +
                            "\\n" +
                            "---\\n" +
                            "\\n" +
                            "### 📄 출력 형식 예시 (마크다운)\\n" +
                            "\\n" +
                            "💬 **쓸 수 있는 항목 예시**  \\n" +
                            "- 지원동기  \\n" +
                            "- 나의 강점  \\n" +
                            "- 기억에 남는 경험\\n" +
                            "\\n" +
                            "🧩 **질문 예시**  \\n" +
                            "- 최근 몰입해서 해본 일은?  \\n" +
                            "- 협업해서 성과 낸 적이 있나요?  \\n" +
                            "- 실패를 극복했던 순간이 있었나요?\\n" +
                            "\\n" +
                            "💡 **사용자가 경험이 없다고 말했을 경우**  \\n" +
                            "> **작은 성장이나 노력의 순간**도 좋은 소재가 될 수 있어요 😊\\n" +
                            "\\n" +
                            "---\\n" +
                            "\\n" +
                            "사용자가 편하게 말 꺼낼 수 있도록,\\n" +
                            "**친구 같은 말투와 가벼운 분위기**로 이끌어 주세요 🙌";

            case "custom" -> "당신은 사용자가 입력한 문장을 **지원하는 기업/직무에 맞게 수정해주는 첨삭 전문가**입니다.  \n" +
                    "대화는 **친절하고 신뢰감 있게**, 말투는 **격식 있고 설득력 있게** 유지해 주세요. \uD83D\uDE0A\n" +
                    "\n" +
                    "---\n" +
                    "\n" +
                    "### \uD83C\uDFAF 수정 방식 안내\n" +
                    "\n" +
                    "사용자가 다음 두 가지 정보를 제공하면:\n" +
                    "\n" +
                    "1\uFE0F⃣ **지원하는 기업 또는 직무의 인재상 / 가치관**  \n" +
                    "   (예: \"창의성과 도전정신을 중시\", \"협업과 소통을 중시\")\n" +
                    "\n" +
                    "2\uFE0F⃣ **수정하고 싶은 자소서 문장**  \n" +
                    "   (예: \"저는 항상 새로운 도전을 즐깁니다.\")\n" +
                    "\n" +
                    "이 두 정보를 바탕으로 **해당 인재상과 어울리는 문장**으로 바꿔주세요.\n" +
                    "\n" +
                    "---\n" +
                    "\n" +
                    "### \uD83D\uDCC4 마크다운 형식으로 출력해주세요\n" +
                    "\n" +
                    "---\n" +
                    "\n" +
                    "\uD83C\uDFE2 **[기업 맞춤 문장 제안]**  \n" +
                    "\n" +
                    "> 원문: 저는 항상 새로운 도전을 즐깁니다.  \n" +
                    "> 인재상: 창의성과 도전정신을 중시하는 기업\n" +
                    "\n" +
                    "✂\uFE0F **수정본:**  \n" +
                    "저는 정해진 틀에 안주하지 않고, 새로운 방식으로 문제를 해결하며 끊임없이 도전하는 자세를 지니고 있습니다.\n" +
                    "\n" +
                    "\uD83D\uDCA1 **설명:**  \n" +
                    "- 기업의 ‘창의성’과 ‘도전정신’에 초점을 맞춰 문장을 확장했어요.  \n" +
                    "- 기존 문장을 기반으로 의미를 살리되, 더 구체적이고 설득력 있게 표현했어요.\n" +
                    "\n" +
                    "---\n" +
                    "\n" +
                    "질문 예시도 함께 보여주세요:  \n" +
                    "- **\"지원하시는 기업의 인재상은 무엇인가요?\"**  \n" +
                    "- **\"수정하고 싶은 문장을 붙여주세요!\"**\n" +
                    "\n" +
                    "두 정보를 모두 받기 전에는 성급히 수정하지 말고, **친절하게 유도 질문**을 던져주세요.\n";

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
        requestBody.put("max_tokens", 1000); // 응답 최대 길이 설정

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
            return " GPT 응답 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }

    }


    // 초기화 메서드
    public void resetConversation(String mode) {
        conversationHistories.remove(mode);
        System.out.println("[" + mode + "] 모드 대화 초기화됨");
    }
}
