import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import "../styles/household-settings.css";

export default function HouseholdSettings() {
  const nav = useNavigate();
  const { householdId } = useParams();

  const [household, setHousehold] = useState(null);
  const [members, setMembers] = useState([]);
  const [name, setName] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [leaving, setLeaving] = useState(false);
  const [closing, setClosing] = useState(false);
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");

  const isOwner = household?.role?.toUpperCase() === "OWNER";
  const activeMemberCount = members.length;
  const canClose = isOwner && activeMemberCount === 1;
  const canLeave = household && !isOwner;

  const memberLabel = useMemo(() => {
    if (loading) return "Loading members...";
    return `${activeMemberCount} active ${activeMemberCount === 1 ? "member" : "members"}`;
  }, [activeMemberCount, loading]);

  useEffect(() => {
    let ignore = false;

    async function loadSettings() {
      setLoading(true);
      setErr("");
      setMsg("");

      try {
        const [householdRes, membersRes] = await Promise.all([
          api.get(`/api/households/${householdId}`),
          api.get(`/api/households/${householdId}/members`),
        ]);

        if (!ignore) {
          setHousehold(householdRes.data);
          setMembers(membersRes.data || []);
          setName(householdRes.data?.name || "");
        }
      } catch {
        if (!ignore) setErr("Could not load household settings.");
      } finally {
        if (!ignore) setLoading(false);
      }
    }

    loadSettings();
    return () => {
      ignore = true;
    };
  }, [householdId]);

  async function renameHousehold(e) {
    e.preventDefault();

    const trimmed = name.trim();
    if (!trimmed) {
      setErr("Household name is required.");
      setMsg("");
      return;
    }

    setSaving(true);
    setErr("");
    setMsg("");

    try {
      const res = await api.patch(`/api/households/${householdId}`, { name: trimmed });
      setHousehold(res.data);
      setName(res.data?.name || trimmed);
      setMsg("Household name updated.");
    } catch {
      setErr("Could not update household name.");
    } finally {
      setSaving(false);
    }
  }

  async function leaveHousehold() {
    const ok = window.confirm("Leave this household? You will lose access to its grocery lists.");
    if (!ok) return;

    setLeaving(true);
    setErr("");
    setMsg("");

    try {
      await api.post(`/api/households/${householdId}/leave`);
      nav("/dashboard");
    } catch {
      setErr("Could not leave household.");
    } finally {
      setLeaving(false);
    }
  }

  async function closeHousehold() {
    const ok = window.confirm("Close this household? This will delete its grocery lists and cannot be undone.");
    if (!ok) return;

    setClosing(true);
    setErr("");
    setMsg("");

    try {
      await api.delete(`/api/households/${householdId}`);
      nav("/dashboard");
    } catch {
      setErr("Could not close household.");
    } finally {
      setClosing(false);
    }
  }

  return (
    <div className="page household-settings-page">
      <div className="container">
        <header className="topbar">
          <div className="titleBlock">
            <button className="linkBtn" onClick={() => nav(`/households/${householdId}`)}>
              Back
            </button>
            <div>
              <h2>Household Settings</h2>
              <div className="muted">{loading ? "Loading household..." : household?.name || "Household"}</div>
            </div>
          </div>
          <div className="top-actions">
            <button className="linkBtn" onClick={() => nav("/dashboard")}>
              Dashboard
            </button>
          </div>
        </header>

        {err && <div className="error">{err}</div>}
        {msg && <div className="success">{msg}</div>}

        <section className="settingsGrid">
          <div className="settingsPanel">
            <div className="panelHeader">
              <div>
                <h3>Details</h3>
                <div className="muted">Your role: {household?.role || "..."}</div>
              </div>
              <span className="badge">{memberLabel}</span>
            </div>

            {loading && <div className="empty">Loading settings...</div>}

            {!loading && isOwner && (
              <form className="settingsForm" onSubmit={renameHousehold}>
                <label htmlFor="householdName">Household name</label>
                <div className="formRow">
                  <input
                    id="householdName"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    maxLength={80}
                    placeholder="Household name"
                  />
                  <button className="primaryBtn" type="submit" disabled={saving}>
                    {saving ? "Saving..." : "Save"}
                  </button>
                </div>
              </form>
            )}

            {!loading && !isOwner && (
              <div className="readonlyBlock">
                <div className="readonlyLabel">Household name</div>
                <div className="readonlyValue">{household?.name}</div>
              </div>
            )}
          </div>

          <div className="settingsPanel dangerPanel">
            <div className="panelHeader">
              <div>
                <h3>{isOwner ? "Close Household" : "Leave Household"}</h3>
                <div className="muted">
                  {isOwner
                    ? "Owners can close only when no other active members remain."
                    : "Leaving removes your access to this household."}
                </div>
              </div>
            </div>

            {loading && <div className="empty">Checking membership...</div>}

            {!loading && canLeave && (
              <button className="dangerBtn" onClick={leaveHousehold} disabled={leaving}>
                {leaving ? "Leaving..." : "Leave household"}
              </button>
            )}

            {!loading && isOwner && !canClose && (
              <div className="statusNote">
                You cannot close this household while other active members still exist.
              </div>
            )}

            {!loading && canClose && (
              <button className="dangerBtn" onClick={closeHousehold} disabled={closing}>
                {closing ? "Closing..." : "Close household"}
              </button>
            )}
          </div>
        </section>
      </div>
    </div>
  );
}
