import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios"; // change to "../api/api" if that's your file name
import "../styles/household.css";

export default function Household() {
  const nav = useNavigate();
  const { householdId } = useParams();

  const [lists, setLists] = useState([]);
  const [members, setMembers] = useState([]);
  const [name, setName] = useState("");
  const [err, setErr] = useState("");
  const [memberErr, setMemberErr] = useState("");

  const [inviteEmail, setInviteEmail] = useState("");
  const [inviteMsg, setInviteMsg] = useState("");

  // Load household details
  useEffect(() => {
    let ignore = false;

    async function fetchLists() {
      setErr("");
      try {
        const res = await api.get(`/api/households/${householdId}/lists`);
        if (!ignore) setLists(res.data);
      } catch {
        if (!ignore) setErr("Could not load lists.");
      }
    }

    async function fetchMembers() {
      setMemberErr("");
      try {
        const res = await api.get(`/api/households/${householdId}/members`);
        if (!ignore) setMembers(res.data);
      } catch {
        if (!ignore) setMemberErr("Could not load members.");
      }
    }

    fetchLists();
    fetchMembers();
    return () => {
      ignore = true;
    };
  }, [householdId]);

  // Create a new grocery list
  async function createList(e) {
    e.preventDefault();
    const trimmed = name.trim();
    if (!trimmed) return;

    setErr("");
    try {
      const res = await api.post(`/api/households/${householdId}/lists`, {
        name: trimmed,
      });

      // Add newest first
      setLists((prev) => [res.data, ...prev]);
      setName("");
    } catch {
      setErr("Could not create list.");
    }
  }

  // Create an invite for this household (OWNER on backend should be enforced)
  async function sendInvite(e) {
    e.preventDefault();
    const email = inviteEmail.trim();
    if (!email) return;

    setInviteMsg("");
    try {
      const res = await api.post(`/api/households/${householdId}/invites`, {
        email,
      });

      const token = res.data?.token; // optional if your backend returns it
      setInviteMsg(token ? `Invite created. Token: ${token}` : "Invite created.");
      setInviteEmail("");
    } catch {
      setInviteMsg("Could not send invite.");
    }
  }

  return (
    <div className="page">
      <div className="container">
        <header className="topbar">
          <div className="titleBlock">
            <button className="linkBtn" onClick={() => nav("/dashboard")}>
              ← Back
            </button>
            <div>
              <h2>Grocery Lists</h2>
              <div className="muted">Household: {householdId}</div>
            </div>
          </div>

          <div className="top-actions">
            <button className="linkBtn" onClick={() => nav("/invites")}>
              Invites
            </button>
          </div>
        </header>

        {/* Create list */}
        <form className="create-card" onSubmit={createList}>
          <input
            placeholder="New list name (e.g., Weekly Groceries)..."
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
          <button className="primaryBtn" type="submit">
            Create
          </button>
        </form>

        {/* Invite member */}
        <form className="create-card" onSubmit={sendInvite} style={{ marginTop: 12 }}>
          <input
            placeholder="Invite member by email..."
            value={inviteEmail}
            onChange={(e) => setInviteEmail(e.target.value)}
          />
          <button className="primaryBtn" type="submit">
            Send Invite
          </button>
        </form>

        {inviteMsg && (
          <div className="muted" style={{ marginTop: 8 }}>
            {inviteMsg}
          </div>
        )}

        {err && <div className="error">{err}</div>}

        {/* Members */}
        <section className="sectionBlock">
          <div className="sectionHeader">
            <div>
              <h3>Members</h3>
              <div className="muted">Everyone currently in this household</div>
            </div>
            <span className="badge">{members.length}</span>
          </div>

          {memberErr && <div className="error">{memberErr}</div>}

          <div className="memberList">
            {members.map((member) => (
              <div key={member.id} className="memberRow">
                <div className="memberMain">
                  <div className="memberName">{member.displayName}</div>
                  <div className="muted">{member.email}</div>
                </div>
                <div className="memberMeta">
                  <span className="badge">{member.role}</span>
                  <div className="muted">
                    {member.joinedAt
                      ? `Joined ${new Date(member.joinedAt).toLocaleDateString()}`
                      : member.status}
                  </div>
                </div>
              </div>
            ))}

            {members.length === 0 && !memberErr && (
              <div className="empty">No members found.</div>
            )}
          </div>
        </section>

        {/* Lists */}
        <div className="grid" style={{ marginTop: 14 }}>
          {lists.map((l) => (
            <div
              key={l.id}
              className="card clickable"
              onClick={() => nav(`/lists/${l.id}`)}
            >
              <div className="card-title">{l.name}</div>
              <div className="muted">Open list</div>
            </div>
          ))}

          {lists.length === 0 && (
            <div className="empty">No lists yet. Create your first one 👆</div>
          )}
        </div>
      </div>
    </div>
  );
}
