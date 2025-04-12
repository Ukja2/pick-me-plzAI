import { clearChat } from "./chatClear.js";
// 기본 모드 설정
let currentMode = "beginner";

// 모드 변경 함수
export function setupModeSwitching() {
    document.querySelectorAll(".nav-item").forEach(button => {
        button.addEventListener("click", () => {
            document.querySelectorAll(".nav-item").forEach(btn => btn.classList.remove("active"));
            button.classList.add("active"); // 모든 버튼의 active 클래스 제거 후 클릭한 버튼에만 추가, 토글 x

            if (button.textContent.includes("처음부터")) {
                currentMode = "full";
            } else if (button.textContent.includes("내 문장 다듬기")) {
                currentMode = "edit";
            } else if (button.textContent.includes("아이디어 얻기")) {
                currentMode = "idea";
            } else if (button.textContent.includes("기업 맞춤 수정")) {
                currentMode = "custom";
            }

            clearChat();
        })
        
    });

}

// 현재 모드를 가져오는 함수
export function getCurrentMode() {
    return currentMode;
}
