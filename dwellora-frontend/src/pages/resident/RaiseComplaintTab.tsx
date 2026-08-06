import { useEffect, useState, useCallback, useMemo } from "react";
import { Box, Typography, Card, CardContent, Grid, Chip, Stack, TextField, Button, Dialog, DialogTitle, DialogContent, DialogActions, Snackbar, Alert, Tabs, Tab } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import { complaintApi } from "../../api/communityApi";
import { useAuth } from "../../auth/AuthContext";
import type { ComplaintRequest, ComplaintResponse } from "../../api/types";
import { statusColor } from "../../theme/theme";

const emptyForm = (apartmentId: number): ComplaintRequest => ({ apartmentId, category: "", description: "" });

export default function RaiseComplaintTab() {
  const { user } = useAuth();
  const apartmentId = user?.apartmentId ?? 0;
  const [complaints, setComplaints] = useState<ComplaintResponse[]>([]);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState<ComplaintRequest>(emptyForm(apartmentId));
  const [toast, setToast] = useState("");
  const [tab, setTab] = useState("ALL");

  const load = useCallback(() => {
    if (!user) return;
    complaintApi.getByUser(user.userId).then(setComplaints).catch(() => setToast("Could not load your complaints."));
  }, [user]);
  useEffect(load, [load]);

  const filtered = useMemo(() => tab === "ALL" ? complaints : complaints.filter(c => c.status === tab), [complaints, tab]);

  const handleSubmit = async () => {
    if (!user) return;
    try {
      await complaintApi.raise(user.userId, form);
      setToast("Complaint raised — your manager has been notified.");
      setOpen(false);
      setForm(emptyForm(apartmentId));
      load();
    } catch (err: any) {
      setToast(err?.response?.data?.details || "Could not submit complaint.");
    }
  };

  return (
    <Box>
      <Stack direction={{ xs: "column", sm: "row" }} sx={{ justifyContent: "space-between", gap: 2, mb: 1 }}>
        <Box>
          <Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25" }}>My Complaints</Typography>
          <Typography variant="body2" sx={{ color: "#6B7A5C" }}>{complaints.length} raised · {complaints.filter(c=>c.status!=="RESOLVED").length} pending</Typography>
        </Box>
        <Button startIcon={<AddIcon />} variant="contained" onClick={() => setOpen(true)} sx={{ bgcolor: "#C05F3C", "&:hover": { bgcolor: "#A24A2C" }, fontWeight: 700, borderRadius: 2.5, boxShadow: "0 6px 16px rgba(192,95,60,0.22)", whiteSpace: "nowrap" }}>Raise a complaint</Button>
      </Stack>

      <Tabs value={tab} onChange={(_,v)=>setTab(v)} variant="scrollable" scrollButtons="auto" sx={{ mb: 2.5, "& .MuiTabs-indicator": { bgcolor: "#C05F3C", height: 3, borderRadius: 2 }, "& .MuiTab-root": { fontWeight: 700, textTransform: "none", minHeight: 42, color: "#6B7A5C" }, "& .Mui-selected": { color: "#2E3A25 !important" } }}>
        <Tab label={`All (${complaints.length})`} value="ALL" /><Tab label="Open" value="OPEN" /><Tab label="In Progress" value="IN_PROGRESS" /><Tab label="Resolved" value="RESOLVED"/>
      </Tabs>

      <Grid container spacing={2.5}>
        {filtered.map((c) => (
          <Grid size={{ xs: 12, md: 6 }} key={c.complaintId}>
            <Card sx={{ borderRadius: 3, border: c.status === "OPEN" ? "1.5px solid #F59E0B" : c.status === "IN_PROGRESS" ? "1.5px solid #B08442" : "1px solid #E6DCC9", bgcolor: "#FFFDF9", overflow: "hidden", boxShadow: "0 4px 14px rgba(46,58,37,0.04)" }}>
              <Box sx={{ height: 4, bgcolor: c.status === "OPEN" ? "#F59E0B" : c.status === "IN_PROGRESS" ? "#B08442" : "#6B8F52" }}/>
              <CardContent sx={{ p: 2.5 }}>
                <Stack direction="row" sx={{ justifyContent: "space-between", alignItems: "flex-start" }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 800, color: "#2E3A25" }}>{c.category}</Typography>
                  <Chip size="small" label={c.status} color={statusColor[c.status] as any} sx={{ fontWeight: 700 }}/>
                </Stack>
                <Typography variant="body2" sx={{ mt: 1.2, color: "#2E3A25", lineHeight: 1.6, bgcolor: "#FAF5EC", p: 1.5, borderRadius: 2, border: "1px solid #F0EDD8" }}>{c.description}</Typography>
                {c.resolutionRemark && <Box sx={{ mt: 1.5, p: 1.5, borderRadius: 2, bgcolor: "#F0F7ED", border: "1px solid #C5D8B8" }}><Typography variant="caption" sx={{ color: "#6B8F52", fontWeight: 800, letterSpacing: 0.5 }}>MANAGER'S REMARK</Typography><Typography variant="body2" sx={{ color: "#2E3A25", mt: 0.3 }}>{c.resolutionRemark}</Typography></Box>}
                <Typography variant="caption" sx={{ color: "#6B7A5C", fontWeight: 600, display: "block", mt: 1.2 }}>Raised {new Date(c.raisedAt).toLocaleDateString()}</Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
        {filtered.length === 0 && <Grid size={12}><Box sx={{ p: 5, textAlign: "center", bgcolor: "#FFFDF9", borderRadius: 3, border: "1px dashed #E6DCC9" }}><Typography sx={{ color: "#6B7A5C" }}>{complaints.length === 0 ? "You haven't raised any complaints." : `No ${tab.toLowerCase().replace("_"," ")} complaints.`}</Typography></Box></Grid>}
      </Grid>

      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth slotProps={{ paper: { sx: { borderRadius: 3 } } }}>
        <DialogTitle sx={{ fontWeight: 800, color: "#2E3A25" }}>Raise a complaint</DialogTitle>
        <DialogContent><Stack spacing={2} sx={{ mt: 1 }}>
          <TextField label="Category" placeholder="e.g. Plumbing, Electrical, Cleanliness" fullWidth value={form.category} onChange={(e) => setForm((f) => ({ ...f, category: e.target.value }))} sx={{ "& .MuiOutlinedInput-root.Mui-focused fieldset": { borderColor: "#C05F3C" }, "& label.Mui-focused": { color: "#C05F3C" } }}/>
          <TextField label="Description" fullWidth multiline rows={4} value={form.description} onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))} sx={{ "& .MuiOutlinedInput-root.Mui-focused fieldset": { borderColor: "#C05F3C" }, "& label.Mui-focused": { color: "#C05F3C" } }}/>
        </Stack></DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}><Button onClick={() => setOpen(false)} sx={{ color: "#6B7A5C", fontWeight: 600 }}>Cancel</Button><Button variant="contained" onClick={handleSubmit} sx={{ bgcolor: "#C05F3C", "&:hover": { bgcolor: "#A24A2C" }, fontWeight: 700 }}>Submit</Button></DialogActions>
      </Dialog>
      <Snackbar open={!!toast} autoHideDuration={4000} onClose={() => setToast("")}><Alert severity="info" onClose={() => setToast("")} sx={{ bgcolor: "#2E3A25", color: "#fff", fontWeight: 600 }}>{toast}</Alert></Snackbar>
    </Box>
  );
}