import { useState } from "react";
import { Tabs, Tab, Box } from "@mui/material";
import DashboardIcon from "@mui/icons-material/Dashboard";
import PoolIcon from "@mui/icons-material/Pool";
import PeopleIcon from "@mui/icons-material/People";
import EventAvailableIcon from "@mui/icons-material/EventAvailable";
import ReportProblemIcon from "@mui/icons-material/ReportProblem";
import CampaignIcon from "@mui/icons-material/Campaign";
import CelebrationIcon from "@mui/icons-material/Celebration";
import AppShell from "../components/AppShell";
import AmenitiesTab from "./manager/AmenitiesTab";
import ResidentsTab from "./manager/ResidentsTab";
import BookingsTab from "./manager/BookingsTab";
import ComplaintsTab from "./manager/ComplaintsTab";
import NoticesTab from "./manager/NoticesTab";
import EventsTab from "./manager/EventsTab";

const TABS = [
  { label: "Amenities", icon: <PoolIcon />, component: <AmenitiesTab /> },
  { label: "Residents", icon: <PeopleIcon />, component: <ResidentsTab /> },
  { label: "Bookings", icon: <EventAvailableIcon />, component: <BookingsTab /> },
  { label: "Complaints", icon: <ReportProblemIcon />, component: <ComplaintsTab /> },
  { label: "Notices", icon: <CampaignIcon />, component: <NoticesTab /> },
  { label: "Events", icon: <CelebrationIcon />, component: <EventsTab /> },
];

export default function ManagerDashboard() {
  const [tab, setTab] = useState(0);

  return (
    <AppShell
      title="Manager"
      navItems={[{ label: "Dashboard", path: "/manager", icon: <DashboardIcon /> }]}
    >
      <Box sx={{ mb: 3, mt: 1 }}>
        <Tabs
          value={tab}
          onChange={(_, v) => setTab(v)}
          variant="scrollable"
          scrollButtons="auto"
          sx={{
            "& .MuiTabs-indicator": { bgcolor: "#C05F3C", height: 3, borderRadius: 2 },
            "& .MuiTab-root": { fontWeight: 700, textTransform: "none", color: "#6B7A5C" },
            "& .Mui-selected": { color: "#2E3A25" },
          }}
        >
          {TABS.map((t) => (
            <Tab key={t.label} label={t.label} icon={t.icon} iconPosition="start" sx={{ minHeight: 52 }} />
          ))}
        </Tabs>
      </Box>
      <Box sx={{ bgcolor: "#FFFDF9", borderRadius: 3, border: "1px solid #E6DCC9", p: { xs: 2, md: 3 }, boxShadow: "0 4px 16px rgba(46,58,37,0.04)" }}>
        {TABS[tab].component}
      </Box>
    </AppShell>
  );
}