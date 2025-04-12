function sendMessageToServer(userMessage) {
    // 백엔드 서버로 메시지 전송
    fetch('http://localhost:8080/chat', {  // 서버의 API 주소 입력하기
      method: 'POST',  // HTTP 요청 방법
      headers: {
        'Content-Type': 'application/json',  // JSON 데이터 형식으로 보낸다는 것을 명시
      },
      body: JSON.stringify({ message: userMessage })  // 사용자 메시지를 JSON 형태로 변환해서 전송
    })
    .then(response => response.json())  // fetch 작업이 완료 후 서버 응답을 JSON 형식으로 처리
    .then(data => {
      // 서버로부터 받은 응답 처리
      addMessage(data.response, "bot");  // data JSON 객체에서 'response' 속성의 값을 가져와서 addMessage 함수 호출 -> 즉 AI의 응답을 화면상에 출력
    })
    .catch(error => {
      console.error('에러 발생:', error);  // 에러 발생 시 콘솔에 에러 출력
    });
  }
  