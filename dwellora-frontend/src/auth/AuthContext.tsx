import { createContext, useContext, useState, useCallback, type ReactNode } from "react";
import type { LoginResponse, Role } from "../api/types";

interface AuthUser {
  userId: number;
  apartmentId: number | null;
  fullName: string;
  role: Role;
  email: string;
}

interface AuthContextValue {
  user: AuthUser | null;
  isAuthenticated: boolean;
  login: (data: LoginResponse) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function loadStoredUser(): AuthUser | null {
  const raw = localStorage.getItem("dwellora_user");
  if (!raw) return null;
  try {
    return JSON.parse(raw) as AuthUser;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(loadStoredUser());

  const login = useCallback((data: LoginResponse) => {
    const authUser: AuthUser = {
      userId: data.userId,
      apartmentId: data.apartmentId,
      fullName: data.fullName,
      role: data.role,
      email: data.email,
    };
    localStorage.setItem("dwellora_token", data.token);
    localStorage.setItem("dwellora_user", JSON.stringify(authUser));
    setUser(authUser);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem("dwellora_token");
    localStorage.removeItem("dwellora_user");
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: !!user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
