import { useEffect, useState, useCallback } from "react";
import { Box, Typography, Card, CardContent, Stack, TextField, Button, Chip, Grid, Dialog, DialogTitle, DialogContent, DialogActions, Snackbar, Alert, LinearProgress } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import EventIcon from "@mui/icons-material/Event";
import PeopleIcon from "@mui/icons-material/People";
import { eventApi } from "../../api/communityApi";
import { useAuth } from "../../auth/AuthContext";
import type { EventRequest, EventResponse } from "../../api/types";
const emptyForm = (apartmentId: number): EventRequest => ({ apartmentId, title: "", description: "", eventDate: "", capacity: null });

export default function EventsTab() {
  const { user } = useAuth();
  const apartmentId = user?.apartmentId ?? 0;
  const [events, setEvents] = useState<EventResponse[]>([]);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState<EventRequest>(emptyForm(apartmentId));
  const [toast, setToast] = useState("");
  const load = useCallback(() => { if (!apartmentId) return; eventApi.getUpcoming(apartmentId).then(setEvents).catch(() => setToast("Could not load events.")); }, [apartmentId]);
  useEffect(load, [load]);
  const handleCreate = async () => {
    try { await eventApi.create(form); setToast("Event created."); setOpen(false); setForm(emptyForm(apartmentId)); load(); }
    catch (err: any) { setToast(err?.response?.data?.details || "Could not create event. Check the date is in the future."); }
  };

  return (
    <Box>
      <Stack direction="row" sx={{ justifyContent: "space-between", alignItems: "center", mb: 2.5 }}>
        <Box><Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25" }}>Community Events</Typography><Typography variant="body2" sx={{ color: "#6B7A5C" }}>{events.length} upcoming</Typography></Box>
        <Button startIcon={<AddIcon />} variant="contained" onClick={() => setOpen(true)} sx={{ bgcolor: "#C05F3C", "&:hover": { bgcolor: "#A24A2C" }, fontWeight: 700, borderRadius: 2.5, px: 2.5, boxShadow: "0 6px 16px rgba(192,95,60,0.22)" }}>Create event</Button>
      </Stack>
      <Grid container spacing={2.5}>
        {events.map((e) => {
          const pct = e.capacity ? Math.min(100, (e.currentRsvps / e.capacity) * 100) : 0;
          const d = new Date(e.eventDate);
          return (
            <Grid size={{ xs: 12, md: 6 }} key={e.eventId}>
              <Card sx={{ borderRadius: 3, border: e.isFull ? "1.5px solid #F87171" : "1px solid #E6DCC9", bgcolor: "#FFFDF9", overflow: "hidden", boxShadow: "0 4px 14px rgba(46,58,37,0.04)", "&:hover": { boxShadow: "0 10px 26px rgba(46,58,37,0.08)" }, transition: ".2s" }}>
                <CardContent sx={{ p: 2.5 }}>
                  <Stack direction="row" spacing={1.5} sx={{ alignItems: "flex-start" }}>
                    <Box sx={{ width: 54, height: 54, borderRadius: 2, bgcolor: e.isFull ? "#FEE2E2" : "#FAF5EC", border: `1px solid ${e.isFull ? "#FECACA" : "#E6DCC9"}`, display: "grid", placeItems: "center", flexShrink: 0, textAlign: "center" }}>
                      <Box><Typography sx={{ fontWeight: 900, color: e.isFull ? "#DC2626" : "#C05F3C", lineHeight: 1, fontSize: 18 }}>{d.getDate()}</Typography><Typography variant="caption" sx={{ color: "#6B7A5C", fontWeight: 800, fontSize: 10, textTransform: "uppercase" }}>{d.toLocaleString("en", { month: "short" })}</Typography></Box>
                    </Box>
                    <Box sx={{ flex: 1, minWidth: 0 }}>
                      <Stack direction="row" sx={{ justifyContent: "space-between", gap: 1, alignItems: "flex-start" }}>
                        <Typography variant="subtitle1" sx={{ fontWeight: 800, color: "#2E3A25", lineHeight: 1.25 }}>{e.title}</Typography>
                        {e.isFull && <Chip size="small" label="Full" color="error" sx={{ fontWeight: 700, height: 20 }} />}
                      </Stack>
                      <Stack direction="row" spacing={1} sx={{ mt: 0.5, color: "#6B7A5C", alignItems: "center" }}>
                        <EventIcon sx={{ fontSize: 14 }} /><Typography variant="caption" sx={{ fontWeight: 600 }}>{d.toLocaleString()}</Typography>
                      </Stack>
                    </Box>
                  </Stack>
                  <Typography variant="body2" sx={{ mt: 1.5, color: "#2E3A25", lineHeight: 1.6 }}>{e.description}</Typography>
                  <Stack direction="row" spacing={1} sx={{ alignItems: "center", mt: 1.8 }}>
                    <PeopleIcon sx={{ fontSize: 16, color: "#B08442" }} /><Typography variant="caption" sx={{ color: "#6B7A5C", fontWeight: 700 }}>{e.currentRsvps}{e.capacity ? ` / ${e.capacity}` : ""} RSVPs</Typography>
                    <Box sx={{ flex: 1 }} />{e.capacity && <Typography variant="caption" sx={{ color: e.isFull ? "#DC2626" : "#6B8F52", fontWeight: 800 }}>{e.isFull ? "Full" : `${e.capacity - e.currentRsvps} spots left`}</Typography>}
                  </Stack>
                  {e.capacity ? <LinearProgress variant="determinate" value={pct} sx={{ mt: 1, height: 6, borderRadius: 2, bgcolor: "#FAF5EC", "& .MuiLinearProgress-bar": { bgcolor: e.isFull ? "#EF4444" : pct > 75 ? "#F59E0B" : "#6B8F52", borderRadius: 2 } }} /> : null}
                </CardContent>
              </Card>
            </Grid>
          );
        })}
        {events.length === 0 && <Grid size={12}><Box sx={{ p: 5, textAlign: "center", bgcolor: "#FFFDF9", borderRadius: 3, border: "1px dashed #E6DCC9" }}><Typography sx={{ color: "#6B7A5C" }}>No upcoming events. Create one to bring the community together.</Typography></Box></Grid>}
      </Grid>
      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth slotProps={{ paper: { sx: { borderRadius: 3 } } }}>
        <DialogTitle sx={{ fontWeight: 800, color: "#2E3A25" }}>Create event</DialogTitle>
        <DialogContent><Stack spacing={2} sx={{ mt: 1 }}>
          <TextField label="Title" fullWidth value={form.title} onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))} />
          <TextField label="Description" fullWidth multiline rows={3} value={form.description} onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))} />
          <TextField label="Date & time" type="datetime-local" fullWidth value={form.eventDate} onChange={(e) => setForm((f) => ({ ...f, eventDate: e.target.value }))} slotProps={{ inputLabel: { shrink: true } }} />
          <TextField label="Capacity (optional)" type="number" fullWidth value={form.capacity ?? ""} onChange={(e) => setForm((f) => ({ ...f, capacity: e.target.value ? Number(e.target.value) : null }))} />
        </Stack></DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}><Button onClick={() => setOpen(false)} sx={{ color: "#6B7A5C" }}>Cancel</Button><Button variant="contained" onClick={handleCreate} sx={{ bgcolor: "#C05F3C", "&:hover": { bgcolor: "#A24A2C" }, fontWeight: 700 }}>Create</Button></DialogActions>
      </Dialog>
      <Snackbar open={!!toast} autoHideDuration={4000} onClose={() => setToast("")}><Alert severity="info" onClose={() => setToast("")} sx={{ bgcolor: "#2E3A25", color: "#fff", fontWeight: 600 }}>{toast}</Alert></Snackbar>
    </Box>
  );
}