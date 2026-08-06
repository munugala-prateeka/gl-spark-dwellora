import { useEffect, useState, useCallback, useMemo } from "react";
import { Box, Typography, Card, CardContent, Grid, Chip, Stack, TextField, Button, Snackbar, Alert, Tabs, Tab } from "@mui/material";
import { complaintApi } from "../../api/communityApi";
import { useAuth } from "../../auth/AuthContext";
import type { ComplaintResponse, ComplaintStatus } from "../../api/types";
import { statusColor } from "../../theme/theme";

export default function ComplaintsTab() {
  const { user } = useAuth();
  const apartmentId = user?.apartmentId ?? 0;
  const [complaints, setComplaints] = useState<ComplaintResponse[]>([]);
  const [remarks, setRemarks] = useState<Record<number, string>>({});
  const [toast, setToast] = useState("");
  const [tab, setTab] = useState("ALL");

  const load = useCallback(() => {
    if (!apartmentId) return;
    complaintApi.getByApartment(apartmentId).then(setComplaints).catch(() => setToast("Could not load complaints."));
  }, [apartmentId]);
  useEffect(load, [load]);

  const filtered = useMemo(() => tab === "ALL" ? complaints : complaints.filter(c => c.status === tab), [complaints, tab]);
  const handleUpdate = async (id: number, status: ComplaintStatus) => {
    try { await complaintApi.update(id, { status, resolutionRemark: remarks[id] || undefined }); setToast("Complaint updated — resident notified."); load(); }
    catch { setToast("Could not update complaint."); }
  };

  return (
    <Box>
      <Stack direction="row" sx={{ justifyContent: "space-between", alignItems: "center", mb: 1 }}>
        <Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25" }}>Complaints</Typography>
        <Chip label={`${complaints.filter(c=>c.status==="OPEN").length} open`} sx={{ bgcolor: "#FEF3C7", color: "#92400E", fontWeight: 700 }} />
      </Stack>
      <Tabs value={tab} onChange={(_,v)=>setTab(v)} variant="scrollable" scrollButtons="auto" sx={{ mb: 2.5, "& .MuiTabs-indicator": { bgcolor: "#C05F3C", height: 3, borderRadius: 2 }, "& .MuiTab-root": { fontWeight: 700, textTransform: "none", minHeight: 42, color: "#6B7A5C" }, "& .Mui-selected": { color: "#2E3A25 !important" } }}>
        <Tab label={`All (${complaints.length})`} value="ALL" /><Tab label={`Open (${complaints.filter(c=>c.status==="OPEN").length})`} value="OPEN" /><Tab label="In Progress" value="IN_PROGRESS" /><Tab label="Resolved" value="RESOLVED" />
      </Tabs>

      <Grid container spacing={2.5}>
        {filtered.map((c) => (
          <Grid size={{ xs: 12, md: 6 }} key={c.complaintId}>
            <Card sx={{ borderRadius: 3, border: c.status === "OPEN" ? "2px solid #F59E0B" : c.status === "IN_PROGRESS" ? "1.5px solid #B08442" : "1px solid #E6DCC9", bgcolor: "#FFFDF9", boxShadow: "0 4px 14px rgba(46,58,37,0.04)", overflow: "hidden" }}>
              <Box sx={{ height: 4, bgcolor: c.status === "OPEN" ? "#F59E0B" : c.status === "IN_PROGRESS" ? "#B08442" : "#6B8F52" }} />
              <CardContent sx={{ p: 2.5 }}>
                <Stack direction="row" sx={{ justifyContent: "space-between", alignItems: "flex-start", mb: 0.5 }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 800, color: "#2E3A25" }}>{c.category}</Typography>
                  <Chip size="small" label={c.status} color={statusColor[c.status] as any} sx={{ fontWeight: 700 }} />
                </Stack>
                <Chip label={`Flat ${c.flatNumber}`} size="small" sx={{ bgcolor: "#E9EBDD", color: "#2E3A25", fontWeight: 700 }} />
                <Typography variant="body2" sx={{ mt: 1.5, color: "#2E3A25", lineHeight: 1.6, bgcolor: "#FAF5EC", p: 1.5, borderRadius: 2, border: "1px solid #F0EDD8" }}>{c.description}</Typography>
                {c.resolutionRemark && (
                  <Box sx={{ mt: 1.5, p: 1.5, borderRadius: 2, bgcolor: "#F0F7ED", border: "1px solid #C5D8B8" }}>
                    <Typography variant="caption" sx={{ color: "#6B8F52", fontWeight: 800, letterSpacing: 0.5 }}>RESOLVED · </Typography>
                    <Typography variant="body2" sx={{ color: "#2E3A25" }}>{c.resolutionRemark}</Typography>
                  </Box>
                )}
                {c.status !== "RESOLVED" && (
                  <Stack spacing={1.5} sx={{ mt: 2 }}>
                    <TextField size="small" label="Resolution remark" placeholder="What was done?" value={remarks[c.complaintId] ?? ""} onChange={(e) => setRemarks((r) => ({ ...r, [c.complaintId]: e.target.value }))} fullWidth sx={{ "& .MuiOutlinedInput-root.Mui-focused fieldset": { borderColor: "#C05F3C" }, "& label.Mui-focused": { color: "#C05F3C" } }} />
                    <Stack direction="row" spacing={1}>
                      {c.status === "OPEN" && <Button size="small" variant="outlined" onClick={() => handleUpdate(c.complaintId, "IN_PROGRESS")} sx={{ borderColor: "#B08442", color: "#8F6A31", fontWeight: 700, borderRadius: 2, flex: 1 }}>In progress</Button>}
                      <Button size="small" variant="contained" onClick={() => handleUpdate(c.complaintId, "RESOLVED")} sx={{ bgcolor: "#6B8F52", "&:hover": { bgcolor: "#5A7A42" }, fontWeight: 700, borderRadius: 2, flex: 1 }}>Resolve</Button>
                    </Stack>
                  </Stack>
                )}
              </CardContent>
            </Card>
          </Grid>
        ))}
        {filtered.length === 0 && <Grid size={12}><Box sx={{ p: 5, textAlign: "center", bgcolor: "#FFFDF9", borderRadius: 3, border: "1px dashed #E6DCC9" }}><Typography sx={{ color: "#6B7A5C" }}>No {tab.toLowerCase()} complaints.</Typography></Box></Grid>}
      </Grid>
      <Snackbar open={!!toast} autoHideDuration={4000} onClose={() => setToast("")}><Alert severity="info" onClose={() => setToast("")} sx={{ bgcolor: "#2E3A25", color: "#fff", fontWeight: 600 }}>{toast}</Alert></Snackbar>
    </Box>
  );
}