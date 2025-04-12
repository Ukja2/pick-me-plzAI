import { setupModeSwitching, getCurrentMode } from "./modeManager.js";
import { sendMessageToServer } from "./apiService.js";


// 페이지가 로딩되면 실행됨
// DOMContentLoaded 이벤트는 HTML 문서가 완전히 로드된 후에 발생
window.addEventListener('DOMContentLoaded', () => {
    
    setupModeSwitching(); // 모드 버튼 클릭 시 모드 변경

    // 챗봇 인사 메시지 보여주기
    addMessage("안녕하세요! 무엇을 도와드릴까요? 😊", "bot");
});

// 메시지를 화면에 추가 함수
function addMessage(text, sender) {
    const chatMessages = document.getElementById("chat-messages"); // html에서 chat-messages채팅창

    const messages = document.createElement("div"); 
    messages.classList.add("message", sender); // 클래스: message + bot 또는 user
    messages.textContent = text; // 메시지 내용 넣기

    chatMessages.appendChild(messages); // 채팅창에 추가
    chatMessages.scrollTop = chatMessages.scrollHeight; // 자동으로 아래로 스크롤
}

// 사용자 메시지 전송 함수
function sendMessage(){
    const userInput = document.getElementById("user-input"); // html에서 user-input
    const userMessage = userInput.value.trim(); // 사용자가 입력한 메시지, 양쪽 끝 공백제거

    if (userMessage !== ""){
        addMessage(userMessage, "user");
        userInput.value = ""; // 입력창 초기화  
    }

    const currentMode = getCurrentMode();

    // 백엔드에 메시지 전송 (리팩토링)
    sendMessageToServer(userMessage, currentMode)
        .then(data => {
            addMessage(data.response, "bot"); // GPT 응답 출력
        })
        .catch(error => {
            console.error("GPT 응답 에러:", error);
            addMessage("⚠️ 서버에서 응답을 받지 못했습니다.", "bot"); // 에러 시 메시지 표시
        });
}

// 버튼 클릭 시 메시지 전송
document.getElementById("send-button").addEventListener("click", (event) => {
    sendMessage(); // 메시지 전송
});

// 엔터 클릭 시 메시지 전송
document.getElementById("user-input").addEventListener("keypress", (event) => {
    if (event.key === "Enter") {
        sendMessage();  // Enter 키로 메시지 전송
    } 
});