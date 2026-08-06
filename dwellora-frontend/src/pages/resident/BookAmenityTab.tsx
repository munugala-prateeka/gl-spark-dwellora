import { useEffect, useState, useCallback, useMemo } from "react";
import {
  Box, Typography, Card, CardContent, Grid, Chip, Stack, Button, TextField,
  Dialog, DialogTitle, DialogContent, DialogActions, Snackbar, Alert, InputAdornment,
} from "@mui/material";
import EventIcon from "@mui/icons-material/Event";
import PoolIcon from "@mui/icons-material/Pool";
import FitnessCenterIcon from "@mui/icons-material/FitnessCenter";
import SearchIcon from "@mui/icons-material/Search";
import AccessTimeIcon from "@mui/icons-material/AccessTime";
import { amenityApi, bookingApi } from "../../api/amenityApi";
import { useAuth } from "../../auth/AuthContext";
import type { AmenityResponse, AvailabilitySlot } from "../../api/types";

export default function BookAmenityTab() {
  const { user } = useAuth();
  const apartmentId = user?.apartmentId ?? 0;
  const [amenities, setAmenities] = useState<AmenityResponse[]>([]);
  const [selected, setSelected] = useState<AmenityResponse | null>(null);
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10));
  const [slots, setSlots] = useState<AvailabilitySlot[]>([]);
  const [toast, setToast] = useState("");
  const [q, setQ] = useState("");

  useEffect(() => {
    if (!apartmentId) return;
    amenityApi.getByApartment(apartmentId).then((all) => setAmenities(all.filter((a) => a.available)))
      .catch(() => setToast("Could not load amenities."));
  }, [apartmentId]);

  const filtered = useMemo(() => amenities.filter(a => `${a.amenityName} ${a.amenityType}`.toLowerCase().includes(q.toLowerCase())), [amenities, q]);

  const openAmenity = (a: AmenityResponse) => {
    setSelected(a);
    loadSlots(a.amenityId, date);
  };

  const loadSlots = useCallback((amenityId: number, d: string) => {
    bookingApi.getAvailability(amenityId, d).then(setSlots).catch(() => setSlots([]));
  }, []);

  useEffect(() => {
    if (selected) loadSlots(selected.amenityId, date);
  }, [date]);

  const handleBook = async (slot: AvailabilitySlot) => {
    if (!selected || !user) return;
    const [start, end] = slot.slot.split(" - ");
    try {
      await bookingApi.add({
        userId: user.userId,
        amenityId: selected.amenityId,
        bookingDate: date,
        startTime: start.length === 5 ? start + ":00" : start,
        endTime: end.length === 5 ? end + ":00" : end,
      });
      setToast("Booked! Check your notifications for confirmation.");
      loadSlots(selected.amenityId, date);
    } catch (err: any) {
      setToast(err?.response?.data?.details || "Could not book this slot.");
    }
  };

  return (
    <Box>
      <Stack direction={{ xs: "column", sm: "row" }} sx={{ justifyContent: "space-between", gap: 2, mb: 2.5 }}>
        <Box>
          <Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25" }}>Book an Amenity</Typography>
          <Typography variant="body2" sx={{ color: "#6B7A5C" }}>{amenities.length} available for booking</Typography>
        </Box>
        <TextField
          size="small"
          placeholder="Search gym, pool..."
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
            width: 260,
            "& .MuiOutlinedInput-root": {
              bgcolor: "#FFFDF9",
              borderRadius: 2.5,
              "& fieldset": { borderColor: "#E6DCC9" },
            },
          }}
      />
      </Stack>

      {filtered.length === 0 ? (
        <Box sx={{ p: 5, textAlign: "center", bgcolor: "#FFFDF9", borderRadius: 3, border: "1px dashed #E6DCC9" }}>
          <Box sx={{ width: 56, height: 56, borderRadius: 3, bgcolor: "#FAF5EC", display: "grid", placeItems: "center", mx: "auto", mb: 1.5, color: "#B08442" }}><PoolIcon /></Box>
          <Typography sx={{ fontWeight: 700, color: "#2E3A25" }}>{amenities.length === 0 ? "No amenities configured yet" : "No matches found"}</Typography>
          <Typography variant="body2" sx={{ color: "#6B7A5C" }}>{amenities.length === 0 ? "Your manager will add amenities soon" : `No results for "${q}"`}</Typography>
        </Box>
      ) : (
        <Grid container spacing={2.5}>
          {filtered.map((a) => (
            <Grid size={{ xs: 12, sm: 6, md: 4 }} key={a.amenityId}>
              <Card onClick={() => openAmenity(a)} sx={{ cursor: "pointer", borderRadius: 3, border: "1px solid #E6DCC9", bgcolor: "#FFFDF9", boxShadow: "0 4px 14px rgba(46,58,37,0.04)", transition: "all .2s", "&:hover": { transform: "translateY(-4px)", borderColor: "#C05F3C", boxShadow: "0 12px 28px rgba(192,95,60,0.14)" } }}>
                <CardContent sx={{ p: 2.5 }}>
                  <Box sx={{ width: 42, height: 42, borderRadius: 2, bgcolor: "rgba(107,143,82,0.12)", color: "#6B8F52", display: "grid", placeItems: "center", mb: 1.5 }}>
                    {a.amenityType === "GYM" ? <FitnessCenterIcon fontSize="small"/> : <PoolIcon fontSize="small"/>}
                  </Box>
                  <Typography variant="subtitle1" sx={{ fontWeight: 800, color: "#2E3A25", lineHeight: 1.2 }}>{a.amenityName}</Typography>
                  <Chip label={a.amenityType.replaceAll("_", " ")} size="small" sx={{ mt: 0.7, bgcolor: "#FAF5EC", color: "#6B7A5C", fontWeight: 600, fontSize: 11, height: 20 }}/>
                  <Stack direction="row" spacing={1} sx={{ mt: 1.5, alignItems: "center", color: "#6B7A5C" }}>
                    <AccessTimeIcon sx={{ fontSize: 14, color: "#B08442" }}/><Typography variant="caption" sx={{ fontWeight: 700 }}>{a.openingTime?.slice(0, 5)} – {a.closingTime?.slice(0, 5)}</Typography>
                  </Stack>
                  <Chip size="small" sx={{ mt: 1.2, bgcolor: "#E9EBDD", color: "#2E3A25", fontWeight: 700, height: 22 }} label={a.bookingPolicy.replace("_", " ")} />
                  <Button fullWidth variant="contained" sx={{ mt: 2, bgcolor: "#C05F3C", "&:hover": { bgcolor: "#A24A2C" }, fontWeight: 700, borderRadius: 2 }}>View slots</Button>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      <Dialog open={!!selected} onClose={() => setSelected(null)} maxWidth="sm" fullWidth slotProps={{ paper: { sx: { borderRadius: 3 } } }}>
        <DialogTitle sx={{ fontWeight: 800, color: "#2E3A25", display: "flex", alignItems: "center", gap: 1.5 }}>
          <Box sx={{ width: 36, height: 36, borderRadius: 2, bgcolor: "#C05F3C", color: "#fff", display: "grid", placeItems: "center" }}><EventIcon fontSize="small"/></Box>{selected?.amenityName}
        </DialogTitle>
        <DialogContent>
          <TextField label="Date" type="date" fullWidth sx={{ mt: 1, "& .MuiOutlinedInput-root.Mui-focused fieldset": { borderColor: "#C05F3C" }, "& label.Mui-focused": { color: "#C05F3C" } }} value={date} onChange={(e) => setDate(e.target.value)} slotProps={{ inputLabel: { shrink: true } }}/>
          <Typography variant="caption" sx={{ color: "#6B7A5C", fontWeight: 700, letterSpacing: 0.5, mt: 2, display: "block" }}>{slots.length} SLOTS FOR {date}</Typography>
          <Grid container spacing={1.5} sx={{ mt: 1 }}>
            {slots.map((s) => (
              <Grid size={6} key={s.slot}>
                <Button fullWidth variant={s.remaining > 0 ? "outlined" : "text"} disabled={s.remaining <= 0} onClick={() => handleBook(s)} sx={{ justifyContent: "space-between", borderRadius: 2, py: 1.2, borderColor: s.remaining > 0 ? "#C05F3C" : "#E6DCC9", color: s.remaining > 0 ? "#2E3A25" : "#6B7A5C", fontWeight: 700, "&:hover": { bgcolor: s.remaining > 0 ? "rgba(192,95,60,0.06)" : undefined, borderColor: "#C05F3C" } }} endIcon={<Chip size="small" label={`${s.remaining} left`} sx={{ bgcolor: s.remaining > 0 ? "#6B8F52" : "#E6DCC9", color: s.remaining > 0 ? "#fff" : "#6B7A5C", fontWeight: 700, height: 20 }}/>}>
                  {s.slot}
                </Button>
              </Grid>
            ))}
            {slots.length === 0 && <Grid size={12}><Box sx={{ p: 3, textAlign: "center", bgcolor: "#FAF5EC", borderRadius: 2, border: "1px solid #E6DCC9", mt: 1 }}><Typography sx={{ color: "#6B7A5C" }}>No slots for this date.</Typography></Box></Grid>}
          </Grid>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}><Button onClick={() => setSelected(null)} sx={{ color: "#6B7A5C", fontWeight: 600 }}>Close</Button></DialogActions>
      </Dialog>
      <Snackbar open={!!toast} autoHideDuration={4000} onClose={() => setToast("")}><Alert severity="info" onClose={() => setToast("")} sx={{ bgcolor: "#2E3A25", color: "#fff", fontWeight: 600 }}>{toast}</Alert></Snackbar>
    </Box>
  );
}