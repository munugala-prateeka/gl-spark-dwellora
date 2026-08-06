import { useState } from "react";
import { Tabs, Tab, Box } from "@mui/material";
import DashboardIcon from "@mui/icons-material/Dashboard";
import PoolIcon from "@mui/icons-material/Pool";
import EventNoteIcon from "@mui/icons-material/EventNote";
import ReportProblemIcon from "@mui/icons-material/ReportProblem";
import CampaignIcon from "@mui/icons-material/Campaign";
import CelebrationIcon from "@mui/icons-material/Celebration";
import AppShell from "../components/AppShell";
import BookAmenityTab from "./resident/BookAmenityTab";
import MyBookingsTab from "./resident/MyBookingsTab";
import RaiseComplaintTab from "./resident/RaiseComplaintTab";
import NoticesTab from "./resident/NoticesTab";
import EventsTab from "./resident/EventsTab";

const TABS = [
  { label: "Book Amenity", icon: <PoolIcon />, component: <BookAmenityTab /> },
  { label: "My Bookings", icon: <EventNoteIcon />, component: <MyBookingsTab /> },
  { label: "Complaints", icon: <ReportProblemIcon />, component: <RaiseComplaintTab /> },
  { label: "Notices", icon: <CampaignIcon />, component: <NoticesTab /> },
  { label: "Events", icon: <CelebrationIcon />, component: <EventsTab /> },
];

export default function ResidentDashboard() {
  const [tab, setTab] = useState(0);

  return (
    <AppShell
      title="Resident"
      navItems={[{ label: "Dashboard", path: "/resident", icon: <DashboardIcon /> }]}
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