import { useEffect, useState, useCallback } from "react";
import { Box, Typography, Card, CardContent, Grid, Chip, Stack, Button, Snackbar, Alert, LinearProgress } from "@mui/material";
import EventIcon from "@mui/icons-material/Event";
import PeopleIcon from "@mui/icons-material/People";
import { eventApi } from "../../api/communityApi";
import { useAuth } from "../../auth/AuthContext";
import type { EventResponse } from "../../api/types";

export default function EventsTab() {
  const { user } = useAuth();
  const apartmentId = user?.apartmentId ?? 0;
  const [events, setEvents] = useState<EventResponse[]>([]);
  const [rsvped, setRsvped] = useState<Set<number>>(new Set());
  const [toast, setToast] = useState("");

  const load = useCallback(() => {
    if (!apartmentId || !user) return;
    eventApi.getUpcoming(apartmentId).then(setEvents).catch(() => setToast("Could not load events."));
    eventApi.getMyRsvps(user.userId).then((ids) => setRsvped(new Set(ids))).catch(() => {});
  }, [apartmentId, user]);
  useEffect(load, [load]);

  const handleRsvp = async (eventId: number) => {
    if (!user) return;
    try { await eventApi.rsvp(eventId, user.userId); setRsvped((prev) => new Set(prev).add(eventId)); setToast("You're on the list!"); load(); }
    catch (err: any) { setToast(err?.response?.data?.details || "Could not RSVP — this event may be full."); }
  };
  const handleWithdraw = async (eventId: number) => {
    if (!user) return;
    try { await eventApi.withdrawRsvp(eventId, user.userId); setRsvped((prev) => { const n = new Set(prev); n.delete(eventId); return n; }); setToast("RSVP withdrawn."); load(); }
    catch { setToast("Could not withdraw your RSVP."); }
  };

  return (
    <Box>
      <Stack direction="row" sx={{ justifyContent: "space-between", alignItems: "center", mb: 2 }}>
        <Box><Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25" }}>Community Events</Typography><Typography variant="body2" sx={{ color: "#6B7A5C" }}>{events.length} upcoming · {rsvped.size} joined</Typography></Box>
      </Stack>
      <Grid container spacing={2.5}>
        {events.map((e) => {
          const iAmIn = rsvped.has(e.eventId);
          const d = new Date(e.eventDate);
          const pct = e.capacity ? Math.min(100, (e.currentRsvps / e.capacity) * 100) : 0;
          return (
            <Grid size={{ xs: 12, md: 6 }} key={e.eventId}>
              <Card sx={{ borderRadius: 3, border: iAmIn ? "2px solid #6B8F52" : e.isFull ? "1.5px solid #F87171" : "1px solid #E6DCC9", bgcolor: "#FFFDF9", overflow: "hidden", boxShadow: "0 4px 14px rgba(46,58,37,0.04)" }}>
                <CardContent sx={{ p: 2.5 }}>
                  <Stack direction="row" spacing={1.5} sx={{ alignItems: "flex-start" }}>
                    <Box sx={{ width: 54, height: 54, borderRadius: 2, bgcolor: iAmIn ? "#E8F5E9" : "#FAF5EC", border: `1px solid ${iAmIn ? "#C8E6C9" : "#E6DCC9"}`, display: "grid", placeItems: "center", flexShrink: 0, textAlign: "center" }}>
                      <Box><Typography sx={{ fontWeight: 900, color: "#C05F3C", lineHeight: 1, fontSize: 18 }}>{d.getDate()}</Typography><Typography variant="caption" sx={{ color: "#6B7A5C", fontWeight: 800, fontSize: 10, textTransform: "uppercase" }}>{d.toLocaleString("en", { month: "short" })}</Typography></Box>
                    </Box>
                    <Box sx={{ flex: 1, minWidth: 0 }}>
                      <Stack direction="row" sx={{ justifyContent: "space-between", gap: 1, alignItems: "flex-start" }}>
                        <Typography variant="subtitle1" sx={{ fontWeight: 800, color: "#2E3A25", lineHeight: 1.25 }}>{e.title}</Typography>
                        {e.isFull && <Chip size="small" label="Full" color="error" sx={{ fontWeight: 700, height: 20 }}/>}{iAmIn && <Chip size="small" label="Joined" sx={{ bgcolor: "#6B8F52", color: "#fff", fontWeight: 700, height: 20 }}/>}
                      </Stack>
                      <Stack direction="row" spacing={0.7} sx={{ mt: 0.5, color: "#6B7A5C", alignItems: "center" }}><EventIcon sx={{ fontSize: 14 }}/><Typography variant="caption" sx={{ fontWeight: 600 }}>{d.toLocaleString()}</Typography></Stack>
                    </Box>
                  </Stack>
                  <Typography variant="body2" sx={{ mt: 1.5, color: "#2E3A25", lineHeight: 1.6 }}>{e.description}</Typography>
                  <Stack direction="row" spacing={1} sx={{ alignItems: "center", mt: 1.5 }}><PeopleIcon sx={{ fontSize: 16, color: "#B08442" }}/><Typography variant="caption" sx={{ color: "#6B7A5C", fontWeight: 700 }}>{e.currentRsvps}{e.capacity ? ` / ${e.capacity}` : ""} attending</Typography></Stack>
                  {e.capacity && <LinearProgress variant="determinate" value={pct} sx={{ mt: 1, height: 6, borderRadius: 2, bgcolor: "#FAF5EC", "& .MuiLinearProgress-bar": { bgcolor: e.isFull ? "#EF4444" : iAmIn ? "#6B8F52" : pct > 75 ? "#F59E0B" : "#C05F3C", borderRadius: 2 } }}/>}
                  <Box sx={{ mt: 2 }}>{iAmIn ? <Button fullWidth size="small" variant="outlined" color="error" onClick={() => handleWithdraw(e.eventId)} sx={{ borderRadius: 2, fontWeight: 700 }}>Withdraw RSVP</Button> : <Button fullWidth size="small" variant="contained" disabled={e.isFull} onClick={() => handleRsvp(e.eventId)} sx={{ bgcolor: "#C05F3C", "&:hover": { bgcolor: "#A24A2C" }, fontWeight: 700, borderRadius: 2 }}>{e.isFull ? "Event full" : "RSVP"}</Button>}</Box>
                </CardContent>
              </Card>
            </Grid>
          );
        })}
        {events.length === 0 && <Grid size={12}><Box sx={{ p: 5, textAlign: "center", bgcolor: "#FFFDF9", borderRadius: 3, border: "1px dashed #E6DCC9" }}><Typography sx={{ color: "#6B7A5C" }}>No upcoming events.</Typography></Box></Grid>}
      </Grid>
      <Snackbar open={!!toast} autoHideDuration={4000} onClose={() => setToast("")}><Alert severity="info" onClose={() => setToast("")} sx={{ bgcolor: "#2E3A25", color: "#fff", fontWeight: 600 }}>{toast}</Alert></Snackbar>
    </Box>
  );
}