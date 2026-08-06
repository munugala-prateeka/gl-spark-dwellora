import { useEffect, useState, useCallback, useMemo } from "react";
import {
  Box, Typography, Chip, Stack, TextField, Button, Dialog, DialogTitle,
  DialogContent, DialogActions, Snackbar, Alert, Paper, IconButton,
  Avatar, Tooltip, InputAdornment,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import SearchIcon from "@mui/icons-material/Search";
import PeopleIcon from "@mui/icons-material/People";
import { userApi } from "../../api/userApi";
import { useAuth } from "../../auth/AuthContext";
import type { ResidentRequest, UserResponse } from "../../api/types";
import { statusColor } from "../../theme/theme";

const emptyForm = (apartmentId: number): ResidentRequest => ({
  apartmentId, fullName: "", email: "", phone: "", flatNumber: "",
});

export default function ResidentsTab() {
  const { user } = useAuth();
  const apartmentId = user?.apartmentId ?? 0;
  const [residents, setResidents] = useState<UserResponse[]>([]);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState<ResidentRequest>(emptyForm(apartmentId));
  const [toast, setToast] = useState("");
  const [q, setQ] = useState("");

  const load = useCallback(() => {
    if (!apartmentId) return;
    userApi.getResidentsByApartment(apartmentId).then(setResidents).catch(() => setToast("Could not load residents."));
  }, [apartmentId]);

  useEffect(load, [load]);

  const filtered = useMemo(() => {
    const s = q.trim().toLowerCase();
    if (!s) return residents;
    return residents.filter(r => `${r.fullName} ${r.email} ${r.flatNumber} ${r.phone}`.toLowerCase().includes(s));
  }, [residents, q]);

  const set = (field: keyof ResidentRequest, value: string) => setForm((f) => ({ ...f, [field]: value }));

  const handleAdd = async () => {
    try {
      await userApi.createResident(form);
      setToast("Resident added — an activation email has been sent.");
      setOpen(false);
      setForm(emptyForm(apartmentId));
      load();
    } catch (err: any) {
      setToast(err?.response?.data?.details || "Could not add resident.");
    }
  };

  const handleRemove = async (id: number) => {
    try {
      await userApi.deleteResident(id);
      setToast("Resident removed.");
      load();
    } catch {
      setToast("Could not remove resident.");
    }
  };

  return (
    <Box>
      <Stack direction={{ xs: "column", sm: "row" }} sx={{ justifyContent: "space-between", gap: 2, mb: 2.5 }}>
        <Box>
          <Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25" }}>Residents</Typography>
          <Stack direction="row" spacing={1} sx={{ mt: 0.5, alignItems: "center" }}>
            <Chip label={`${residents.length} total`} size="small" sx={{ bgcolor: "#E9EBDD", color: "#2E3A25", fontWeight: 700, height: 22 }} />
            <Chip label={`${residents.filter(r=>r.accountStatus==="ACTIVE").length} active`} size="small" sx={{ bgcolor: "#E8F5E9", color: "#2E7D32", fontWeight: 700, height: 22 }} />
            {residents.filter(r=>r.accountStatus==="PENDING_ACTIVATION").length > 0 && (
              <Chip label={`${residents.filter(r=>r.accountStatus==="PENDING_ACTIVATION").length} pending`} size="small" sx={{ bgcolor: "#FFF8E1", color: "#8F6A31", fontWeight: 700, height: 22 }} />
            )}
          </Stack>
        </Box>
        <Stack direction="row" spacing={1.5} sx={{ alignItems: "center" }}>
          <TextField
            size="small"
            placeholder="Search name, flat, email..."
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
                width: 240,
                "& .MuiOutlinedInput-root": {
                  bgcolor: "#FFFDF9",
                  borderRadius: 2.5,
                  "& fieldset": { borderColor: "#E6DCC9" },
                },
              }}
            />
          <Button startIcon={<AddIcon />} variant="contained" onClick={() => setOpen(true)} sx={{ bgcolor: "#C05F3C", "&:hover": { bgcolor: "#A24A2C" }, fontWeight: 700, borderRadius: 2.5, px: 2.5, boxShadow: "0 6px 16px rgba(192,95,60,0.22)", whiteSpace: "nowrap" }}>
            Add resident
          </Button>
        </Stack>
      </Stack>

      <Paper sx={{ borderRadius: 3, border: "1px solid #E6DCC9", overflow: "hidden", boxShadow: "0 4px 14px rgba(46,58,37,0.04)" }}>
        <Box sx={{ overflowX: "auto" }}>
          <Box component="table" sx={{ width: "100%", borderCollapse: "collapse", minWidth: 680 }}>
            <Box component="thead" sx={{ bgcolor: "#FAF5EC", borderBottom: "1px solid #E6DCC9" }}>
              <Box component="tr">
                {["Resident","Flat","Contact","Status",""].map((h) => (
                  <Box component="th" key={h} sx={{ px: 2.5, py: 1.4, textAlign: "left", fontWeight: 800, color: "#2E3A25", fontSize: "0.72rem", letterSpacing: "0.07em", textTransform: "uppercase", whiteSpace: "nowrap" }}>{h}</Box>
                ))}
              </Box>
            </Box>
            <Box component="tbody">
              {filtered.map((r) => (
                <Box component="tr" key={r.userId} sx={{ borderBottom: "1px solid #F0EDD8", "&:last-child": { borderBottom: 0 }, "&:hover": { bgcolor: "#FAF5EC" }, transition: ".15s" }}>
                  <Box component="td" sx={{ px: 2.5, py: 1.8 }}>
                    <Stack direction="row" spacing={1.5} sx={{ alignItems: "center" }}>
                      <Avatar sx={{ width: 36, height: 36, bgcolor: "#E9EBDD", color: "#2E3A25", fontWeight: 800, fontSize: 13, border: "1px solid #DDE0CB" }}>{r.fullName.charAt(0).toUpperCase()}</Avatar>
                      <Typography sx={{ color: "#2E3A25", fontWeight: 700, whiteSpace: "nowrap" }}>{r.fullName}</Typography>
                    </Stack>
                  </Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8 }}>
                    <Chip label={r.flatNumber} size="small" sx={{ bgcolor: "#FFFDF9", border: "1px solid #E6DCC9", color: "#2E3A25", fontWeight: 800, height: 24 }} />
                  </Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8 }}>
                    <Typography variant="body2" sx={{ color: "#2E3A25", fontWeight: 600, lineHeight: 1.2 }}>{r.email}</Typography>
                    <Typography variant="caption" sx={{ color: "#6B7A5C" }}>{r.phone}</Typography>
                  </Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8 }}>
                    <Chip size="small" label={r.accountStatus} color={statusColor[r.accountStatus] as any} sx={{ fontWeight: 700 }} />
                  </Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, textAlign: "right" }}>
                    <Tooltip title="Remove resident">
                      <IconButton size="small" onClick={() => handleRemove(r.userId)} sx={{ bgcolor: "#FFF1ED", border: "1px solid #F0C9B8", "&:hover": { bgcolor: "#FFE4DB" } }}>
                        <DeleteIcon fontSize="small" sx={{ color: "#C05F3C" }} />
                      </IconButton>
                    </Tooltip>
                  </Box>
                </Box>
              ))}
              {filtered.length === 0 && (
                <Box component="tr">
                  <Box component="td" colSpan={5} sx={{ px: 2.5, py: 6, textAlign: "center" }}>
                    <Box sx={{ width: 48, height: 48, borderRadius: 3, bgcolor: "#FAF5EC", border: "1px solid #E6DCC9", display: "grid", placeItems: "center", mx: "auto", mb: 1.5, color: "#B08442" }}><PeopleIcon /></Box>
                    <Typography sx={{ color: "#2E3A25", fontWeight: 700 }}>{residents.length === 0 ? "No residents yet" : "No matches found"}</Typography>
                    <Typography variant="body2" sx={{ color: "#6B7A5C" }}>{residents.length === 0 ? "Add your first resident to get started" : `No results for "${q}"`}</Typography>
                  </Box>
                </Box>
              )}
            </Box>
          </Box>
        </Box>
      </Paper>

      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth slotProps={{ paper: { sx: { borderRadius: 3 } } }}>
        <DialogTitle sx={{ fontWeight: 800, color: "#2E3A25" }}>Add resident</DialogTitle>
        <DialogContent>
          <Stack spacing={2.5} sx={{ mt: 1 }}>
            <TextField label="Full name" fullWidth value={form.fullName} onChange={(e) => set("fullName", e.target.value)} />
            <TextField label="Email" fullWidth value={form.email} onChange={(e) => set("email", e.target.value)} />
            <TextField label="Phone" fullWidth value={form.phone} onChange={(e) => set("phone", e.target.value)} />
            <TextField label="Flat number" fullWidth value={form.flatNumber} onChange={(e) => set("flatNumber", e.target.value)} placeholder="e.g. A-102" />
          </Stack>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setOpen(false)} sx={{ color: "#6B7A5C", fontWeight: 600 }}>Cancel</Button>
          <Button variant="contained" onClick={handleAdd} sx={{ bgcolor: "#C05F3C", "&:hover": { bgcolor: "#A24A2C" }, fontWeight: 700, borderRadius: 2, px: 3 }}>Add</Button>
        </DialogActions>
      </Dialog>

      <Snackbar open={!!toast} autoHideDuration={4000} onClose={() => setToast("")}>
        <Alert severity="info" onClose={() => setToast("")} sx={{ bgcolor: "#2E3A25", color: "#fff", fontWeight: 600 }}>{toast}</Alert>
      </Snackbar>
    </Box>
  );
}
