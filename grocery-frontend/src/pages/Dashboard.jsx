import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import "../styles/dashboard.css";

export default function Dashboard() {
  const nav = useNavigate();
  const [households, setHouseholds] = useState([]);
  const [name, setName] = useState("");
  const [err, setErr] = useState("");

  useEffect(() => {
    let ignore = false;

    async function fetchHouseholds() {
      setErr("");
      try {
        const res = await api.get("/api/households");
        if (!ignore) {
          setHouseholds(res.data);
        }
      } catch {
        if (!ignore) {
          setErr("Could not load households.");
        }
      }
    }

    fetchHouseholds();

    return () => {
      ignore = true;
    };
  }, []);

  async function createHousehold(e) {
    e.preventDefault();
    setErr("");

    try {
      const res = await api.post("/api/households", { name });

      setHouseholds((prev) => [res.data, ...prev]); // add to top
      setName("");
    } catch {
      setErr("Could not create household.");
    }
  }

  function logout() {
    localStorage.removeItem("token");
    nav("/login");
  }

  return (
    <div className="page">
      <div className="container">
        <header className="topbar">
          <h2>My Households</h2>
          <div className="top-actions">
            <button className="linkBtn" onClick={() => nav("/invites")}>
              Invites
            </button>
            <button className="linkBtn" onClick={logout}>
              Logout
            </button>
          </div>
        </header>

        <form className="create-card" onSubmit={createHousehold}>
          <input
            placeholder="New household name..."
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
          <button className="primaryBtn" type="submit">
            Create
          </button>
        </form>

        {err && <div className="error">{err}</div>}

        <div className="grid">
          {households.map((h) => (
            <div
              key={h.id}
              className="card"
              onClick={() => nav(`/households/${h.id}`)}
              style={{ cursor: "pointer" }}
            >
              <div className="card-title">{h.name}</div>
              <span className="badge">{h.role}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
