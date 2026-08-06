import { useState } from "react";
import { Tabs, Tab, Box } from "@mui/material";
import DashboardIcon from "@mui/icons-material/Dashboard";
import HowToRegIcon from "@mui/icons-material/HowToReg";
import GroupIcon from "@mui/icons-material/Group";
import AppShell from "../components/AppShell";
import OverviewTab from "./admin/OverviewTab";
import OnboardingTab from "./admin/OnboardingTab";
import UsersTab from "./admin/UsersTab";

const TABS = [
  { label: "Overview", icon: <DashboardIcon />, component: <OverviewTab /> },
  { label: "Onboarding Log", icon: <HowToRegIcon />, component: <OnboardingTab /> },
  { label: "Users", icon: <GroupIcon />, component: <UsersTab /> },
];

export default function AdminDashboard() {
  const [tab, setTab] = useState(0);

  return (
    <AppShell
      title="Platform Admin"
      navItems={[{ label: "Dashboard", path: "/admin", icon: <DashboardIcon /> }]}
    >
      <Box sx={{ mb: 3, mt: 1 }}>
        <Tabs
          value={tab}
          onChange={(_, v) => setTab(v)}
          variant="scrollable"
          scrollButtons="auto"
          sx={{
            "& .MuiTabs-indicator": { bgcolor: "#C05F3C", height: 3, borderRadius: 2 },
            "& .MuiTab-root": { fontWeight: 700, textTransform: "none", color: "#6B7A5C", minHeight: 52 },
            "& .Mui-selected": { color: "#2E3A25 !important" },
          }}
        >
          {TABS.map((t) => (
            <Tab key={t.label} label={t.label} icon={t.icon} iconPosition="start" />
          ))}
        </Tabs>
      </Box>
      <Box sx={{ bgcolor: "#FFFDF9", borderRadius: 3, border: "1px solid #E6DCC9", p: { xs: 2, md: 3 }, boxShadow: "0 4px 16px rgba(46,58,37,0.04)" }}>
        {TABS[tab].component}
      </Box>
    </AppShell>
  );
}