import { useEffect, useMemo, useState } from "react";
import {
  AppBar,
  Toolbar,
  Box,
  Button,
  Container,
  Typography,
  Stack,
  Grid,
  Card,
  CardContent,
  Chip,
  Tab,
  Tabs,
  IconButton,
  useScrollTrigger,
  useTheme,
  alpha,
  keyframes,
} from "@mui/material";
import ApartmentIcon from "@mui/icons-material/Apartment";
import EventAvailableIcon from "@mui/icons-material/EventAvailable";
import BuildIcon from "@mui/icons-material/Build";
import CampaignIcon from "@mui/icons-material/Campaign";
import CelebrationIcon from "@mui/icons-material/Celebration";
import NotificationsActiveIcon from "@mui/icons-material/NotificationsActive";
import GroupsIcon from "@mui/icons-material/Groups";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import { CheckCircleOutlined as CheckCircleOutlineIcon } from "@mui/icons-material";
import AdminPanelSettingsIcon from "@mui/icons-material/AdminPanelSettings";
import ManageAccountsIcon from "@mui/icons-material/ManageAccounts";
import HomeWorkIcon from "@mui/icons-material/HomeWork";
import MenuIcon from "@mui/icons-material/Menu";
import CloseIcon from "@mui/icons-material/Close";
import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
import { Link as RouterLink } from "react-router-dom";
import swimmingPool from "../assets/swimming_pool.jpg";
import gym from "../assets/gym.jpg";
import apartment from "../assets/apartment.jpg";

const fadeUp = keyframes`
  from { opacity: 0; transform: translateY(24px); }
  to { opacity: 1; transform: translateY(0); }
`;

const float = keyframes`
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-8px); }
`;

