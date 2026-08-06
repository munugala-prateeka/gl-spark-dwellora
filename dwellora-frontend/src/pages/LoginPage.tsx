import { useState } from "react";
import {
  Box,
  Paper,
  TextField,
  Button,
  Typography,
  Alert,
  Link as MLink,
  Stack,
  Grid,
  InputAdornment,
  IconButton,
  Divider,
  CircularProgress,
  alpha,
  keyframes,
} from "@mui/material";
import ApartmentIcon from "@mui/icons-material/Apartment";
import EmailOutlinedIcon from "@mui/icons-material/EmailOutlined";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import EventAvailableIcon from "@mui/icons-material/EventAvailable";
import BuildIcon from "@mui/icons-material/Build";
import CampaignIcon from "@mui/icons-material/Campaign";
import { useNavigate, Link } from "react-router-dom";
import { userApi } from "../api/userApi";
import { useAuth } from "../auth/AuthContext";

const fadeUp = keyframes`
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
`;

const CREAM = "#FAF5EC";
const PAPER = "#FFFDF9";
const MOSS = "#2E3A25";
const MOSS_MID = "#4A5A3C";
const MOSS_LIGHT = "#6B7A5C";
const TERRA = "#C05F3C";
const TERRA_DARK = "#A24A2C";
const GOLD = "#B08442";
const GOLD_DARK = "#8F6A31";

