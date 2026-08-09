import { useEffect, useState, useCallback, useMemo } from "react";
import { Box, Typography, Card, CardContent, Chip, Stack, Snackbar, Alert, TextField, MenuItem, IconButton } from "@mui/material";
import ClearIcon from "@mui/icons-material/Clear";
import { bookingApi } from "../../api/bookingApi";
import { useAuth } from "../../auth/AuthContext";
import type { AdminBookingResponse } from "../../api/types";
import { statusColor } from "../../theme/theme";

export default function BookingsTab() {
  const { user } = useAuth();
  const apartmentId = user?.apartmentId ?? 0;
  const [bookings, setBookings] = useState<AdminBookingResponse[]>([]);
  const [todayCount, setTodayCount] = useState<number | null>(null);
  const [toast, setToast] = useState("");
  const [filter, setFilter] = useState("ALL");
  const [dateFilter, setDateFilter] = useState("");
  const [sortBy, setSortBy] = useState("DATE_DESC");

  const load = useCallback(() => {
    if (!apartmentId) return;
    bookingApi.getByApartment(apartmentId).then(setBookings).catch(() => setToast("Could not load bookings."));
    bookingApi.getTodayCount(apartmentId).then(setTodayCount).catch(() => {});
  }, [apartmentId]);
  useEffect(load, [load]);

  const filteredAndSorted = useMemo(() => {
    let list = [...bookings];
    if (filter !== "ALL") list = list.filter(b => b.bookingStatus === filter);
    if (dateFilter) list = list.filter(b => b.bookingDate === dateFilter);
    list.sort((a, b) => {
      if (sortBy === "DATE_DESC") return new Date(`${b.bookingDate}T${b.startTime}`).getTime() - new Date(`${a.bookingDate}T${a.startTime}`).getTime();
      if (sortBy === "DATE_ASC") return new Date(`${a.bookingDate}T${a.startTime}`).getTime() - new Date(`${b.bookingDate}T${b.startTime}`).getTime();
      if (sortBy === "AMENITY_ASC") return a.amenityName.localeCompare(b.amenityName);
      if (sortBy === "AMENITY_DESC") return b.amenityName.localeCompare(a.amenityName);
      return 0;
    });
    return list;
  }, [bookings, filter, dateFilter, sortBy]);

  return (
    <Box>
      <Stack direction={{ xs: "column", sm: "row" }} sx={{ justifyContent: "space-between", gap: 2, mb: 2.5 }}>
        <Box>
          <Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25" }}>Bookings</Typography>
          <Typography variant="body2" sx={{ color: "#6B7A5C" }}>{bookings.length} total · {bookings.filter(b=>b.bookingStatus==="BOOKED").length} active</Typography>
        </Box>
        <Stack direction={{ xs: "column", sm: "row" }} spacing={1.5} sx={{ alignItems: { xs: "stretch", sm: "center" } }}>
          <TextField select size="small" value={filter} onChange={e=>setFilter(e.target.value)} sx={{ minWidth: 135, bgcolor: "#FFFDF9" }}>
            <MenuItem value="ALL">All statuses</MenuItem><MenuItem value="BOOKED">Booked</MenuItem><MenuItem value="CANCELLED">Cancelled</MenuItem>
          </TextField>
          <TextField
            size="small"
            type="date"
            label="Filter by date"
            value={dateFilter}
            onChange={(e) => setDateFilter(e.target.value)}
            slotProps={{
              inputLabel: {
                shrink: true,
              },
              input: {
                endAdornment: dateFilter ? (
                  <IconButton
                    size="small"
                    onClick={() => setDateFilter("")}
                  >
                    <ClearIcon fontSize="small" />
                  </IconButton>
                ) : undefined,
              },
            }}
            sx={{
              bgcolor: "#FFFDF9",
              minWidth: 170,
            }}
          />
          <TextField select size="small" value={sortBy} onChange={e=>setSortBy(e.target.value)} sx={{ minWidth: 155, bgcolor: "#FFFDF9" }} label="Sort by">
            <MenuItem value="DATE_DESC">Date: Newest first</MenuItem>
            <MenuItem value="DATE_ASC">Date: Oldest first</MenuItem>
            <MenuItem value="AMENITY_ASC">Amenity A–Z</MenuItem>
            <MenuItem value="AMENITY_DESC">Amenity Z–A</MenuItem>
          </TextField>
          <Card sx={{ minWidth: 110, bgcolor: "#C05F3C", color: "#fff", borderRadius: 2.5, boxShadow: "0 6px 16px rgba(192,95,60,0.25)" }}>
            <CardContent sx={{ py: 1.2, px: 2, "&:last-child": { pb: 1.2 } }}>
              <Typography variant="caption" sx={{ opacity: 0.85, fontWeight: 700, letterSpacing: 0.5 }}>TODAY</Typography>
              <Typography variant="h5" sx={{ fontWeight: 900, lineHeight: 1 }}>{todayCount ?? "—"}</Typography>
            </CardContent>
          </Card>
        </Stack>
      </Stack>

      <Box sx={{ borderRadius: 3, border: "1px solid #E6DCC9", overflow: "hidden", bgcolor: "#FFFDF9", boxShadow: "0 4px 14px rgba(46,58,37,0.04)" }}>
        <Box sx={{ overflowX: "auto" }}>
          <Box component="table" sx={{ width: "100%", borderCollapse: "collapse", minWidth: 640 }}>
            <Box component="thead" sx={{ bgcolor: "#FAF5EC", borderBottom: "1px solid #E6DCC9" }}>
              <Box component="tr">{["Resident","Flat","Amenity","Date","Time","Status"].map((h) => (
                <Box component="th" key={h} sx={{ px: 2.5, py: 1.4, textAlign: "left", fontWeight: 800, color: "#2E3A25", fontSize: "0.72rem", letterSpacing: "0.07em", textTransform: "uppercase", whiteSpace: "nowrap" }}>{h}</Box>
              ))}</Box>
            </Box>
            <Box component="tbody">
              {filteredAndSorted.map((b) => (
                <Box component="tr" key={b.bookingId} sx={{ borderBottom: "1px solid #F0EDD8", "&:last-child": { borderBottom: 0 }, "&:hover": { bgcolor: "#FAF5EC" }, transition: ".15s" }}>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, color: "#2E3A25", fontWeight: 700, whiteSpace: "nowrap" }}>{b.residentName}</Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8 }}><Chip label={b.flatNumber} size="small" sx={{ bgcolor: "#E9EBDD", color: "#2E3A25", fontWeight: 700, height: 22 }} /></Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, color: "#2E3A25", fontWeight: 600 }}>{b.amenityName}</Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, color: "#6B7A5C", whiteSpace: "nowrap" }}>{b.bookingDate}</Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, color: "#6B7A5C", fontFamily: "monospace", fontSize: 13, whiteSpace: "nowrap" }}>{b.startTime?.slice(0,5)} – {b.endTime?.slice(0,5)}</Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8 }}><Chip size="small" label={b.bookingStatus} color={statusColor[b.bookingStatus] as any} sx={{ fontWeight: 700 }} /></Box>
                </Box>
              ))}
              {filteredAndSorted.length === 0 && (
                <Box component="tr"><Box component="td" colSpan={6} sx={{ px: 2.5, py: 5, textAlign: "center", color: "#6B7A5C" }}>No bookings for this filter.</Box></Box>
              )}
            </Box>
          </Box>
        </Box>
      </Box>
      <Snackbar open={!!toast} autoHideDuration={4000} onClose={() => setToast("")}><Alert severity="info" onClose={() => setToast("")} sx={{ bgcolor: "#2E3A25", color: "#fff", fontWeight: 600 }}>{toast}</Alert></Snackbar>
    </Box>
  );
}
