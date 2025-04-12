import { clearChat } from "./chatClear.js";
import { getModeIntroMessage } from "./welcomeText.js";
import { addMessage } from "./message.js";

// 기본 모드 설정
let currentMode = "beginner";

// 모드 변경 함수
export function setupModeSwitching() {
    document.querySelectorAll(".nav-item").forEach(button => {
        button.addEventListener("click", () => {
            document.querySelectorAll(".nav-item").forEach(btn => btn.classList.remove("active"));
            button.classList.add("active"); // 모든 버튼의 active 클래스 제거 후 클릭한 버튼에만 추가, 토글 x

            let selectedMode = ""; // 선택된 모드 변수 선언

            if (button.textContent.includes("처음부터")) {
                selectedMode = "full";
            } else if (button.textContent.includes("내 문장 다듬기")) {
                selectedMode = "edit";
            } else if (button.textContent.includes("아이디어 얻기")) {
                selectedMode = "idea";
            } else if (button.textContent.includes("기업 맞춤 수정")) {
                selectedMode = "custom";
            }

            currentMode = selectedMode; // 현재 모드 업데이트

            // 탭 전환 시 백엔드에 초기화 요청 보내기
            fetch(`http://localhost:8080/chat/reset?mode=${selectedMode}`, {
                method: "POST"
            });

            clearChat(); // 채팅창 초기화
            const message = getModeIntroMessage(currentMode); // 인사말 불러오기
            addMessage(message, "bot"); // 인사말 출력
        });
    });
}

// 현재 모드를 가져오는 함수
export function getCurrentMode() {
    return currentMode;
}    