import { useState } from "react";
import { Box, alpha, Paper, TextField, Button, Typography, Alert, Stack } from "@mui/material";
import LockResetIcon from "@mui/icons-material/LockReset";
import { useSearchParams, useNavigate } from "react-router-dom";
import { userApi } from "../api/userApi";

const CREAM = "#FAF5EC";
const PAPER = "#FFFDF9";
const MOSS = "#2E3A25";
const MOSS_LIGHT = "#6B7A5C";
const TERRA = "#C05F3C";
const TERRA_DARK = "#A24A2C";

const PASSWORD_PATTERN = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&#]).{8,}$/;

export default function ActivatePage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token") ?? "";
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (!token) {
      setError("Missing activation token. Please use the link from your email.");
      return;
    }
    if (!PASSWORD_PATTERN.test(password)) {
      setError("Password must be at least 8 characters and include a letter, a number, and a special character (@$!%*?&#).");
      return;
    }
    if (password !== confirm) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);
    try {
      await userApi.activate({ token, newPassword: password });
      setSuccess(true);
      setTimeout(() => navigate("/login"), 2500);
    } catch (err: any) {
      setError(err?.response?.data?.details || "This activation link is invalid or has expired.");
    } finally {
      setLoading(false);
    }
  };

  const inputSx = {
    "& label": { color: MOSS_LIGHT },
    "& label.Mui-focused": { color: TERRA },
    "& .MuiOutlinedInput-root": {
      "&.Mui-focused fieldset": { borderColor: TERRA, borderWidth: 2 },
    },
  };

  return (
    <Box sx={{ minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center", bgcolor: CREAM, px: 2 }}>
      <Paper sx={{ p: { xs: 3, sm: 5 }, maxWidth: 420, width: "100%", bgcolor: PAPER, borderRadius: 3, border: `1px solid #E6DCC9`, boxShadow: "0 12px 36px rgba(46,58,37,0.06)" }}>
        <Stack spacing={1} sx={{ alignItems: "center", mb: 3 }}>
          <LockResetIcon sx={{ fontSize: 44, color: TERRA }} />
          <Typography variant="h5" sx={{ fontWeight: 800, color: MOSS }}>
            Activate your account
          </Typography>
          <Typography variant="body2" sx={{ color: MOSS_LIGHT, textAlign: "center" }}>
            Set a password to finish setting up your Dwellora account
          </Typography>
        </Stack>

        {error && <Alert severity="error" sx={{ mb: 2, borderRadius: 2 }}>{error}</Alert>}
        {success && (
          <Alert severity="success" sx={{ mb: 2, borderRadius: 2 }}>
            Account activated. Redirecting to login...
          </Alert>
        )}

        <form onSubmit={handleSubmit}>
          <Stack spacing={2.5}>
            <TextField
              label="New password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              fullWidth
              disabled={success}
              helperText="At least 8 characters, with a letter, a number, and a special character (@$!%*?&#)"
              sx={inputSx}
            />
            <TextField
              label="Confirm password"
              type="password"
              value={confirm}
              onChange={(e) => setConfirm(e.target.value)}
              required
              fullWidth
              disabled={success}
              sx={inputSx}
            />
            <Button
              type="submit"
              variant="contained"
              size="large"
              disabled={loading || success}
              fullWidth
              sx={{
                py: 1.5,
                borderRadius: 2,
                fontWeight: 800,
                bgcolor: TERRA,
                boxShadow: `0 8px 20px ${alpha(TERRA, 0.2)}`,
                "&:hover": { bgcolor: TERRA_DARK },
              }}
            >
              {loading ? "Activating..." : "Activate account"}
            </Button>
          </Stack>
        </form>
      </Paper>
    </Box>
  );
}