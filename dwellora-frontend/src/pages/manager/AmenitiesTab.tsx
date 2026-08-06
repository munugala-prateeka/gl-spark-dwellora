import { useEffect, useState, useCallback, useMemo } from "react";
import {
  Box, Typography, Card, CardContent, Grid, Chip, Stack, Button,
  Dialog, DialogTitle, DialogContent, DialogActions, TextField,
  MenuItem, IconButton, Snackbar, Alert, FormControlLabel, Switch, InputAdornment, Tooltip
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import SearchIcon from "@mui/icons-material/Search";
import PoolIcon from "@mui/icons-material/Pool";
import FitnessCenterIcon from "@mui/icons-material/FitnessCenter";
import { amenityApi } from "../../api/amenityApi";
import { useAuth } from "../../auth/AuthContext";
import type { AmenityRequest, AmenityResponse, AmenityType, BookingPolicy } from "../../api/types";

const AMENITY_TYPES: AmenityType[] = ["GYM","SWIMMING_POOL","COMMUNITY_HALL","BADMINTON_COURT","TENNIS_COURT","BASKETBALL_COURT","YOGA_STUDIO","CHILDRENS_PLAY_AREA","PARTY_HALL","LIBRARY"];
const emptyForm = (apartmentId: number): AmenityRequest => ({
  apartmentId, amenityName: "", amenityType: "GYM", capacity: 1, available: true,
  openingTime: "06:00:00", closingTime: "22:00:00", bookingPolicy: "PER_PERSON", slotDurationMinutes: 60, maxBookingsPerDay: 1, maxBookingsPerMonth: null,
});

export default function AmenitiesTab() {
  const { user } = useAuth();
  const apartmentId = user?.apartmentId ?? 0;
  const [amenities, setAmenities] = useState<AmenityResponse[]>([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<AmenityResponse | null>(null);
  const [form, setForm] = useState<AmenityRequest>(emptyForm(apartmentId));
  const [toast, setToast] = useState("");
  const [search, setSearch] = useState("");

  const load = useCallback(() => {
    if (!apartmentId) return;
    amenityApi.getByApartment(apartmentId).then(setAmenities).catch(() => setToast("Could not load amenities."));
  }, [apartmentId]);
  useEffect(load, [load]);

  const filtered = useMemo(() => amenities.filter(a => a.amenityName.toLowerCase().includes(search.toLowerCase()) || a.amenityType.toLowerCase().includes(search.toLowerCase())), [amenities, search]);

  const openNew = () => { setEditing(null); setForm(emptyForm(apartmentId)); setOpen(true); };
  const openEdit = (a: AmenityResponse) => { setEditing(a); setForm({ ...a }); setOpen(true); };
  const set = <K extends keyof AmenityRequest>(field: K, value: AmenityRequest[K]) => setForm((f) => ({ ...f, [field]: value }));

  const handleSave = async () => {
    try {
      if (editing) { await amenityApi.update(editing.amenityId, form); setToast("Amenity updated."); }
      else { await amenityApi.add(form); setToast("Amenity added."); }
      setOpen(false); load();
    } catch (err: any) { setToast(err?.response?.data?.details || "Could not save amenity."); }
  };
  const handleDelete = async (id: number) => {
    try { await amenityApi.remove(id); setToast("Amenity removed."); load(); } catch { setToast("Could not remove amenity."); }
  };

  return (
    <Box>
      <Stack direction={{ xs: "column", sm: "row" }} sx={{ justifyContent: "space-between", gap: 2, mb: 3 }}>
        <Box>
          <Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25" }}>Amenities</Typography>
          <Typography variant="body2" sx={{ color: "#6B7A5C" }}>{amenities.length} configured · {amenities.filter(a=>a.available).length} available</Typography>
        </Box>
        <Stack direction="row" spacing={1.5} sx={{ alignItems: "center" }}>
          <TextField
            size="small"
            placeholder="Search amenities..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
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
              bgcolor: "#FFFDF9",
              borderRadius: 2,
              width: 220,
              "& fieldset": { borderColor: "#E6DCC9" },
            }}
          />
          <Button startIcon={<AddIcon />} variant="contained" onClick={openNew} sx={{ bgcolor: "#C05F3C", "&:hover": { bgcolor: "#A24A2C" }, fontWeight: 700, borderRadius: 2.5, px: 2.5, whiteSpace: "nowrap", boxShadow: "0 6px 16px rgba(192,95,60,0.22)" }}>
            Add amenity
          </Button>
        </Stack>
      </Stack>

      {filtered.length === 0 ? (
        <Box sx={{ p: 5, textAlign: "center", bgcolor: "#FFFDF9", borderRadius: 3, border: "1px dashed #E6DCC9" }}>
          <Box sx={{ width: 56, height: 56, borderRadius: 3, bgcolor: "#FAF5EC", display: "grid", placeItems: "center", mx: "auto", mb: 1.5, color: "#B08442" }}><PoolIcon /></Box>
          <Typography sx={{ fontWeight: 700, color: "#2E3A25" }}>{amenities.length === 0 ? "No amenities yet" : "No matches found"}</Typography>
          <Typography variant="body2" sx={{ color: "#6B7A5C", mb: 2 }}>{amenities.length === 0 ? "Add your first amenity to enable bookings" : "Try a different search term"}</Typography>
          {amenities.length === 0 && <Button variant="contained" onClick={openNew} sx={{ bgcolor: "#C05F3C" }}>Add amenity</Button>}
        </Box>
      ) : (
        <Grid container spacing={2.5}>
          {filtered.map((a) => (
            <Grid size={{ xs: 12, sm: 6, md: 4 }} key={a.amenityId}>
              <Card sx={{ borderRadius: 3, border: "1px solid #E6DCC9", bgcolor: "#FFFDF9", boxShadow: "0 4px 14px rgba(46,58,37,0.04)", transition: "all .2s", "&:hover": { transform: "translateY(-4px)", boxShadow: "0 12px 28px rgba(46,58,37,0.08)", borderColor: "#DDE0CB" } }}>
                <CardContent sx={{ p: 2.5 }}>
                  <Stack direction="row" sx={{ justifyContent: "space-between", alignItems: "flex-start", mb: 1 }}>
                    <Box sx={{ width: 42, height: 42, borderRadius: 2, bgcolor: a.available ? "rgba(107,143,82,0.12)" : "#FAF5EC", color: a.available ? "#6B8F52" : "#B08442", display: "grid", placeItems: "center" }}>
                      {a.amenityType === "GYM" ? <FitnessCenterIcon fontSize="small"/> : <PoolIcon fontSize="small"/>}
                    </Box>
                    <Chip size="small" label={a.available ? "Available" : "Unavailable"} sx={{ fontWeight: 700, bgcolor: a.available ? "#6B8F52" : "#E6DCC9", color: a.available ? "#fff" : "#6B7A5C", height: 22 }} />
                  </Stack>
                  <Typography variant="subtitle1" sx={{ fontWeight: 800, color: "#2E3A25", lineHeight: 1.2 }}>{a.amenityName}</Typography>
                  <Chip label={a.amenityType.replaceAll("_", " ")} size="small" sx={{ mt: 0.7, bgcolor: "#FAF5EC", color: "#6B7A5C", fontWeight: 600, fontSize: 11, height: 20 }} />
                  <Box sx={{ mt: 1.8, p: 1.5, bgcolor: "#FAF5EC", borderRadius: 2, border: "1px solid #F0EDD8" }}>
                    <Typography variant="caption" sx={{ color: "#B08442", fontWeight: 800, letterSpacing: 0.5 }}>{a.openingTime?.slice(0,5)} – {a.closingTime?.slice(0,5)} · {a.slotDurationMinutes} min slots</Typography>
                    <Typography variant="body2" sx={{ color: "#2E3A25", fontWeight: 600, mt: 0.3 }}>Capacity {a.capacity} · {a.bookingPolicy.replace("_"," ")} · {a.maxBookingsPerDay}/day{a.maxBookingsPerMonth?` · ${a.maxBookingsPerMonth}/mo`:""}</Typography>
                  </Box>
                  <Stack direction="row" spacing={1} sx={{ mt: 2 }}>
                    <Tooltip title="Edit"><IconButton size="small" onClick={() => openEdit(a)} sx={{ bgcolor: "#FAF5EC", border: "1px solid #E6DCC9", "&:hover": { bgcolor: "#E9EBDD" } }}><EditIcon fontSize="small" sx={{ color: "#B08442" }} /></IconButton></Tooltip>
                    <Tooltip title="Delete"><IconButton size="small" onClick={() => handleDelete(a.amenityId)} sx={{ bgcolor: "#FFF1ED", border: "1px solid #F0C9B8", "&:hover": { bgcolor: "#FFE4DB" } }}><DeleteIcon fontSize="small" sx={{ color: "#C05F3C" }} /></IconButton></Tooltip>
                  </Stack>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth slotProps={{ paper: { sx: { borderRadius: 3 } } }}>
        <DialogTitle sx={{ fontWeight: 800, color: "#2E3A25" }}>{editing ? "Edit amenity" : "Add amenity"}</DialogTitle>
        <DialogContent>
          <Grid container spacing={2.5} sx={{ mt: 0.5 }}>
            <Grid size={12}><TextField label="Name" fullWidth value={form.amenityName} onChange={(e) => set("amenityName", e.target.value)} /></Grid>
            <Grid size={6}><TextField select label="Type" fullWidth value={form.amenityType} onChange={(e) => set("amenityType", e.target.value as AmenityType)}>{AMENITY_TYPES.map((t) => <MenuItem key={t} value={t}>{t.replaceAll("_", " ")}</MenuItem>)}</TextField></Grid>
            <Grid size={6}><TextField select label="Booking policy" fullWidth value={form.bookingPolicy} onChange={(e) => set("bookingPolicy", e.target.value as BookingPolicy)}><MenuItem value="PER_PERSON">Per person</MenuItem><MenuItem value="PER_FLAT">Per flat</MenuItem></TextField></Grid>
            <Grid size={6}><TextField label="Opening time" type="time" fullWidth value={form.openingTime?.slice(0, 5)} onChange={(e) => set("openingTime", e.target.value + ":00")} slotProps={{ inputLabel: { shrink: true } }} /></Grid>
            <Grid size={6}><TextField label="Closing time" type="time" fullWidth value={form.closingTime?.slice(0, 5)} onChange={(e) => set("closingTime", e.target.value + ":00")} slotProps={{ inputLabel: { shrink: true } }} /></Grid>
            <Grid size={4}><TextField label="Capacity" type="number" fullWidth value={form.capacity} onChange={(e) => set("capacity", Number(e.target.value))} /></Grid>
            <Grid size={4}><TextField label="Slot (min)" type="number" fullWidth value={form.slotDurationMinutes} onChange={(e) => set("slotDurationMinutes", Number(e.target.value))} /></Grid>
            <Grid size={4}><TextField label="Max/day" type="number" fullWidth value={form.maxBookingsPerDay} onChange={(e) => set("maxBookingsPerDay", Number(e.target.value))} /></Grid>
            <Grid size={6}><TextField label="Max/month (optional)" type="number" fullWidth value={form.maxBookingsPerMonth ?? ""} onChange={(e) => set("maxBookingsPerMonth", e.target.value ? Number(e.target.value) : null)} /></Grid>
            <Grid size={6}><FormControlLabel control={<Switch checked={form.available} onChange={(e) => set("available", e.target.checked)} />} label="Available" /></Grid>
          </Grid>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}><Button onClick={() => setOpen(false)} sx={{ color: "#6B7A5C", fontWeight: 600 }}>Cancel</Button><Button variant="contained" onClick={handleSave} sx={{ bgcolor: "#C05F3C", "&:hover": { bgcolor: "#A24A2C" }, fontWeight: 700 }}>Save</Button></DialogActions>
      </Dialog>
      <Snackbar open={!!toast} autoHideDuration={4000} onClose={() => setToast("")}><Alert severity="info" onClose={() => setToast("")} sx={{ bgcolor: "#2E3A25", color: "#fff", fontWeight: 600 }}>{toast}</Alert></Snackbar>
    </Box>
  );
}