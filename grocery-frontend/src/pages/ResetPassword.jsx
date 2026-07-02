import { useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import api from "../api/axios";
import "../styles/auth.css";

export default function ResetPassword() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token") || "";

  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [message, setMessage] = useState("");
  const [err, setErr] = useState(token ? "" : "Reset token is missing.");
  const [loading, setLoading] = useState(false);

  async function onSubmit(e) {
    e.preventDefault();
    setErr("");
    setMessage("");

    if (!token) {
      setErr("Reset token is missing.");
      return;
    }

    if (password !== confirmPassword) {
      setErr("Passwords do not match.");
      return;
    }

    setLoading(true);
    try {
      const res = await api.post("/api/auth/reset-password", {
        token,
        password,
        confirmPassword,
      });
      setMessage(res.data.message);
      setPassword("");
      setConfirmPassword("");
    } catch {
      setErr("Reset link is invalid or expired.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-wrap">
      <form className="auth-card" onSubmit={onSubmit}>
        <h1 className="auth-title">Choose new password</h1>
        <p className="auth-sub">Enter and confirm your new password.</p>

        <label>New password</label>
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <label>Confirm password</label>
        <input
          type="password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
        />

        {message && <div className="success">{message}</div>}
        {err && <div className="error">{err}</div>}

        <button type="submit" disabled={loading || !token}>
          {loading ? "Resetting..." : "Reset password"}
        </button>

        <p className="muted">
          Back to <Link to="/login">login</Link>
        </p>
      </form>
    </div>
  );
}
