import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from "../api/axios";
import "../styles/auth.css";

export default function Login() {
  const nav = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [err, setErr] = useState("");

  async function onSubmit(e) {
    e.preventDefault();
    setErr("");

    try {
      const res = await api.post("/api/auth/login", { email, password });
      localStorage.setItem("token", res.data.token);
      nav("/dashboard");
    } catch (error) {
      const code = error?.response?.data?.error;

      if (code === "INVALID_CREDENTIALS") {
        setErr("Invalid email or password.");
      } else {
        setErr("Login failed. Please try again.");
      }
    }
  }

  return (
    <div className="auth-wrap">
      <form className="auth-card" onSubmit={onSubmit}>
        <div className="auth-header">
          <h1 className="auth-title">Grocery App</h1>
          <p className="auth-sub">Log in to continue</p>
        </div>

        <div className="auth-field">
          <label>Email</label>
          <input value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>

        <div className="auth-field">
          <label>Password</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>

        <div className="auth-row">
          <Link to="/forgot-password">Forgot password?</Link>
        </div>

        {err && <div className="error">{err}</div>}

        <button type="submit">Log in</button>

        <p className="muted">
          No account? <Link to="/signup">Sign up</Link>
        </p>
      </form>
    </div>
  );
}
