import { useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import "../styles/auth.css";

export default function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);

  async function onSubmit(e) {
    e.preventDefault();
    setErr("");
    setMessage("");
    setLoading(true);

    try {
      const res = await api.post("/api/auth/forgot-password", { email });
      setMessage(res.data.message);
      setEmail("");
    } catch {
      setErr("Could not request password reset. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-wrap">
      <form className="auth-card" onSubmit={onSubmit}>
        <h1 className="auth-title">Reset password</h1>
        <p className="auth-sub">Enter your email to request a reset link.</p>

        <label>Email</label>
        <input value={email} onChange={(e) => setEmail(e.target.value)} />

        {message && <div className="success">{message}</div>}
        {err && <div className="error">{err}</div>}

        <button type="submit" disabled={loading}>
          {loading ? "Sending..." : "Send reset link"}
        </button>

        <p className="muted">
          Remembered it? <Link to="/login">Log in</Link>
        </p>
      </form>
    </div>
  );
}
