import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import "../styles/invites.css";

export default function Invites() {
  const nav = useNavigate();
  const [invites, setInvites] = useState([]);
  const [err, setErr] = useState("");

  useEffect(() => {
    let ignore = false;

    async function fetchInvites() {
      setErr("");
      try {
        const res = await api.get("/api/invites");
        if (!ignore) setInvites(res.data);
      } catch {
        if (!ignore) setErr("Could not load invites.");
      }
    }

    fetchInvites();
    return () => (ignore = true);
  }, []);

  async function accept(token) {
    setErr("");
    try {
      await api.post(`/api/invites/${token}/accept`);
      // remove from UI instantly
      setInvites((prev) => prev.filter((i) => i.token !== token));
      // optionally take them to dashboard to see the new household
      // nav("/dashboard");
    } catch {
      setErr("Could not accept invite.");
    }
  }

  return (
    <div className="page invites-page">
      <div className="container">
        <header className="topbar">
          <div className="titleBlock">
            <button className="linkBtn" onClick={() => nav(-1)}>
              ← Back
            </button>
            <div>
              <h2>Invites</h2>
              <div className="muted">Accept a household invite</div>
            </div>
          </div>
        </header>

        {err && <div className="error">{err}</div>}

        <div className="inviteList">
          {invites.map((i) => (
            <div key={i.inviteId ?? i.token} className="inviteCard">
              <div className="inviteMain">
                <div className="inviteTitle">{i.householdName ?? "Household Invite"}</div>
                <div className="muted inviteMeta">
                  {i.email ? `Sent to: ${i.email}` : ""}
                  {i.expiresAt ? ` • Expires: ${new Date(i.expiresAt).toLocaleString()}` : ""}
                </div>
              </div>

              <button className="primaryBtn" onClick={() => accept(i.token)}>
                Accept
              </button>
            </div>
          ))}

          {invites.length === 0 && (
            <div className="empty">No pending invites.</div>
          )}
        </div>
      </div>
    </div>
  );
}
