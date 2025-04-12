// 기본 모드 설정
let currentMode = "beginner";

// 모드 변경 함수
export function setupModeSwitching() {
    document.querySelectorAll(".nav-item").forEach(button => {
        button.addEventListener("click", () => {
            document.querySelectorAll(".nav-item").forEach(btn => btn.classList.remove("active"));
            button.classList.add("active"); // 모든 버튼의 active 클래스 제거 후 클릭한 버튼에만 추가, 토글 x

            if (button.textContent.includes("처음부터")) {
                currentMode = "beginner";
            } else if (button.textContent.includes("고수처럼")) {
                currentMode = "advanced";
            }

        });
    });
}

// 현재 모드를 가져오는 함수
export function getCurrentMode() {
    return currentMode;
}
