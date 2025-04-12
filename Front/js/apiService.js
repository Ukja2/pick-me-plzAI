
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
    .then(response => response.json()); 
  }
  