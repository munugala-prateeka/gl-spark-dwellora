import { useEffect, useState } from "react";
import { Box, Typography, Card, CardContent, Grid, Chip, Snackbar, Alert } from "@mui/material";
import ApartmentIcon from "@mui/icons-material/Apartment";
import GroupIcon from "@mui/icons-material/Group";
import EngineeringIcon from "@mui/icons-material/Engineering";
import PendingActionsIcon from "@mui/icons-material/PendingActions";
import { onboardingApi } from "../../api/onboardingApi";
import { apartmentApi } from "../../api/apartmentApi";
import { userApi } from "../../api/userApi";
import type { ApartmentResponse, UserResponse, OnboardingResponse } from "../../api/types";
import { statusColor } from "../../theme/theme";

function StatCard({ icon, label, value, color }: { icon: React.ReactNode; label: string; value: number; color: string; }) {
  return (
    <Card sx={{ borderRadius: 3, border: "1px solid #E6DCC9", bgcolor: "#FFFDF9", boxShadow: "0 4px 14px rgba(46,58,37,0.04)" }}>
      <CardContent sx={{ display: "flex", alignItems: "center", gap: 2, py: 2.5 }}>
        <Box sx={{ width: 48, height: 48, borderRadius: 2.5, bgcolor: `${color}18`, color, display: "grid", placeItems: "center", border: `1px solid ${color}30` }}>{icon}</Box>
        <Box>
          <Typography variant="h5" sx={{ fontWeight: 900, color: "#2E3A25", lineHeight: 1 }}>{value}</Typography>
          <Typography variant="body2" sx={{ color: "#6B7A5C", fontWeight: 600 }}>{label}</Typography>
        </Box>
      </CardContent>
    </Card>
  );
}

export default function OverviewTab() {
  const [apartments, setApartments] = useState<ApartmentResponse[]>([]);
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [pending, setPending] = useState<OnboardingResponse[]>([]);
  const [toast, setToast] = useState("");

  useEffect(() => {
    apartmentApi.getAll().then(setApartments).catch(() => setToast("Could not load apartments."));
    userApi.getAll().then(setUsers).catch(() => setToast("Could not load users."));
    onboardingApi.getPending().then(setPending).catch(() => setToast("Could not load pending requests."));
  }, []);

  const managerCount = users.filter((u) => u.role === "MANAGER").length;
  const residentCount = users.filter((u) => u.role === "RESIDENT").length;

  return (
    <Box>
      <Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25" }}>Platform overview</Typography>
      <Typography variant="body2" sx={{ color: "#6B7A5C", mb: 3 }}>A snapshot of everything running on Dwellora right now.</Typography>

      <Grid container spacing={2.5} sx={{ mb: 4 }}>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}><StatCard icon={<ApartmentIcon />} label="Provisioned apartments" value={apartments.length} color="#C05F3C" /></Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}><StatCard icon={<EngineeringIcon />} label="Managers" value={managerCount} color="#B08442" /></Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}><StatCard icon={<GroupIcon />} label="Residents" value={residentCount} color="#6B8F52" /></Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}><StatCard icon={<PendingActionsIcon />} label="Pending requests" value={pending.length} color="#D97706" /></Grid>
      </Grid>

      <Typography variant="subtitle1" sx={{ fontWeight: 800, color: "#2E3A25", mb: 1.5 }}>Apartments</Typography>
      <Box sx={{ borderRadius: 3, border: "1px solid #E6DCC9", overflow: "hidden", bgcolor: "#FFFDF9", boxShadow: "0 4px 14px rgba(46,58,37,0.04)" }}>
        <Box sx={{ overflowX: "auto" }}>
          <Box component="table" sx={{ width: "100%", borderCollapse: "collapse", minWidth: 720 }}>
            <Box component="thead" sx={{ bgcolor: "#FAF5EC", borderBottom: "1px solid #E6DCC9" }}>
              <Box component="tr">
                {["Name","Address","City / State","Blocks","Units","Status"].map(h=>(
                  <Box component="th" key={h} sx={{ px: 2.5, py: 1.4, textAlign: "left", fontWeight: 800, color: "#2E3A25", fontSize: "0.72rem", letterSpacing: "0.07em", textTransform: "uppercase", whiteSpace: "nowrap" }}>{h}</Box>
                ))}
              </Box>
            </Box>
            <Box component="tbody">
              {apartments.map((a) => (
                <Box component="tr" key={a.apartmentId} sx={{ borderBottom: "1px solid #F0EDD8", "&:last-child":{borderBottom:0}, "&:hover":{bgcolor:"#FAF5EC"} }}>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, fontWeight: 700, color: "#2E3A25" }}>{a.apartmentName}</Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, color: "#6B7A5C" }}>{a.address}</Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, color: "#6B7A5C" }}>{a.city}, {a.state}</Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, color: "#2E3A25", fontWeight: 600 }}>{a.totalBlocks}</Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, color: "#2E3A25", fontWeight: 600 }}>{a.totalUnits}</Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8 }}><Chip size="small" label={a.status} color={statusColor[a.status] as any} sx={{ fontWeight: 700 }}/></Box>
                </Box>
              ))}
              {apartments.length === 0 && <Box component="tr"><Box component="td" colSpan={6} sx={{ px: 2.5, py: 5, textAlign: "center", color: "#6B7A5C" }}>No apartments provisioned yet.</Box></Box>}
            </Box>
          </Box>
        </Box>
      </Box>

      <Snackbar open={!!toast} autoHideDuration={4000} onClose={() => setToast("")}>
        <Alert severity="info" onClose={() => setToast("")} sx={{ bgcolor: "#2E3A25", color: "#fff", fontWeight: 600 }}>{toast}</Alert>
      </Snackbar>
    </Box>
  );
}