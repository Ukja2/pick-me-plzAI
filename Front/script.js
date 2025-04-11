// 페이지가 로딩되면 실행됨
// DOMContentLoaded 이벤트는 HTML 문서가 완전히 로드된 후에 발생
window.addEventListener('DOMContentLoaded', () => {
    
    // 챗봇 인사 메시지 보여주기
    addMessage("안녕하세요! 무엇을 도와드릴까요? 😊", "bot");
});

// 메시지를 화면에 추가하는 함수
function addMessage(text, sender) {
    const chatMessages = document.getElementById("chat-messages"); // html에서 chat-messages채팅창

    const messages = document.createElement("div"); 
    messages.classList.add("message", sender); // 클래스: message + bot 또는 user
    messages.textContent = text; // 메시지 내용 넣기

    chatMessages.appendChild(messages); // 채팅창에 추가
    chatMessages.scrollTop = chatMessages.scrollHeight; // 자동으로 아래로 스크롤
}

// 전송 버튼 눌렀을 때 사용자 메시지 출력
document.getElementById("send-button").addEventListener("click", () => {
    const userInput = document.getElementById("user-input");
    const userMessage = userInput.value.trim(); // 입력한 내용 가져오기 (양쪽 공백 제거)

    if (userMessage !== "") {
        addMessage(userMessage, "user");  // 화면에 유저 메시지 추가
        userInput.value = "";  // 입력창 비우기
    }
});
