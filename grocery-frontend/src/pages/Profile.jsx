import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import "../styles/profile.css";

export default function Profile() {
  const nav = useNavigate();
  const [profile, setProfile] = useState(null);
  const [displayName, setDisplayName] = useState("");
  const [err, setErr] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    let ignore = false;

    async function fetchProfile() {
      setErr("");
      setLoading(true);
      try {
        const res = await api.get("/api/users/me");
        if (!ignore) {
          setProfile(res.data);
          setDisplayName(res.data.displayName || "");
        }
      } catch {
        if (!ignore) setErr("Could not load profile.");
      } finally {
        if (!ignore) setLoading(false);
      }
    }

    fetchProfile();
    return () => {
      ignore = true;
    };
  }, []);

  async function saveProfile(e) {
    e.preventDefault();
    setErr("");
    setMessage("");

    const trimmed = displayName.trim();
    if (trimmed.length < 2 || trimmed.length > 60) {
      setErr("Display name must be between 2 and 60 characters.");
      return;
    }

    setSaving(true);
    try {
      const res = await api.patch("/api/users/me", { displayName: trimmed });
      setProfile(res.data);
      setDisplayName(res.data.displayName || "");
      setMessage("Profile updated.");
    } catch {
      setErr("Could not update profile.");
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="page profile-page">
      <div className="container">
        <header className="topbar">
          <div className="titleBlock">
            <button className="linkBtn" onClick={() => nav("/dashboard")}>
              ← Back
            </button>
            <div>
              <h2>Profile</h2>
              <div className="muted">Manage your account name</div>
            </div>
          </div>
        </header>

        {loading && <div className="empty">Loading profile...</div>}
        {err && <div className="error">{err}</div>}
        {message && <div className="success">{message}</div>}

        {!loading && profile && (
          <form className="profile-card" onSubmit={saveProfile}>
            <div className="profile-field">
              <label>Display name</label>
              <input
                value={displayName}
                onChange={(e) => setDisplayName(e.target.value)}
              />
            </div>

            <div className="profile-field">
              <label>Email</label>
              <input value={profile.email || ""} readOnly />
              <div className="muted">Email cannot be changed here.</div>
            </div>

            <button className="primaryBtn" type="submit" disabled={saving}>
              {saving ? "Saving..." : "Save profile"}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}
