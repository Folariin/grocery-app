import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Signup from "./pages/Signup";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Household from "./pages/Household";
import ListPage from "./pages/ListPage";
import Invites from "./pages/Invites";

function RequireAuth({ children }) {
  const token = localStorage.getItem("token");
  return token ? children : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route
          path="/dashboard"
          element={
            <RequireAuth>
              <Dashboard />
            </RequireAuth>
          }
        />
        <Route
          path="/households/:householdId"
          element={
            <RequireAuth>
              <Household />
            </RequireAuth>
          }
        />
        <Route
          path="/lists/:listId"
          element={
            <RequireAuth>
              <ListPage />
            </RequireAuth>
          }
        />
        <Route
          path="/invites"
          element={
            <RequireAuth>
              <Invites />
            </RequireAuth>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}
