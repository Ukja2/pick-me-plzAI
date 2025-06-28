## 📝 뽑아주소서 - AI 자기소개서 작성 서포터

![작동 화면](https://github.com/user-attachments/assets/bd7378e7-1055-4aee-996d-4d45b796aaf8)

## 기획 배경

고학년 대학생이나 취업을 준비하는 사람들은 자기소개서를 작성할 때 막막함을 많이 느낍니다.
“무엇부터 써야 하지?”, “이 문장 이상한가?”, “어떤 경험을 써야 하지?”와 같은 고민들로 쉽게 시작하지 못하죠.

이 프로젝트는 저와 같은 고민을 가진 사람들을 위해,
AI의 도움으로 자기소개서를 더 쉽게, 더 똑똑하게 작성할 수 있도록 돕고자 기획되었습니다.

특히 단순한 챗봇이 아닌, **GPT 모델에 모드별 역할(프롬프트)** 을 부여하여 상황에 따라 다른 응답을 하도록 구성했습니다.
마치 자소서를 잘 아는 친구나 선배가 옆에서 도와주는 것처럼 느낄 수 있도록 만들고자 했습니다.

그리고 무엇보다도,

> 첫 웹 프로그램을 직접 만들어보고 싶었습니다. 완벽하지 않고 어설픈 부분도 많지만, 검색해가며 하나씩 기능을 구현하는 것에 배움의 의미를 두고 이 프로젝트를 시작했습니다.


---

## 기술 스택
<table>
  <thead>
    <tr>
      <th style="text-align:center;">Frontend</th>
      <th style="text-align:center;">Backend</th>
      <th style="text-align:center;">AI</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td align="center">
        <img alt="HTML5" src="https://img.shields.io/badge/HTML5-E34F26.svg?&style=flat&logo=HTML5&logoColor=white"/><br/>
        <img alt="CSS3" src="https://img.shields.io/badge/CSS3-1572B6.svg?&style=flat&logo=CSS3&logoColor=white"/><br/>
        <img alt="JavaScript" src="https://img.shields.io/badge/JavaScript-F7DF1E.svg?&style=flat&logo=JavaScript&logoColor=black"/>
      </td>
      <td align="center">
        <img alt="Java" src="https://img.shields.io/badge/Java-007396.svg?&style=flat&logo=Java&logoColor=white"/><br/>
        <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-6DB33F.svg?&style=flat&logo=Spring%20Boot&logoColor=white"/><br/>
      </td>
      <td align="center">
        <img alt="OpenAI" src="https://img.shields.io/badge/OpenAI-412991.svg?&style=flat&logo=OpenAI&logoColor=white"/><br/>
        <img alt="GPT-3.5" src="https://img.shields.io/badge/GPT--3.5-10a37f.svg?&style=flat"/>
      </td>
    </tr>
  </tbody>
</table>

---
## 프로젝트 구조

```
├─ 📁 Front              # 프론트엔드 코드
│  ├─ index.html
│  ├─ style.css
│  └─ js/
│     ├─ message.js       # 메시지 출력 및 전송 로직
│     ├─ apiService.js    # fetch로 백엔드와 통신
│     ├─ modeManager.js   # 모드 전환 및 상태 관리
│     ├─ welcomeText.js   # 모드별 환영 메시지
│     └─ chatClear.js     # 채팅 초기화
│
├─ 📁 Back               # 백엔드 코드 (Spring Boot)
│  ├─ ChatBotController.java # 컨트롤러 클래스
│  ├─ ChatBotService.java	 # 서비스 클래스
│  └─ application.yml		 # API Key 보관
```
---

## 기능


### Frontend
**1.Backend에 사용자 메시지 전송 및 GPT 응답 출력**
```
// message.js
sendMessageToServer(userMessage, currentMode)
  .then(data => {
    addMessage(data.response, "bot"); // GPT 응답 출력
  });
```

사용자가 입력한 메시지를 fetch()로 서버에 전송하고, 받은 응답을 addMessage()로 채팅창에 출력하도록 구현했습니다. **(클라이언트 -> 서버)**



**2. 좌측 모드 전환 시 기존 대화 초기화 + 환영 메시지 출력**
![](https://velog.velcdn.com/images/ghkdehs/post/e3372d59-6ff3-4f01-aaf0-48643d0415f4/image.gif)

```
// modeManager.js
button.addEventListener("click", () => {
  currentMode = "idea"; // 예시
  clearChat(); // 기존 대화 초기화
  addMessage(getIntroMessage(currentMode), "bot"); // 모드별 인사말 출력
});
```

사이드바 버튼 클릭 시 모드를 변경하고, 이전에 대화하던 상태를 모두 삭제하고, 해당 모드의 가이드 메시지를 표시하도록 구현했습니다.



### Backend


**1. Front의 사용자 입력 데이터 JSON 구조 변환 후 요청처리 및 응답데이터 파싱**

```
// ChatBotService.java
requestBody.put("messages", history); // 누적 대화 전송
HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, request, Map.class);
```
프론트로 부터 전달받은 메시지를 GPT가 읽을 수 있는 구조(`"model"`, `"messages"`, `"max_tokens"` 등을 포함한 JSON 형태) 로 변환 후 API에 전달한 다음, 받은 응답에서 `content`만 추출해 사용자에게 반환하도록 구현했습니다. **(서버 -> 클라이언트)**

**2. 대화 히스토리 관리**
```
// ChatBotService.java
private final Map<String, List<Map<String, String>>> conversationHistories = new HashMap<>();
```
새로운 HashMap 객체를 생성 후 이곳에 대화를 누적 저장해서 GPT가 이전 맥락을 기억하며 응답하도록 했습니다. 이는 각 모드별로 해당 기능이 적용될 수 있도록 했습니다.


**3. 모드 변경 시 서버 측 대화 초기화**
```
// ChatBotService.java
public void resetConversation(String mode) {
    conversationHistories.remove(mode);
}
```
프론트에서 `/reset` 요청 시 해당 모드의 대화 내용을 서버에서 초기화해 GPT가 이전의 기록은 삭제하고 새로운 대화를 시작하게끔 구현했습니다.

---

## 트러블슈팅
### GPT 응답 파싱 관련 문제
**문제상황**

개발 초반에 OpenAI GPT API로부터 응답을 받은 후, 사용자에게 보여줄 "content" 값만 추출하려고 했지만, 초기에는 응답 구조를 명확히 이해하지 못해 아래와 같은 문제가 발생했습니다.

- 응답 데이터에서 choices → message → content 구조를 몰랐음

- 단순히 .get("content") 방식으로 접근해 null 또는 타입 에러 발생

- 형변환 관련 경고 (unchecked cast)로 인해 파싱 실패

---

**원인 분석**
GPT의 응답은 아래와 같이 중첩된 JSON 구조로 되어 있음을 확인했고, 이 중첩된 구조에 따른 형변환을 제대로 해야됨을 알게 되었습니다.
```
{
  "choices": [
    {
      "message": {
        "role": "assistant",
        "content": "내용"
      }
    }
  ]
}
```

---

**해결**

```
List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
String content = (String) message.get("content");
```

- "choices"는 배열 → List<Map<String, Object>>

- "message"는 객체 → Map<String, Object>

- "content"는 최종 텍스트 → String (명시적 형변환)

---

Java만 배운 상황에서 처음 프레임워크를 사용하여 만든 미니 프로젝트다 보니 모르는 것이 많았고, 무엇보다 자연어 처리 기반의 AI(GPT)와 통신하면서, 응답 구조가 일반 API보다 더 복잡하다는 점을 검색을 통해 알게 되었고,
특히 "choices" → "message" → "content"처럼 `중첩된 구조`를 정확히 파악하며, `형변환을 적절히 명시`해야 파싱할 수 있다는 것을 배우게 됐습니다.

