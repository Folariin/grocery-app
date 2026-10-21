import test from "node:test";
import assert from "node:assert/strict";
import { checkBackend } from "./checkBackend.js";

test("uses configured URL without credentials and accepts UP", async () => {
  await checkBackend("https://api.example.com/", { fetchImpl: async (url, options) => {
    assert.equal(url, "https://api.example.com/api/health");
    assert.equal(options.credentials, "omit");
    assert.equal(options.cache, "no-store");
    assert.equal(options.headers, undefined);
    return { ok: true, json: async () => ({ status: "UP" }) };
  } });
});

test("rejects unsuccessful or unexpected responses", async () => {
  for (const response of [
    { ok: false },
    { ok: true, json: async () => ({ status: "DOWN" }) },
    { ok: true, json: async () => { throw new Error("Invalid JSON"); } },
  ]) {
    await assert.rejects(checkBackend("https://api.example.com", { fetchImpl: async () => response }));
  }
});

const pendingFetch = (_url, { signal }) => new Promise((_resolve, reject) => {
  if (signal.aborted) reject(new Error("Aborted"));
  else signal.addEventListener("abort", () => reject(new Error("Aborted")), { once: true });
});

test("stops waiting at the deadline", async () => {
  await assert.rejects(checkBackend("https://api.example.com", { timeoutMs: 5, fetchImpl: pendingFetch }));
});

test("cancels an obsolete attempt", async () => {
  const controller = new AbortController();
  const request = checkBackend("https://api.example.com", { signal: controller.signal, fetchImpl: pendingFetch });
  controller.abort();
  await assert.rejects(request);
});
