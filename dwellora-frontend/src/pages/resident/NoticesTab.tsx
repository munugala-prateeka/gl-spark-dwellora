import { useEffect, useState } from "react";
import { Box, Typography, Card, CardContent, Grid, Chip, Stack, Snackbar, Alert } from "@mui/material";
import CampaignIcon from "@mui/icons-material/Campaign";
import { noticeApi } from "../../api/communityApi";
import { useAuth } from "../../auth/AuthContext";
import type { NoticeResponse } from "../../api/types";

export default function NoticesTab() {
  const { user } = useAuth();
  const apartmentId = user?.apartmentId ?? 0;
  const [notices, setNotices] = useState<NoticeResponse[]>([]);
  const [toast, setToast] = useState("");

  useEffect(() => {
    if (!apartmentId) return;
    noticeApi.getActive().then(setNotices).catch(() => setToast("Could not load notices."));
  }, [apartmentId]);

  return (
    <Box>
      <Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25", mb: 0.5 }}>Notice Board</Typography>
      <Typography variant="body2" sx={{ color: "#6B7A5C", mb: 2.5 }}>{notices.length} active · {notices.filter(n=>n.isUrgent).length} urgent</Typography>
      <Grid container spacing={2.5}>
        {notices.map((n) => (
          <Grid size={{ xs: 12, md: 6 }} key={n.noticeId}>
            <Card sx={{ borderRadius: 3, border: n.isUrgent ? "2px solid #C05F3C" : "1px solid #E6DCC9", bgcolor: "#FFFDF9", boxShadow: "0 4px 14px rgba(46,58,37,0.04)", overflow: "hidden" }}>
              {n.isUrgent && <Box sx={{ height: 4, bgcolor: "#C05F3C" }}/>}
              <CardContent sx={{ p: 2.5 }}>
                <Stack direction="row" spacing={1} sx={{ alignItems: "center", mb: 1 }}>
                  {n.isUrgent ? <Chip size="small" label="Urgent" color="error" sx={{ fontWeight: 700, height: 20 }}/> : <Box sx={{ width: 28, height: 28, borderRadius: 1.5, bgcolor: "#FAF5EC", border: "1px solid #E6DCC9", display: "grid", placeItems: "center", color: "#B08442" }}><CampaignIcon sx={{ fontSize: 16 }}/></Box>}
                  <Typography variant="subtitle1" sx={{ fontWeight: 800, color: "#2E3A25" }}>{n.title}</Typography>
                </Stack>
                <Typography variant="body2" sx={{ color: "#2E3A25", lineHeight: 1.6, bgcolor: n.isUrgent ? "#FFF1ED" : "#FAF5EC", p: 1.5, borderRadius: 2, border: `1px solid ${n.isUrgent ? "#F0C9B8" : "#F0EDD8"}` }}>{n.body}</Typography>
                <Stack direction="row" spacing={0.7} sx={{ alignItems: "center", mt: 1.2 }}><Box sx={{ width: 6, height: 6, borderRadius: "50%", bgcolor: n.isUrgent ? "#C05F3C" : "#B08442" }}/><Typography variant="caption" sx={{ color: "#6B7A5C", fontWeight: 600 }}>Published {new Date(n.publishedAt).toLocaleDateString()}</Typography></Stack>
              </CardContent>
            </Card>
          </Grid>
        ))}
        {notices.length === 0 && <Grid size={12}><Box sx={{ p: 5, textAlign: "center", bgcolor: "#FFFDF9", borderRadius: 3, border: "1px dashed #E6DCC9" }}><Typography sx={{ color: "#6B7A5C" }}>No active notices right now.</Typography></Box></Grid>}
      </Grid>
      <Snackbar open={!!toast} autoHideDuration={4000} onClose={() => setToast("")}><Alert severity="info" onClose={() => setToast("")} sx={{ bgcolor: "#2E3A25", color: "#fff", fontWeight: 600 }}>{toast}</Alert></Snackbar>
    </Box>
  );
}