const highlights = [
  { icon: <EventAvailableIcon />, text: "Book amenities without conflicts" },
  { icon: <BuildIcon />, text: "Track maintenance to resolution" },
  { icon: <CampaignIcon />, text: "Stay updated with community notices" },
];

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const res = await userApi.login({ email, password });
      login(res);
      if (res.role === "PLATFORM_ADMIN") navigate("/admin");
      else if (res.role === "MANAGER") navigate("/manager");
      else navigate("/resident");
    } catch (err: any) {
      setError(err?.response?.data?.details || err?.response?.data?.message || "Invalid email or password.");
    } finally {
      setLoading(false);
    }
  };

  const fieldSx = {
    "& .MuiOutlinedInput-root": {
      borderRadius: 2,
      bgcolor: "#FFFFFF",
      "& fieldset": { borderColor: alpha(GOLD, 0.35) },
      "&:hover fieldset": { borderColor: alpha(GOLD, 0.6) },
      "&.Mui-focused fieldset": { borderColor: TERRA, borderWidth: 2 },
    },
    "& label": { color: MOSS_LIGHT },
    "& label.Mui-focused": { color: TERRA },
  };

  return (
    <Box sx={{ minHeight: "100vh", bgcolor: CREAM }}>
      <Grid container sx={{ minHeight: "100vh" }}>
        <Grid
          size={{ xs: 12, md: 6 }}
          sx={{
            display: { xs: "none", md: "flex" },
            flexDirection: "column",
            justifyContent: "space-between",
            position: "relative",
            overflow: "hidden",
            p: 6,
            background: `linear-gradient(155deg, ${MOSS} 0%, ${MOSS_MID} 100%)`,
            color: "#fff",
          }}
        >
          <Box
            sx={{
              position: "absolute",
              top: -110,
              right: -110,
              width: 420,
              height: 420,
              borderRadius: "50%",
              background: `radial-gradient(circle, ${alpha(GOLD, 0.28)} 0%, transparent 70%)`,
            }}
          />
          <Box
            sx={{
              position: "absolute",
              bottom: -90,
              left: -90,
              width: 340,
              height: 340,
              borderRadius: "50%",
              background: `radial-gradient(circle, ${alpha(TERRA, 0.32)} 0%, transparent 70%)`,
            }}
          />
          <Box
            sx={{
              position: "absolute",
              inset: 0,
              backgroundImage: `
                linear-gradient(${alpha("#fff", 0.045)} 1px, transparent 1px),
                linear-gradient(90deg, ${alpha("#fff", 0.045)} 1px, transparent 1px)
              `,
              backgroundSize: "56px 56px",
              maskImage: "radial-gradient(ellipse at center, black 10%, transparent 72%)",
            }}
          />

          <Stack direction="row" spacing={1.5} sx={{ alignItems: "center", position: "relative", zIndex: 1 }}>
            <Box
              sx={{
                width: 42,
                height: 42,
                borderRadius: 2,
                display: "grid",
                placeItems: "center",
                bgcolor: TERRA,
                color: "#fff",
              }}
            >
              <ApartmentIcon sx={{ fontSize: 22 }} />
            </Box>
            <Typography variant="h6" sx={{ fontWeight: 800, letterSpacing: 0.3 }}>
              Dwellora
            </Typography>
          </Stack>

          <Box sx={{ position: "relative", zIndex: 1, animation: `${fadeUp} .6s ease both` }}>
            <Typography
              sx={{
                fontWeight: 900,
                fontSize: "2.6rem",
                lineHeight: 1.2,
                letterSpacing: -1,
                mb: 2,
              }}
            >
              Welcome back to
              <br />
              your community.
            </Typography>
            <Typography sx={{ color: alpha("#fff", 0.78), fontSize: "1.05rem", lineHeight: 1.7, maxWidth: 400, mb: 5 }}>
              Sign in to manage bookings, maintenance requests, notices, and everything that keeps your society running smoothly.
            </Typography>

            <Stack spacing={2.5}>
              {highlights.map((h) => (
                <Stack key={h.text} direction="row" spacing={2} sx={{ alignItems: "center" }}>
                  <Box
                    sx={{
                      width: 40,
                      height: 40,
                      borderRadius: 2,
                      display: "grid",
                      placeItems: "center",
                      bgcolor: alpha(GOLD, 0.25),
                      border: `1px solid ${alpha(GOLD, 0.45)}`,
                      color: "#F3E3C6",
                      flexShrink: 0,
                    }}
                  >
                    {h.icon}
                  </Box>
                  <Typography sx={{ color: alpha("#fff", 0.92), fontWeight: 500 }}>{h.text}</Typography>
                </Stack>
              ))}
            </Stack>
          </Box>

          <Typography variant="caption" sx={{ color: alpha("#fff", 0.5), position: "relative", zIndex: 1 }}>
            © {new Date().getFullYear()} Dwellora · Structured community operations
          </Typography>
        </Grid>

        <Grid
          size={{ xs: 12, md: 6 }}
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            px: { xs: 2, sm: 4 },
            py: { xs: 6, md: 4 },
          }}
        >
          <Box sx={{ width: "100%", maxWidth: 430, animation: `${fadeUp} .5s ease both` }}>
            <Button
              component={Link}
              to="/"
              startIcon={<ArrowBackIcon />}
              sx={{
                mb: 3,
                textTransform: "none",
                fontWeight: 600,
                color: GOLD_DARK,
                "&:hover": { color: TERRA, bgcolor: alpha(GOLD, 0.1) },
              }}
            >
              Back to home
            </Button>

            <Paper
              elevation={0}
              sx={{
                p: { xs: 3, sm: 4.5 },
                borderRadius: 3,
                bgcolor: PAPER,
                border: `1px solid ${alpha(GOLD, 0.28)}`,
                boxShadow: `0 12px 40px ${alpha(MOSS, 0.08)}`,
              }}
            >
              <Stack spacing={1} sx={{ mb: 3.5 }}>
                <Box
                  sx={{
                    width: 48,
                    height: 48,
                    borderRadius: 2,
                    display: { xs: "grid", md: "none" },
                    placeItems: "center",
                    bgcolor: TERRA,
                    color: "#fff",
                    mb: 1,
                  }}
                >
                  <ApartmentIcon />
                </Box>
                <Typography variant="h4" sx={{ fontWeight: 800, letterSpacing: -0.5, color: MOSS }}>
                  Sign in
                </Typography>
                <Typography variant="body2" sx={{ color: MOSS_LIGHT }}>
                  Enter your credentials to access your portal
                </Typography>
              </Stack>

              {error && (
                <Alert
                  severity="error"
                  sx={{ mb: 2.5, borderRadius: 2, fontWeight: 500 }}
                  onClose={() => setError("")}
                >
                  {error}
                </Alert>
              )}

              <form onSubmit={handleSubmit}>
                <Stack spacing={2.5}>
                  <TextField
                    label="Email address"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    fullWidth
                    autoComplete="email"
                    slotProps={{
                      input: {
                        startAdornment: (
                          <InputAdornment position="start">
                            <EmailOutlinedIcon sx={{ color: GOLD_DARK, fontSize: 20 }} />
                          </InputAdornment>
                        ),
                      },
                    }}
                    sx={fieldSx}
                  />

                    <TextField
                      label="Password"
                      type={showPassword ? "text" : "password"}
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      required
                      fullWidth
                      autoComplete="current-password"
                      slotProps={{
                        input: {
                          startAdornment: (
                            <InputAdornment position="start">
                              <LockOutlinedIcon sx={{ color: GOLD_DARK, fontSize: 20 }} />
                            </InputAdornment>
                          ),
                          endAdornment: (
                            <InputAdornment position="end">
                              <IconButton
                                onClick={() => setShowPassword((v) => !v)}
                                edge="end"
                                size="small"
                                tabIndex={-1}
                                                >
                              {showPassword ? (
                                <VisibilityOff sx={{ fontSize: 20, color: GOLD_DARK }} />
                              ) : (
                                <Visibility sx={{ fontSize: 20, color: GOLD_DARK }} />
                              )}
                            </IconButton>
                          </InputAdornment>
                        ),
                      },
                    }}
                    sx={fieldSx}
                  />

                  <Button
                    type="submit"
                    variant="contained"
                    size="large"
                    disabled={loading}
                    fullWidth
                    endIcon={loading ? null : <ArrowForwardIcon />}
                    sx={{
                      py: 1.5,
                      borderRadius: 2,
                      fontWeight: 800,
                      fontSize: "1rem",
                      bgcolor: TERRA,
                      boxShadow: `0 8px 22px ${alpha(TERRA, 0.28)}`,
                      "&:hover": { bgcolor: TERRA_DARK, boxShadow: `0 10px 26px ${alpha(TERRA, 0.34)}` },
                      "&.Mui-disabled": { bgcolor: alpha(TERRA, 0.5), color: "#fff" },
                    }}
                  >
                    {loading ? (
                      <Stack direction="row" spacing={1.5} sx={{ alignItems: "center" }}>
                        <CircularProgress size={18} sx={{ color: "#fff" }} />
                        <span>Signing in...</span>
                      </Stack>
                    ) : (
                      "Sign in"
                    )}
                  </Button>
                </Stack>
              </form>

              <Divider sx={{ my: 3.5, borderColor: alpha(GOLD, 0.25) }}>
                <Typography variant="caption" sx={{ color: MOSS_LIGHT, px: 1 }}>
                  OR
                </Typography>
              </Divider>

              <Box
                sx={{
                  p: 2.5,
                  borderRadius: 2,
                  bgcolor: alpha(GOLD, 0.09),
                  border: `1px solid ${alpha(GOLD, 0.25)}`,
                  textAlign: "center",
                }}
              >
                <Typography variant="body2" sx={{ color: MOSS_LIGHT, mb: 1.5 }}>
                  New apartment community?
                </Typography>
                <MLink
                  component={Link}
                  to="/onboard"
                  sx={{
                    fontWeight: 700,
                    color: TERRA,
                    textDecoration: "none",
                    display: "inline-flex",
                    alignItems: "center",
                    gap: 0.5,
                    "&:hover": { textDecoration: "underline", color: TERRA_DARK },
                  }}
                >
                  Request onboarding
                  <ArrowForwardIcon sx={{ fontSize: 16 }} />
                </MLink>
              </Box>
            </Paper>
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
}