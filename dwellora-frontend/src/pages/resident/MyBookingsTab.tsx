import { useEffect, useState, useCallback, useMemo } from "react";
import { Box, Typography, Chip, Stack, Button, Snackbar, Alert, Paper, TextField, MenuItem } from "@mui/material";
import { bookingApi } from "../../api/amenityApi";
import { useAuth } from "../../auth/AuthContext";
import type { BookingResponse } from "../../api/types";
import { statusColor } from "../../theme/theme";

export default function MyBookingsTab() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState<BookingResponse[]>([]);
  const [toast, setToast] = useState("");
  const [filter, setFilter] = useState("ALL");

  const load = useCallback(() => {
    if (!user) return;
    bookingApi.getByUser(user.userId).then(setBookings).catch(() => setToast("Could not load your bookings."));
  }, [user]);
  useEffect(load, [load]);

  const filtered = useMemo(() => filter === "ALL" ? bookings : bookings.filter(b => b.bookingStatus === filter), [bookings, filter]);

  const handleCancel = async (id: number) => {
    try {
      await bookingApi.cancel(id);
      setToast("Booking cancelled.");
      load();
    } catch (err: any) {
      setToast(err?.response?.data?.details || "Could not cancel this booking.");
    }
  };

  return (
    <Box>
      <Stack direction={{ xs: "column", sm: "row" }} sx={{ justifyContent: "space-between", gap: 2, mb: 2.5 }}>
        <Box>
          <Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25" }}>My Bookings</Typography>
          <Typography variant="body2" sx={{ color: "#6B7A5C" }}>{bookings.length} total · {bookings.filter(b=>b.bookingStatus==="BOOKED").length} active</Typography>
        </Box>
        <TextField select size="small" value={filter} onChange={e=>setFilter(e.target.value)} sx={{ minWidth: 150, bgcolor: "#FFFDF9" }}>
          <MenuItem value="ALL">All</MenuItem><MenuItem value="BOOKED">Booked</MenuItem><MenuItem value="CANCELLED">Cancelled</MenuItem>
        </TextField>
      </Stack>

      <Paper sx={{ borderRadius: 3, border: "1px solid #E6DCC9", overflow: "hidden", boxShadow: "0 4px 14px rgba(46,58,37,0.04)" }}>
        <Box sx={{ overflowX: "auto" }}>
          <Box component="table" sx={{ width: "100%", borderCollapse: "collapse", minWidth: 560 }}>
            <Box component="thead" sx={{ bgcolor: "#FAF5EC", borderBottom: "1px solid #E6DCC9" }}>
              <Box component="tr">{["Amenity","Date","Time","Status",""].map((h) => (
                <Box component="th" key={h} sx={{ px: 2.5, py: 1.4, textAlign: h === "" ? "right" : "left", fontWeight: 800, color: "#2E3A25", fontSize: "0.72rem", letterSpacing: "0.07em", textTransform: "uppercase" }}>{h}</Box>
              ))}</Box>
            </Box>
            <Box component="tbody">
              {filtered.map((b) => (
                <Box component="tr" key={b.bookingId} sx={{ borderBottom: "1px solid #F0EDD8", "&:last-child": { borderBottom: 0 }, "&:hover": { bgcolor: "#FAF5EC" } }}>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, color: "#2E3A25", fontWeight: 700 }}>{b.amenityName}</Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, color: "#6B7A5C" }}>{b.bookingDate}</Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, color: "#6B7A5C", fontFamily: "monospace", fontSize: 13 }}>{b.startTime?.slice(0, 5)} – {b.endTime?.slice(0, 5)}</Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8 }}><Chip size="small" label={b.bookingStatus} color={statusColor[b.bookingStatus] as any} sx={{ fontWeight: 700 }}/></Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, textAlign: "right" }}>
                    {b.bookingStatus === "BOOKED" && <Button size="small" variant="outlined" color="error" onClick={() => handleCancel(b.bookingId)} sx={{ borderRadius: 2, fontWeight: 700 }}>Cancel</Button>}
                  </Box>
                </Box>
              ))}
              {filtered.length === 0 && <Box component="tr"><Box component="td" colSpan={5} sx={{ px: 2.5, py: 5, textAlign: "center", color: "#6B7A5C" }}>{bookings.length === 0 ? "You haven't booked anything yet." : "No bookings in this filter."}</Box></Box>}
            </Box>
          </Box>
        </Box>
      </Paper>
      <Snackbar open={!!toast} autoHideDuration={4000} onClose={() => setToast("")}><Alert severity="info" onClose={() => setToast("")} sx={{ bgcolor: "#2E3A25", color: "#fff", fontWeight: 600 }}>{toast}</Alert></Snackbar>
    </Box>
  );
}