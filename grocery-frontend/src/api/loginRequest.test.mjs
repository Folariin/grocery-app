import test from "node:test";
import assert from "node:assert/strict";
import { createLoginRequest, LOGIN_TIMEOUT_MS, loginErrorMessage } from "./loginRequest.js";

test("keeps one login pending with a 240-second deadline and returns its success", async () => {
  let complete;
  const calls = [];
  const login = createLoginRequest({ post: (...args) => {
    calls.push(args);
    return new Promise((resolve) => { complete = resolve; });
  } });
  const credentials = { email: "person@example.com", password: "test-password" };
  const controller = new AbortController();
  const first = login(credentials, controller.signal);
  assert.equal(login(credentials, controller.signal), first);
  assert.equal(calls.length, 1);
  assert.equal(calls[0][0], "/api/auth/login");
  assert.deepEqual(calls[0][1], credentials);
  assert.equal(calls[0][2].timeout, 240000);
  assert.equal(LOGIN_TIMEOUT_MS, 240000);
  assert.equal(calls[0][2].signal, controller.signal);
  complete({ data: { token: "test-token" } });
  assert.deepEqual(await first, { data: { token: "test-token" } });
});

test("failure does not retry automatically; manual retry creates one new request", async () => {
  let calls = 0;
  const login = createLoginRequest({ post: () => {
    calls++;
    return Promise.reject({ code: "ECONNABORTED" });
  } });
  await assert.rejects(login({}));
  assert.equal(calls, 1);
  await assert.rejects(login({}));
  assert.equal(calls, 2);
});

test("preserves invalid-credential errors and explains connection failures", () => {
  assert.equal(loginErrorMessage({ response: { data: { error: "INVALID_CREDENTIALS" } } }), "Invalid email or password.");
  assert.equal(loginErrorMessage({ response: { status: 401 } }), "Invalid email or password.");
  for (const error of [{ code: "ECONNABORTED" }, { code: "ERR_NETWORK" }, { response: { status: 503 } }]) {
    assert.match(loginErrorMessage(error), /check your connection and try again/);
  }
  assert.equal(loginErrorMessage({ response: { status: 400 } }), "Login failed. Please try again.");
});

test("failed authentication releases the duplicate-request guard for a manual retry", async () => {
  let rejectRequest;
  let calls = 0;
  const login = createLoginRequest({ post: () => {
    calls++;
    if (calls === 1) return new Promise((_resolve, reject) => { rejectRequest = reject; });
    return Promise.resolve({ data: { token: "retry-token" } });
  } });
  const pending = login({ email: "person@example.com", password: "incorrect" });
  assert.equal(login({}), pending);
  assert.equal(calls, 1);
  rejectRequest({ response: { status: 401 } });
  await assert.rejects(pending);
  assert.equal(calls, 1);
  assert.deepEqual(await login({ email: "person@example.com", password: "corrected" }),
    { data: { token: "retry-token" } });
  assert.equal(calls, 2);
});
