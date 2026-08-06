import { useEffect, useState, useCallback, useMemo } from "react";
import {
  Box, Typography, Card, CardContent, Grid, Chip, Button, Stack,
  Divider, Tabs, Tab, Snackbar, Alert, TextField, InputAdornment
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import ApartmentIcon from "@mui/icons-material/Apartment";
import { onboardingApi } from "../../api/onboardingApi";
import type { OnboardingResponse } from "../../api/types";
import { statusColor } from "../../theme/theme";

export default function OnboardingTab() {
  const [requests, setRequests] = useState<OnboardingResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState("");
  const [busyId, setBusyId] = useState<number | null>(null);
  const [tab, setTab] = useState("ALL");
  const [q, setQ] = useState("");

  const load = useCallback(() => {
    setLoading(true);
    onboardingApi.getAll()
      .then(setRequests)
      .catch(() => setToast("Could not load onboarding requests."))
      .finally(() => setLoading(false));
  }, []);

  useEffect(load, [load]);

  const filtered = useMemo(() => {
    let list = tab === "ALL" ? requests : requests.filter((r) => r.status === tab);
    if (q.trim()) {
      const s = q.toLowerCase();
      list = list.filter(r => `${r.apartmentName} ${r.city} ${r.managerName} ${r.managerEmail}`.toLowerCase().includes(s));
    }
    return list;
  }, [requests, tab, q]);

  const handleApprove = async (id: number) => {
    setBusyId(id);
    try {
      await onboardingApi.approve(id);
      setToast("Request approved — provisioning is underway.");
      load();
    } catch { setToast("Could not approve this request."); }
    finally { setBusyId(null); }
  };

  const handleReject = async (id: number) => {
    setBusyId(id);
    try {
      await onboardingApi.reject(id);
      setToast("Request rejected.");
      load();
    } catch { setToast("Could not reject this request."); }
    finally { setBusyId(null); }
  };

  const pendingCount = requests.filter((r) => r.status === "PENDING").length;

  return (
    <Box>
      <Stack direction={{ xs: "column", sm: "row" }} sx={{ justifyContent: "space-between", gap: 2, mb: 1 }}>
        <Box>
          <Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25" }}>Onboarding request log</Typography>
          <Typography variant="body2" sx={{ color: "#6B7A5C" }}>Every request submitted to Dwellora, with its outcome.</Typography>
        </Box>
        <TextField
  size="small"
  placeholder="Search society, city, manager..."
  value={q}
  onChange={(e) => setQ(e.target.value)}
  slotProps={{
    input: {
      startAdornment: (
        <InputAdornment position="start">
          <SearchIcon sx={{ color: "#B08442", fontSize: 18 }} />
        </InputAdornment>
      ),
    },
  }}
  sx={{
    width: 280,
    "& .MuiOutlinedInput-root": {
      bgcolor: "#FFFDF9",
      borderRadius: 2.5,
      "& fieldset": { borderColor: "#E6DCC9" },
    },
  }}
/>
      </Stack>

      <Tabs
        value={tab}
        onChange={(_, v) => setTab(v)}
        variant="scrollable"
        scrollButtons="auto"
        sx={{ mb: 2.5, "& .MuiTabs-indicator": { bgcolor: "#C05F3C", height: 3, borderRadius: 2 }, "& .MuiTab-root": { fontWeight: 700, textTransform: "none", minHeight: 42, color: "#6B7A5C" }, "& .Mui-selected": { color: "#2E3A25 !important" } }}
      >
        <Tab label={`All (${requests.length})`} value="ALL" />
        <Tab label={`Pending (${pendingCount})`} value="PENDING" />
        <Tab label="Approved" value="APPROVED" />
        <Tab label="Rejected" value="REJECTED" />
      </Tabs>

      {!loading && filtered.length === 0 && (
        <Box sx={{ p: 5, textAlign: "center", bgcolor: "#FAF5EC", borderRadius: 3, border: "1px dashed #E6DCC9" }}>
          <Box sx={{ width: 48, height: 48, borderRadius: 3, bgcolor: "#FFFDF9", border: "1px solid #E6DCC9", display: "grid", placeItems: "center", mx: "auto", mb: 1.5, color: "#B08442" }}><ApartmentIcon/></Box>
          <Typography sx={{ fontWeight: 700, color: "#2E3A25" }}>No requests in this category</Typography>
          <Typography variant="body2" sx={{ color: "#6B7A5C" }}>Try another filter or search term</Typography>
        </Box>
      )}

      <Grid container spacing={2.5}>
        {filtered.map((r) => (
          <Grid size={{ xs: 12, md: 6 }} key={r.requestId}>
            <Card sx={{ borderRadius: 3, border: r.status === "PENDING" ? "2px solid #F59E0B" : "1px solid #E6DCC9", bgcolor: "#FFFDF9", overflow: "hidden", boxShadow: "0 4px 14px rgba(46,58,37,0.04)", transition: ".2s", "&:hover": { transform: "translateY(-3px)", boxShadow: "0 10px 24px rgba(46,58,37,0.08)" } }}>
              <Box sx={{ height: 4, bgcolor: r.status === "PENDING" ? "#F59E0B" : r.status === "APPROVED" ? "#6B8F52" : r.status === "REJECTED" ? "#C05F3C" : "#E6DCC9" }}/>
              <CardContent sx={{ p: 2.5 }}>
                <Stack direction="row" sx={{ justifyContent: "space-between", alignItems: "flex-start", mb: 0.5 }}>
                  <Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25", lineHeight: 1.2 }}>{r.apartmentName}</Typography>
                  <Chip label={r.status} color={statusColor[r.status] as any} size="small" sx={{ fontWeight: 700 }} />
                </Stack>
                <Typography variant="body2" sx={{ color: "#6B7A5C" }}>{r.address}, {r.city}, {r.state} — {r.pincode}</Typography>
                <Chip label={`${r.totalBlocks} blocks · ${r.totalUnits} units`} size="small" sx={{ mt: 1, bgcolor: "#E9EBDD", color: "#2E3A25", fontWeight: 700, height: 22 }} />
                <Divider sx={{ my: 1.8, borderColor: "#E6DCC9" }} />
                <Typography variant="caption" sx={{ color: "#B08442", fontWeight: 800, letterSpacing: 0.6 }}>PROSPECTIVE MANAGER</Typography>
                <Typography variant="body2" sx={{ fontWeight: 700, color: "#2E3A25" }}>{r.managerName}</Typography>
                <Typography variant="body2" sx={{ color: "#6B7A5C" }}>{r.managerEmail} · {r.managerPhone}</Typography>
                {r.status === "PENDING" && (
                  <Stack direction="row" spacing={1.5} sx={{ mt: 2.5 }}>
                    <Button variant="contained" disabled={busyId === r.requestId} onClick={() => handleApprove(r.requestId)} sx={{ bgcolor: "#6B8F52", "&:hover": { bgcolor: "#5A7A42" }, fontWeight: 700, borderRadius: 2, flex: 1 }}>Approve</Button>
                    <Button variant="outlined" disabled={busyId === r.requestId} onClick={() => handleReject(r.requestId)} sx={{ borderColor: "#C05F3C", color: "#C05F3C", fontWeight: 700, borderRadius: 2, flex: 1, "&:hover": { bgcolor: "#FFF1ED", borderColor: "#A24A2C" } }}>Reject</Button>
                  </Stack>
                )}
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Snackbar open={!!toast} autoHideDuration={4000} onClose={() => setToast("")}>
        <Alert severity="info" onClose={() => setToast("")} sx={{ bgcolor: "#2E3A25", color: "#fff", fontWeight: 600 }}>{toast}</Alert>
      </Snackbar>
    </Box>
  );
}