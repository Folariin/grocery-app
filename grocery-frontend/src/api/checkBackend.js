export async function checkBackend(baseURL, { signal, timeoutMs = 90000, fetchImpl = fetch } = {}) {
  const controller = new AbortController();
  const abort = () => controller.abort();
  if (signal?.aborted) controller.abort();
  signal?.addEventListener("abort", abort, { once: true });
  const timeout = setTimeout(abort, timeoutMs);
  try {
    const response = await fetchImpl(baseURL.replace(/\/+$/, "") + "/api/health", {
      signal: controller.signal,
      cache: "no-store",
      credentials: "omit",
    });
    if (!response.ok) throw new Error("Backend unavailable");
    const body = await response.json();
    if (body.status !== "UP") throw new Error("Backend unavailable");
  } finally {
    clearTimeout(timeout);
    signal?.removeEventListener("abort", abort);
  }
}