const blink = keyframes`
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
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
const SAGE = "#6B8F52";

const IMAGES = {
  apartment: apartment,
  pool: swimmingPool,
  gym: gym,
};

const features = [
  {
    icon: <EventAvailableIcon />,
    title: "Smart Amenity Booking",
    description: "Configure per-flat or per-person policies, prevent double-bookings automatically, and let residents book available slots in seconds.",
    color: TERRA,
  },
  {
    icon: <BuildIcon />,
    title: "Tracked Maintenance",
    description: "Residents submit categorized requests. Managers triage and resolve with remarks, providing full transparency from open to closed.",
    color: SAGE,
  },
  {
    icon: <CampaignIcon />,
    title: "Targeted Notices",
    description: "Publish routine updates or urgent alerts. Urgent notices are visually prioritized, and expired posts disappear automatically.",
    color: GOLD,
  },
  {
    icon: <CelebrationIcon />,
    title: "Community Events & RSVP",
    description: "Organize gatherings with capacity limits. Residents RSVP or withdraw seamlessly. No more scattered group chat polls.",
    color: TERRA_DARK,
  },
  {
    icon: <NotificationsActiveIcon />,
    title: "Unified Notifications",
    description: "Booking confirmations, complaint updates, and new notices are aggregated in one clean, in-app inbox for residents.",
    color: MOSS_MID,
  },
  {
    icon: <GroupsIcon />,
    title: "Secure Resident Lifecycle",
    description: "Managers add residents by flat. Accounts are activated via secure email tokens. Zero plaintext passwords exchanged.",
    color: GOLD_DARK,
  },
];

const steps = [
  { n: "01", title: "Request Onboarding", body: "Prospective managers submit society details and contact information. Request status is tracked as PENDING." },
  { n: "02", title: "Admin Approval", body: "Platform admin reviews the request. Approvals provision the apartment and manager account as PENDING_ACTIVATION." },
  { n: "03", title: "Secure Activation", body: "Manager receives a unique, unexpired token via email to set their password. Account becomes ACTIVE." },
  { n: "04", title: "Community Setup", body: "Manager adds residents, configures amenities and booking policies. The society is fully operational." },
];

const roles = [
  {
    key: "manager",
    label: "Society Manager",
    icon: <ManageAccountsIcon />,
    headline: "Operate efficiently",
    points: [
      "Add, update, and deactivate residents by flat number",
      "Configure amenity policies, slot duration, and daily limits",
      "Oversee bookings and track maintenance to resolution",
      "Publish notices and organize community events",
    ],
    cta: { to: "/onboard", text: "Onboard your society" },
  },
  {
    key: "resident",
    label: "Resident",
    icon: <HomeWorkIcon />,
    headline: "Stay connected",
    points: [
      "Book amenities and cancel reservations conflict-free",
      "Raise maintenance complaints and track status live",
      "RSVP to events and read urgent or routine notices",
      "Receive consolidated in-app notifications",
    ],
    cta: { to: "/login", text: "Sign in to portal" },
  },
  {
    key: "admin",
    label: "Platform Admin",
    icon: <AdminPanelSettingsIcon />,
    headline: "Govern the platform",
    points: [
      "Review and triage society onboarding requests",
      "Approve legitimate societies, reject invalid ones",
      "Maintain a trusted, multi-tenant network",
      "Oversee platform-wide health and isolation",
    ],
    cta: { to: "/login", text: "Admin sign in" },
  },
];

const painPoints = [
  { from: "Double-booked amenities", to: "Policy-enforced scheduling" },
  { from: "Lost maintenance threads", to: "Tracked lifecycle workflows" },
  { from: "Missed urgent notices", to: "Prioritized alert delivery" },
  { from: "Plaintext passwords", to: "Secure token activation" },
];

export default function LandingPage() {
  const theme = useTheme();
  const [roleTab, setRoleTab] = useState(0);
  const [mobileOpen, setMobileOpen] = useState(false);
  const [typed, setTyped] = useState("");
  const fullPhrase = "Apartment Operations. Simplified.";

  const scrolled = useScrollTrigger({ disableHysteresis: true, threshold: 24 });

  useEffect(() => {
    let i = 0;
    setTyped("");
    const id = setInterval(() => {
      i += 1;
      setTyped(fullPhrase.slice(0, i));
      if (i >= fullPhrase.length) clearInterval(id);
    }, 55);
    return () => clearInterval(id);
  }, []);

  const activeRole = roles[roleTab];

  const navItems = useMemo(
    () => [
      { label: "Capabilities", href: "#features" },
      { label: "Workflow", href: "#how" },
      { label: "Roles", href: "#roles" },
    ],
    []
  );

  return (
    <Box sx={{ minHeight: "100vh", bgcolor: CREAM, color: MOSS }}>
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          bgcolor: scrolled ? alpha(PAPER, 0.94) : "transparent",
          backdropFilter: scrolled ? "blur(12px)" : "none",
          borderBottom: scrolled ? `1px solid ${theme.palette.divider}` : "none",
          transition: "all .2s ease",
        }}
      >
        <Toolbar sx={{ maxWidth: 1200, width: "100%", mx: "auto", px: { xs: 2, md: 3 } }}>
          <Stack direction="row" spacing={1.2} sx={{ flexGrow: 1, alignItems: "center" }}>
            <Box
              sx={{
                width: 36,
                height: 36,
                borderRadius: 2,
                display: "grid",
                placeItems: "center",
                bgcolor: TERRA,
                color: "#fff",
              }}
            >
              <ApartmentIcon sx={{ fontSize: 20 }} />
            </Box>
            <Typography variant="h6" sx={{ fontWeight: 800, letterSpacing: 0.2, color: MOSS }}>
              Dwellora
            </Typography>
          </Stack>

          <Stack direction="row" spacing={1} sx={{ display: { xs: "none", md: "flex" }, mr: 2 }}>
            {navItems.map((item) => (
              <Button
                key={item.href}
                href={item.href}
                sx={{
                  textTransform: "none",
                  fontWeight: 600,
                  color: GOLD_DARK,
                  "&:hover": { color: TERRA, bgcolor: alpha(GOLD, 0.08) },
                }}
              >
                {item.label}
              </Button>
            ))}
          </Stack>

          <Stack direction="row" spacing={1.5} sx={{ display: { xs: "none", md: "flex" } }}>
            <Button
              component={RouterLink}
              to="/login"
              sx={{ textTransform: "none", fontWeight: 700, color: GOLD_DARK, "&:hover": { bgcolor: alpha(GOLD, 0.08) } }}
            >
              Sign in
            </Button>
            <Button
              component={RouterLink}
              to="/onboard"
              variant="contained"
              sx={{
                textTransform: "none",
                fontWeight: 700,
                borderRadius: 2,
                px: 2.5,
                bgcolor: TERRA,
                boxShadow: `0 6px 16px ${alpha(TERRA, 0.25)}`,
                "&:hover": { bgcolor: TERRA_DARK },
              }}
            >
              Get started
            </Button>
          </Stack>

          <IconButton
            onClick={() => setMobileOpen((v) => !v)}
            sx={{ display: { xs: "inline-flex", md: "none" }, color: MOSS }}
          >
            {mobileOpen ? <CloseIcon /> : <MenuIcon />}
          </IconButton>
        </Toolbar>

        {mobileOpen && (
          <Box
            sx={{
              display: { xs: "block", md: "none" },
              px: 2,
              pb: 2,
              bgcolor: PAPER,
              borderBottom: `1px solid ${theme.palette.divider}`,
            }}
          >
            <Stack spacing={1}>
              {navItems.map((item) => (
                <Button
                  key={item.href}
                  href={item.href}
                  onClick={() => setMobileOpen(false)}
                  sx={{ justifyContent: "flex-start", textTransform: "none", color: GOLD_DARK, fontWeight: 600 }}
                >
                  {item.label}
                </Button>
              ))}
              <Button
                component={RouterLink}
                to="/login"
                sx={{ justifyContent: "flex-start", textTransform: "none", color: GOLD_DARK, fontWeight: 600 }}
              >
                Sign in
              </Button>
              <Button
                component={RouterLink}
                to="/onboard"
                variant="contained"
                sx={{ textTransform: "none", fontWeight: 700, bgcolor: TERRA, "&:hover": { bgcolor: TERRA_DARK } }}
              >
                Get started
              </Button>
            </Stack>
          </Box>
        )}
      </AppBar>

      <Box
        sx={{
          position: "relative",
          pt: { xs: 14, md: 18 },
          pb: { xs: 8, md: 10 },
          overflow: "hidden",
        }}
      >
        <Box
          sx={{
            position: "absolute",
            top: -80,
            right: -80,
            width: 460,
            height: 460,
            borderRadius: "50%",
            background: `radial-gradient(circle, ${alpha(TERRA, 0.12)} 0%, transparent 70%)`,
            pointerEvents: "none",
          }}
        />
        <Box
          sx={{
            position: "absolute",
            bottom: 0,
            left: -70,
            width: 360,
            height: 360,
            borderRadius: "50%",
            background: `radial-gradient(circle, ${alpha(GOLD, 0.14)} 0%, transparent 70%)`,
            pointerEvents: "none",
          }}
        />
        <Box
          sx={{
            position: "absolute",
            top: "35%",
            left: "12%",
            width: 220,
            height: 220,
            borderRadius: "50%",
            background: `radial-gradient(circle, ${alpha(SAGE, 0.1)} 0%, transparent 70%)`,
            pointerEvents: "none",
          }}
        />

        <Container maxWidth="md" sx={{ position: "relative", zIndex: 1, textAlign: "center" }}>
          <Box sx={{ animation: `${fadeUp} .6s ease both` }}>
            <Chip
              label="Built for Communities"
              size="small"
              sx={{
                mb: 3,
                bgcolor: alpha(GOLD, 0.14),
                color: GOLD_DARK,
                border: `1px solid ${alpha(GOLD, 0.3)}`,
                fontWeight: 700,
              }}
            />

            <Typography
              sx={{
                fontWeight: 900,
                fontSize: { xs: "2.4rem", sm: "3rem", md: "3.6rem" },
                lineHeight: 1.15,
                letterSpacing: -0.5,
                mb: 1,
                minHeight: { xs: 100, md: 130 },
                color: MOSS,
              }}
            >
              {typed}
              <Box
                component="span"
                sx={{
                  display: "inline-block",
                  width: 4,
                  height: "0.85em",
                  ml: 0.5,
                  bgcolor: TERRA,
                  verticalAlign: "text-bottom",
                  animation: `${blink} 1s step-end infinite`,
                }}
              />
            </Typography>

            <Typography
              sx={{
                color: MOSS_LIGHT,
                fontSize: { xs: "1.05rem", md: "1.2rem" },
                lineHeight: 1.7,
                maxWidth: 600,
                mx: "auto",
                mb: 5,
              }}
            >
              Dwellora digitizes society management—from amenity bookings and maintenance tracking to resident onboarding and community notices. A structured platform for modern apartment living.
            </Typography>

            <Stack direction={{ xs: "column", sm: "row" }} spacing={2} sx={{ mb: 6, justifyContent: "center" }}>
              <Button
                component={RouterLink}
                to="/onboard"
                variant="contained"
                size="large"
                endIcon={<ArrowForwardIcon />}
                sx={{
                  px: 4,
                  py: 1.5,
                  borderRadius: 2.5,
                  fontWeight: 800,
                  fontSize: "1rem",
                  bgcolor: TERRA,
                  boxShadow: `0 10px 24px ${alpha(TERRA, 0.28)}`,
                  "&:hover": { bgcolor: TERRA_DARK, boxShadow: `0 12px 28px ${alpha(TERRA, 0.34)}` },
                }}
              >
                Request onboarding
              </Button>
              <Button
                component={RouterLink}
                to="/login"
                variant="outlined"
                size="large"
                sx={{
                  px: 4,
                  py: 1.5,
                  borderRadius: 2.5,
                  fontWeight: 700,
                  fontSize: "1rem",
                  borderWidth: 2,
                  borderColor: GOLD,
                  color: GOLD_DARK,
                  "&:hover": { borderWidth: 2, borderColor: GOLD_DARK, bgcolor: alpha(GOLD, 0.08) },
                }}
              >
                Sign in to portal
              </Button>
            </Stack>
          </Box>
        </Container>

        <Stack sx={{ alignItems: "center", opacity: 0.65, mt: 2 }}>
          <Typography variant="caption" sx={{ color: MOSS_LIGHT }}>
            Explore the workflow
          </Typography>
          <KeyboardArrowDownIcon sx={{ animation: `${float} 2s ease-in-out infinite`, color: GOLD_DARK }} />
        </Stack>
      </Box>

      <Box
        id="why"
        sx={{
          py: { xs: 8, md: 12 },
          bgcolor: PAPER,
          borderTop: `1px solid ${theme.palette.divider}`,
          borderBottom: `1px solid ${theme.palette.divider}`,
        }}
      >
        <Container maxWidth="lg">
          <Typography
            variant="h3"
            sx={{ fontWeight: 800, mb: 1, textAlign: "center", fontSize: { xs: "1.6rem", md: "2.2rem" }, color: MOSS }}
          >
            Fragmented channels don't scale.
          </Typography>
          <Typography sx={{ color: MOSS_LIGHT, textAlign: "center", mb: 6, maxWidth: 550, mx: "auto" }}>
            Group chats and paper notices lack policy enforcement, auditability, and structured workflows.
          </Typography>

          <Grid container spacing={3}>
            {painPoints.map((p) => (
              <Grid key={p.from} size={{ xs: 12, sm: 6, md: 3 }}>
                <Card
                  elevation={0}
                  sx={{
                    height: "100%",
                    textAlign: "center",
                    p: 2.5,
                    bgcolor: CREAM,
                    transition: "transform .2s, border-color .2s",
                    "&:hover": {
                      transform: "translateY(-4px)",
                      borderColor: alpha(TERRA, 0.4),
                    },
                  }}
                >
                  <Typography
                    variant="body2"
                    sx={{
                      color: "#B3432B",
                      fontWeight: 700,
                      textDecoration: "line-through",
                      mb: 1.5,
                      display: "block",
                    }}
                  >
                    {p.from}
                  </Typography>
                  <CheckCircleOutlineIcon sx={{ color: SAGE, fontSize: 28, mb: 1 }} />
                  <Typography sx={{ fontWeight: 700, color: MOSS }}>{p.to}</Typography>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Box>

      <Box id="features" sx={{ py: { xs: 10, md: 14 } }}>
        <Container maxWidth="lg">
          <Box sx={{ textAlign: "center", mb: 7 }}>
            <Typography variant="h3" sx={{ fontWeight: 800, mb: 1, fontSize: { xs: "1.6rem", md: "2.2rem" }, color: MOSS }}>
              Core platform capabilities
            </Typography>
            <Typography sx={{ color: MOSS_LIGHT }}>
              Domain-driven microservices mapped to daily community operations.
            </Typography>
          </Box>

          <Grid container spacing={3}> 
            {features.map((f) => (
              <Grid key={f.title} size={{ xs: 12, sm: 6, md: 4 }}>
                <Card
                  elevation={0}
                  sx={{
                    height: "100%",
                    bgcolor: PAPER,
                    transition: "transform .2s, box-shadow .2s, border-color .2s",
                    "&:hover": {
                      transform: "translateY(-6px)",
                      borderColor: alpha(f.color, 0.4),
                      boxShadow: `0 14px 28px ${alpha(f.color, 0.16)}`,
                    },
                  }}
                >
                  <CardContent sx={{ p: 3 }}>
                    <Box
                      sx={{
                        width: 48,
                        height: 48,
                        borderRadius: 2,
                        display: "grid",
                        placeItems: "center",
                        mb: 2,
                        bgcolor: alpha(f.color, 0.12),
                        color: f.color,
                      }}
                    >
                      {f.icon}
                    </Box>
                    <Typography variant="h6" sx={{ fontWeight: 800, mb: 1, color: MOSS }}>
                      {f.title}
                    </Typography>
                    <Typography variant="body2" sx={{ color: MOSS_LIGHT, lineHeight: 1.7 }}>
                      {f.description}
                    </Typography>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Box>

      <Box sx={{ py: { xs: 8, md: 10 }, bgcolor: PAPER, borderTop: `1px solid ${theme.palette.divider}`, borderBottom: `1px solid ${theme.palette.divider}` }}>
        <Container maxWidth="lg">
          <Box sx={{ textAlign: "center", mb: 5 }}>
            <Typography variant="h3" sx={{ fontWeight: 800, fontSize: { xs: "1.6rem", md: "2.2rem" }, color: MOSS }}>
              Where community comes to life
            </Typography>
            <Typography sx={{ color: MOSS_LIGHT, mt: 1 }}>Built for every corner of your society.</Typography>
          </Box>
          <Grid container spacing={3}>
            {[
              { img: IMAGES.apartment, title: "Apartment Living", desc: "Your entire society — blocks, flats, and residents — organized in one place." },
              { img: IMAGES.pool, title: "Swimming Pool & Amenities", desc: "Book slots fairly with per-flat and per-person policies and live availability." },
              { img: IMAGES.gym, title: "Fitness & Recreation", desc: "Gym, courts, and halls with capacity, hours, and queue-free booking." },
            ].map((item) => (
              <Grid key={item.title} size={{ xs: 12, md: 4 }}>
                <Card sx={{ borderRadius: 3, overflow: "hidden", bgcolor: CREAM, height: "100%" }}>
                  <Box component="img" src={item.img} alt={item.title} loading="lazy" sx={{ width: "100%", height: 220, objectFit: "cover", display: "block" }} />
                  <CardContent sx={{ p: 2.5 }}>
                    <Typography variant="h6" sx={{ fontWeight: 800, color: MOSS }}>{item.title}</Typography>
                    <Typography variant="body2" sx={{ color: MOSS_LIGHT, mt: 0.8, lineHeight: 1.6 }}>{item.desc}</Typography>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Box>

      <Box id="how" sx={{ py: { xs: 10, md: 14 }, bgcolor: alpha(GOLD, 0.06) }}>
        <Container maxWidth="lg">
          <Box sx={{ mb: 7, textAlign: "center" }}>
            <Typography variant="h3" sx={{ fontWeight: 800, fontSize: { xs: "1.6rem", md: "2.2rem" }, color: MOSS }}>
              Secure onboarding flow
            </Typography>
            <Typography sx={{ color: MOSS_LIGHT, mt: 1 }}>
              Controlled society setup with token-based activation. No plaintext passwords.
            </Typography>
          </Box>

          <Grid container spacing={3}>
            {steps.map((s, idx) => (
              <Grid key={s.n} size={{ xs: 12, sm: 6, md: 3 }}>
                <Box
                  sx={{
                    position: "relative",
                    height: "100%",
                    p: 3,
                    borderRadius: 3,
                    bgcolor: PAPER,
                    border: `1px solid ${theme.palette.divider}`,
                  }}
                >
                  <Typography
                    sx={{
                      position: "absolute",
                      right: 16,
                      top: 8,
                      fontWeight: 900,
                      fontSize: "2.5rem",
                      lineHeight: 1,
                      color: alpha(TERRA, 0.12),
                      userSelect: "none",
                    }}
                  >
                    {s.n}
                  </Typography>
                  <Typography variant="h6" sx={{ fontWeight: 800, mb: 1, position: "relative", color: MOSS }}>
                    {s.title}
                  </Typography>
                  <Typography
                    variant="body2"
                    sx={{ color: MOSS_LIGHT, lineHeight: 1.7, position: "relative" }}
                  >
                    {s.body}
                  </Typography>
                  {idx < steps.length - 1 && (
                    <Box
                      sx={{
                        display: { xs: "none", md: "block" },
                        position: "absolute",
                        right: -18,
                        top: "50%",
                        width: 36,
                        height: 2,
                        bgcolor: alpha(GOLD, 0.45),
                        zIndex: 2,
                      }}
                    />
                  )}
                </Box>
              </Grid>
            ))}
          </Grid>

          <Box sx={{ textAlign: "center", mt: 6 }}>
            <Button
              component={RouterLink}
              to="/onboard"
              variant="contained"
              size="large"
              sx={{
                px: 5,
                py: 1.5,
                borderRadius: 2.5,
                fontWeight: 800,
                bgcolor: TERRA,
                boxShadow: `0 10px 24px ${alpha(TERRA, 0.26)}`,
                "&:hover": { bgcolor: TERRA_DARK },
              }}
            >
              Initiate society onboarding
            </Button>
          </Box>
        </Container>
      </Box>

      <Box id="roles" sx={{ py: { xs: 10, md: 14 } }}>
        <Container maxWidth="lg">
          <Box sx={{ textAlign: "center", mb: 5 }}>
            <Typography variant="h3" sx={{ fontWeight: 800, fontSize: { xs: "1.6rem", md: "2.2rem" }, color: MOSS }}>
              Role-based access control
            </Typography>
          </Box>

          <Box sx={{ display: "flex", justifyContent: "center", mb: 4 }}>
            <Tabs
              value={roleTab}
              onChange={(_, v) => setRoleTab(v)}
              variant="scrollable"
              scrollButtons="auto"
              sx={{
                p: 0.5,
                borderRadius: 3,
                bgcolor: alpha(GOLD, 0.1),
                border: `1px solid ${alpha(GOLD, 0.22)}`,
                "& .MuiTabs-indicator": {
                  height: "100%",
                  borderRadius: 2.5,
                  bgcolor: alpha(TERRA, 0.14),
                  zIndex: 0,
                },
                "& .MuiTab-root": {
                  textTransform: "none",
                  fontWeight: 700,
                  color: GOLD_DARK,
                  zIndex: 1,
                  minHeight: 48,
                  px: 3,
                },
                "& .Mui-selected": { color: `${TERRA_DARK} !important` },
              }}
            >
              {roles.map((r) => (
                <Tab key={r.key} icon={r.icon} iconPosition="start" label={r.label} />
              ))}
            </Tabs>
          </Box>

          <Card elevation={0} sx={{ borderRadius: 3, overflow: "hidden", bgcolor: PAPER }}>
            <Grid container>
              <Grid
                size={{ xs: 12, md: 5 }}
                sx={{
                  p: { xs: 3, md: 5 },
                  bgcolor: alpha(TERRA, 0.06),
                  borderRight: { md: `1px solid ${theme.palette.divider}` },
                }}
              >
                <Box
                  sx={{
                    width: 52,
                    height: 52,
                    borderRadius: 2.5,
                    display: "grid",
                    placeItems: "center",
                    mb: 2,
                    bgcolor: TERRA,
                    color: "#fff",
                  }}
                >
                  {activeRole.icon}
                </Box>
                <Typography variant="h4" sx={{ fontWeight: 800, mb: 1, color: MOSS }}>
                  {activeRole.label}
                </Typography>
                <Typography sx={{ color: MOSS_LIGHT, mb: 3, fontSize: "1.1rem" }}>
                  {activeRole.headline}
                </Typography>
                <Button
                  component={RouterLink}
                  to={activeRole.cta.to}
                  variant="contained"
                  endIcon={<ArrowForwardIcon />}
                  sx={{ fontWeight: 700, borderRadius: 2, bgcolor: TERRA, "&:hover": { bgcolor: TERRA_DARK } }}
                >
                  {activeRole.cta.text}
                </Button>
              </Grid>
              <Grid size={{ xs: 12, md: 7 }} sx={{ p: { xs: 3, md: 5 } }}>
                <Stack spacing={2}>
                  {activeRole.points.map((point) => (
                    <Stack
                      key={point}
                      direction="row"
                      spacing={2}
                      sx={{
                        alignItems: "flex-start",
                        p: 2,
                        borderRadius: 2,
                        bgcolor: alpha(SAGE, 0.07),
                        border: `1px solid ${alpha(SAGE, 0.18)}`,
                      }}
                    >
                      <CheckCircleOutlineIcon sx={{ color: SAGE, mt: 0.2 }} />
                      <Typography sx={{ lineHeight: 1.6, color: MOSS }}>{point}</Typography>
                    </Stack>
                  ))}
                </Stack>
              </Grid>
            </Grid>
          </Card>
        </Container>
      </Box>

      <Box sx={{ py: { xs: 10, md: 12 } }}>
        <Container maxWidth="sm">
          <Box
            sx={{
              textAlign: "center",
              py: 6,
              px: { xs: 3, md: 5 },
              borderRadius: 4,
              background: `linear-gradient(150deg, ${MOSS} 0%, ${MOSS_MID} 100%)`,
              color: "#fff",
            }}
          >
            <Typography variant="h3" sx={{ fontWeight: 900, mb: 2, fontSize: { xs: "1.6rem", md: "2rem" }, color: "#fff" }}>
              Ready to deploy Dwellora?
            </Typography>
            <Typography sx={{ color: alpha("#fff", 0.82), mb: 4, lineHeight: 1.7 }}>
              Initiate an onboarding request. Upon approval, configure your society and invite residents securely.
            </Typography>
            <Button
              component={RouterLink}
              to="/onboard"
              variant="contained"
              size="large"
              endIcon={<ArrowForwardIcon />}
              sx={{
                px: 5,
                py: 1.5,
                borderRadius: 2.5,
                fontWeight: 800,
                bgcolor: TERRA,
                boxShadow: `0 10px 26px ${alpha("#000", 0.25)}`,
                "&:hover": { bgcolor: TERRA_DARK },
              }}
            >
              Request onboarding
            </Button>
          </Box>
        </Container>
      </Box>

      <Box sx={{ py: 4, borderTop: `1px solid ${theme.palette.divider}` }}>
        <Container maxWidth="lg">
          <Stack direction="row" spacing={1} sx={{ justifyContent: "center", alignItems: "center" }}>
            <ApartmentIcon sx={{ color: TERRA, fontSize: 18 }} />
            <Typography sx={{ fontWeight: 700, color: MOSS }}>Dwellora</Typography>
            <Typography variant="body2" sx={{ color: MOSS_LIGHT }}>
              · Structured community operations
            </Typography>
          </Stack>
        </Container>
      </Box>
    </Box>
  );
}
