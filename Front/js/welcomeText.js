export function getModeIntroMessage(mode) {
    switch (mode) {
        case "full":
            return "📝 지금부터 자기소개서를 처음부터 함께 작성해볼게요!";
        case "edit":
            return "✂️ 문장을 붙여주시면 자연스럽고 설득력 있게 다듬어드릴게요!";
        case "idea":
            return "💡 어떤 내용을 쓸지 함께 아이디어를 떠올려봐요!";
        case "custom":
            return "🏢 지원하는 기업/직무를 알려주시면 맞춤 자소서를 도와드릴게요!";
        default:
            return "안녕하세요! 무엇을 도와드릴까요? 😊";
    }
}