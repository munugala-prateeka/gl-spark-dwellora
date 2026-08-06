import { Navigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export default function RoleRedirect() {
  const { user, isAuthenticated } = useAuth();

  if (!isAuthenticated || !user) return <Navigate to="/login" replace />;
  if (user.role === "PLATFORM_ADMIN") return <Navigate to="/admin" replace />;
  if (user.role === "MANAGER") return <Navigate to="/manager" replace />;
  return <Navigate to="/resident" replace />;
}
