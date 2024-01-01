const baseUrl = pm.environment.get("BaseUrl");

const postRequest = {
  url: baseUrl + "api/auth/signin",
  method: 'POST',
  header: {
    'Content-Type': 'application/json',
  },
  body: {
    mode: 'raw',
    raw: JSON.stringify({
        "username" : "admin",
        "password" : "abc123"
    })
  }
};

pm.sendRequest(postRequest, (error, response) => {
    if (error) {
        console.error(error);
    } else {
        if (response.code === 200) {
            const accessToken = JSON.parse(response.text()).access_token;
            pm.environment.set("Token", accessToken);
        } else {
            console.error("Unexpected response code:", response.code);
        }
    }
});
