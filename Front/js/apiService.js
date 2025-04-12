
export function sendMessageToServer(userMessage, mode) {
    return fetch('http://localhost:8080/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ 
        message: userMessage,
        mode: mode
      })
    })
    .then(response => response.json()); // 백엔드로부터 받은 JSON 문자열을 JS객체로 변환
    
  }
  