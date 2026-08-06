import { createTheme } from "@mui/material/styles";

const palette = {
  cream: "#FAF5EC",
  creamSoft: "#F4EDE1",
  paper: "#FFFDF9",
  moss: "#2E3A25",
  mossMid: "#4A5A3C",
  mossLight: "#6B7A5C",
  terracotta: "#C05F3C",
  terracottaDark: "#A24A2C",
  terracottaLight: "#D98A6A",
  gold: "#B08442",
  goldDark: "#8F6A31",
  goldLight: "#D4B078",
  sage: "#6B8F52",
  brick: "#B3432B",
  border: "#E6DCC9",
};

export const theme = createTheme({
  palette: {
    mode: "light",
    primary: {
      main: palette.terracotta,
      dark: palette.terracottaDark,
      light: palette.terracottaLight,
      contrastText: "#FFFFFF",
    },
    secondary: {
      main: palette.gold,
      dark: palette.goldDark,
      light: palette.goldLight,
      contrastText: "#FFFFFF",
    },
    success: { main: palette.sage },
    error: { main: palette.brick },
    warning: { main: palette.gold },
    info: { main: palette.mossMid },
    background: {
      default: palette.cream,
      paper: palette.paper,
    },
    text: {
      primary: palette.moss,
      secondary: palette.mossLight,
    },
    divider: palette.border,
  },
  shape: { borderRadius: 10 },
  typography: {
    fontFamily: '"Plus Jakarta Sans", "Inter", Roboto, sans-serif',
    h3: { fontWeight: 800, letterSpacing: "-0.03em" },
    h4: { fontWeight: 700, letterSpacing: "-0.02em" },
    h5: { fontWeight: 700, letterSpacing: "-0.01em" },
    h6: { fontWeight: 700 },
    button: { fontWeight: 700, textTransform: "none" as const },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: { borderRadius: 9, paddingLeft: 18, paddingRight: 18 },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: { backgroundImage: "none" },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          border: `1px solid ${palette.border}`,
          boxShadow: "0 4px 10px -2px rgba(46, 58, 37, 0.05)",
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: { fontWeight: 600 },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: palette.paper,
          color: palette.moss,
          boxShadow: "none",
        },
      },
    },
    MuiOutlinedInput: {
      styleOverrides: {
        root: { backgroundColor: "#FFFFFF" },
      },
    },
  },
});

export const statusColor: Record<string, "default" | "warning" | "success" | "error" | "info"> = {
  PENDING: "warning",
  PENDING_ACTIVATION: "warning",
  APPROVED: "success",
  ACTIVE: "success",
  BOOKED: "success",
  OPEN: "warning",
  IN_PROGRESS: "info",
  RESOLVED: "success",
  REJECTED: "error",
  CANCELLED: "error",
  INACTIVE: "default",
  SUSPENDED: "error",
};