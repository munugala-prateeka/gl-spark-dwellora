import { useEffect, useState, useMemo } from "react";
import { Box, Typography, Chip, TextField, MenuItem, Stack, Snackbar, Alert, Avatar, InputAdornment } from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import { userApi } from "../../api/userApi";
import { apartmentApi } from "../../api/apartmentApi";
import type { UserResponse, ApartmentResponse } from "../../api/types";
import { statusColor } from "../../theme/theme";

export default function UsersTab() {
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [apartments, setApartments] = useState<ApartmentResponse[]>([]);
  const [roleFilter, setRoleFilter] = useState("ALL");
  const [q, setQ] = useState("");
  const [toast, setToast] = useState("");

  useEffect(() => {
    userApi.getAll().then(setUsers).catch(() => setToast("Could not load users."));
    apartmentApi.getAll().then(setApartments).catch(() => {});
  }, []);

  const apartmentMap = useMemo(() => new Map(apartments.map(a => [a.apartmentId, a.apartmentName])), [apartments]);

  const filtered = useMemo(() => {
    let list = roleFilter === "ALL" ? users : users.filter((u) => u.role === roleFilter);
    if (q.trim()) {
      const s = q.toLowerCase();
      list = list.filter(u => {
        const aptName = u.apartmentId ? apartmentMap.get(u.apartmentId) ?? "" : "";
        return `${u.fullName} ${u.email} ${u.phone} ${aptName}`.toLowerCase().includes(s);
      });
    }
    return list;
  }, [users, roleFilter, q, apartmentMap]);

  return (
    <Box>
      <Stack direction={{ xs: "column", md: "row" }} sx={{ justifyContent: "space-between", gap: 2, mb: 2 }}>
        <Box>
          <Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25" }}>All users</Typography>
          <Typography variant="body2" sx={{ color: "#6B7A5C" }}>{filtered.length} of {users.length} accounts</Typography>
        </Box>
        <Stack direction="row" spacing={1.5} sx={{ alignItems: "center" }}>
          <TextField
            size="small"
            placeholder="Search name, email, apartment..."
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
          <TextField select size="small" value={roleFilter} onChange={(e) => setRoleFilter(e.target.value)} sx={{ minWidth: 160, bgcolor: "#FFFDF9" }}>
            <MenuItem value="ALL">All roles</MenuItem>
            <MenuItem value="PLATFORM_ADMIN">Platform Admin</MenuItem>
            <MenuItem value="MANAGER">Manager</MenuItem>
            <MenuItem value="RESIDENT">Resident</MenuItem>
          </TextField>
        </Stack>
      </Stack>

      <Box sx={{ borderRadius: 3, border: "1px solid #E6DCC9", overflow: "hidden", bgcolor: "#FFFDF9", boxShadow: "0 4px 14px rgba(46,58,37,0.04)" }}>
        <Box sx={{ overflowX: "auto" }}>
          <Box component="table" sx={{ width: "100%", borderCollapse: "collapse", minWidth: 780 }}>
            <Box component="thead" sx={{ bgcolor: "#FAF5EC", borderBottom: "1px solid #E6DCC9" }}>
              <Box component="tr">
                {["User","Role","Apartment","Email","Phone","Status"].map(h=>(
                  <Box component="th" key={h} sx={{ px: 2.5, py: 1.4, textAlign: "left", fontWeight: 800, color: "#2E3A25", fontSize: "0.72rem", letterSpacing: "0.07em", textTransform: "uppercase", whiteSpace: "nowrap" }}>{h}</Box>
                ))}
              </Box>
            </Box>
            <Box component="tbody">
              {filtered.map((u) => (
                <Box component="tr" key={u.userId} sx={{ borderBottom: "1px solid #F0EDD8", "&:last-child":{borderBottom:0}, "&:hover":{bgcolor:"#FAF5EC"}, transition: ".15s" }}>
                  <Box component="td" sx={{ px: 2.5, py: 1.8 }}>
                    <Stack direction="row" spacing={1.5} sx={{ alignItems: "center" }}>
                      <Avatar sx={{ width: 34, height: 34, bgcolor: "#E9EBDD", color: "#2E3A25", fontWeight: 800, fontSize: 13, border: "1px solid #DDE0CB" }}>{u.fullName.charAt(0)}</Avatar>
                      <Typography sx={{ fontWeight: 700, color: "#2E3A25", whiteSpace: "nowrap" }}>{u.fullName}</Typography>
                    </Stack>
                  </Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8 }}><Chip size="small" label={u.role.replace("_", " ")} sx={{ bgcolor: "#E9EBDD", color: "#2E3A25", fontWeight: 700, height: 22, border: "1px solid #DDE0CB" }} /></Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, color: "#2E3A25", fontWeight: 600, whiteSpace: "nowrap" }}>{u.apartmentId ? (apartmentMap.get(u.apartmentId) ?? `#${u.apartmentId}`) : "—"}</Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, color: "#2E3A25", fontWeight: 500 }}>{u.email}</Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8, color: "#6B7A5C" }}>{u.phone ?? "—"}</Box>
                  <Box component="td" sx={{ px: 2.5, py: 1.8 }}><Chip size="small" label={u.accountStatus} color={statusColor[u.accountStatus] as any} sx={{ fontWeight: 700 }} /></Box>
                </Box>
              ))}
              {filtered.length === 0 && <Box component="tr"><Box component="td" colSpan={6} sx={{ px: 2.5, py: 5, textAlign: "center", color: "#6B7A5C" }}>No users found.</Box></Box>}
            </Box>
          </Box>
        </Box>
      </Box>

      <Snackbar open={!!toast} autoHideDuration={4000} onClose={() => setToast("")}>
        <Alert severity="info" onClose={() => setToast("")} sx={{ bgcolor: "#2E3A25", color: "#fff", fontWeight: 600 }}>{toast}</Alert>
      </Snackbar>
    </Box>
  );
}