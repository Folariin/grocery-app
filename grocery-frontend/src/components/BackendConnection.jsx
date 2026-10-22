import { useEffect, useState } from "react";
import api from "../api/axios";
import { checkBackend } from "../api/checkBackend";
import "../styles/connection.css";

export default function BackendConnection({ children }) {
  const [status, setStatus] = useState("checking");
  const [attempt, setAttempt] = useState(0);

  useEffect(() => {
    const controller = new AbortController();
    const reveal = setTimeout(() => setStatus("connecting"), 400);
    checkBackend(api.defaults.baseURL, { signal: controller.signal })
      .then(() => {
        if (!controller.signal.aborted) setStatus("ready");
      })
      .catch(() => {
        if (!controller.signal.aborted) setStatus("error");
      })
      .finally(() => clearTimeout(reveal));
    return () => {
      clearTimeout(reveal);
      controller.abort();
    };
  }, [attempt]);

  if (status === "ready") return children;
  if (status === "checking") return null;
  const failed = status === "error";
  return (
    <main className="connection-page">
      <section className="connection-content" aria-live="polite" aria-busy={!failed}>
        <div className="connection-brand">Grocery App</div>
        {!failed && <span className="connection-spinner" aria-hidden="true" />}
        <h1>{failed ? "Unable to connect" : "Connecting to server..."}</h1>
        <p>{failed
          ? "The server is taking longer than expected. Please check your connection and try again."
          : "The demo server may take approximately a minute to start after inactivity. Thanks for your patience."}</p>
        {failed && (
          <button type="button" onClick={() => {
            setStatus("checking");
            setAttempt((value) => value + 1);
          }}>Try again</button>
        )}
      </section>
    </main>
  );
}
