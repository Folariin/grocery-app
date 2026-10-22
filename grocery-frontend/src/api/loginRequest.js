export const LOGIN_TIMEOUT_MS = 240000;

// Share an in-flight request so rapid submissions cannot resend credentials.
export function createLoginRequest(client) {
  let pending;
  return (credentials, signal) => {
    if (!pending) {
      pending = client.post("/api/auth/login", credentials, {
        timeout: LOGIN_TIMEOUT_MS,
        signal,
      }).finally(() => { pending = undefined; });
    }
    return pending;
  };
}

export function loginErrorMessage(error) {
  if (error?.response?.data?.error === "INVALID_CREDENTIALS" || error?.response?.status === 401) {
    return "Invalid email or password.";
  }
  if (!error?.response || error.response.status >= 500) {
    return "We couldn't connect to the server. Please check your connection and try again.";
  }
  return "Login failed. Please try again.";
}
