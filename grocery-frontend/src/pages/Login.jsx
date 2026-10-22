import { useEffect, useRef, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from "../api/axios";
import { createLoginRequest, loginErrorMessage } from "../api/loginRequest";
import "../styles/auth.css";

export default function Login() {
  const nav = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [err, setErr] = useState("");
  const [pending, setPending] = useState(false);
  const [connecting, setConnecting] = useState(false);
  const [retry, setRetry] = useState(false);
  const request = useRef(null);
  const active = useRef(null);
  if (!request.current) request.current = createLoginRequest(api);

  useEffect(() => () => {
    active.current?.abort();
  }, []);

  async function onSubmit(e) {
    e.preventDefault();
    if (active.current) return;
    const controller = new AbortController();
    active.current = controller;
    setErr("");
    setRetry(false);
    setPending(true);
    setConnecting(false);
    const reveal = setTimeout(() => {
      if (!controller.signal.aborted) setConnecting(true);
    }, 800);

    try {
      const res = await request.current({ email, password }, controller.signal);
      if (controller.signal.aborted) return;
      localStorage.setItem("token", res.data.token);
      nav("/dashboard");
    } catch (error) {
      if (controller.signal.aborted) return;
      setErr(loginErrorMessage(error));
      setRetry(!error?.response || error.response.status >= 500);
    } finally {
      clearTimeout(reveal);
      if (active.current === controller) active.current = null;
      if (!controller.signal.aborted) {
        setPending(false);
        setConnecting(false);
      }
    }
  }

  return (
    <div className="auth-wrap">
      <form className="auth-card" onSubmit={onSubmit} aria-busy={pending}>
        <div className="auth-header">
          <h1 className="auth-title">Grocery App</h1>
          <p className="auth-sub">Log in to continue</p>
        </div>

        <div className="auth-field">
          <label htmlFor="login-email">Email</label>
          <input id="login-email" autoComplete="username" disabled={pending} value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>

        <div className="auth-field">
          <label htmlFor="login-password">Password</label>
          <input
            type="password"
            id="login-password"
            autoComplete="current-password"
            disabled={pending}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>

        <div className="auth-row">
          <Link to="/forgot-password">Forgot password?</Link>
        </div>

        {connecting && (
          <div className="login-connection" role="status">
            <strong>Connecting to server...</strong>
            <p>The demo server may take approximately a minute to start after inactivity. Your login is still being processed.</p>
          </div>
        )}
        {err && <div className="error" role="alert">{err}</div>}

        <button type="submit" disabled={pending}>
          {pending ? (connecting ? "Connecting..." : "Logging in...") : retry ? "Try again" : "Log in"}
        </button>

        <p className="muted">
          No account? <Link to="/signup">Sign up</Link>
        </p>
      </form>
    </div>
  );
}

