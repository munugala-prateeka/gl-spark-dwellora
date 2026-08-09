import { useEffect, useState, useCallback } from "react";
import {
  Box, Typography, Card, CardContent, Stack, TextField, Button, Chip, Grid,
  Dialog, DialogTitle, DialogContent, DialogActions, FormControlLabel, Switch,
  IconButton, Snackbar, Alert,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import { noticeApi } from "../../api/communityApi";
import { useAuth } from "../../auth/AuthContext";
import type { NoticeRequest, NoticeResponse } from "../../api/types";

const emptyForm = (apartmentId: number): NoticeRequest => ({
  title: "", body: "", isUrgent: false, expiresAt: null,
});

export default function NoticesTab() {
  const { user } = useAuth();
  const apartmentId = user?.apartmentId ?? 0;
  const [notices, setNotices] = useState<NoticeResponse[]>([]);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState<NoticeRequest>(emptyForm(apartmentId));
  const [toast, setToast] = useState("");

  const load = useCallback(() => {
    if (!apartmentId) return;
    noticeApi.getActive().then(setNotices).catch(() => setToast("Could not load notices."));
  }, [apartmentId]);

  useEffect(load, [load]);

  const handlePublish = async () => {
    try {
      await noticeApi.publish(form);
      setToast("Notice published to all residents.");
      setOpen(false);
      setForm(emptyForm(apartmentId));
      load();
    } catch (err: any) {
      setToast(err?.response?.data?.details || "Could not publish notice.");
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await noticeApi.remove(id);
      setToast("Notice removed.");
      load();
    } catch {
      setToast("Could not remove notice.");
    }
  };

  return (
    <Box>
      <Stack direction="row" sx={{ justifyContent: "space-between", alignItems: "center", mb: 2 }}>
        <Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25" }}>Notices</Typography>
        <Button startIcon={<AddIcon />} variant="contained" onClick={() => setOpen(true)} sx={{ bgcolor: "#C05F3C", "&:hover": { bgcolor: "#A24A2C" }, fontWeight: 700, borderRadius: 2 }}>
          Publish notice
        </Button>
      </Stack>

      <Grid container spacing={2.5}>
        {notices.map((n) => (
          <Grid size={{ xs: 12, md: 6 }} key={n.noticeId}>
            <Card sx={{ borderRadius: 3, border: n.isUrgent ? "2px solid #C05F3C" : "1px solid #E6DCC9", bgcolor: "#FFFDF9", boxShadow: "0 4px 14px rgba(46,58,37,0.04)" }}>
              <CardContent>
                <Stack direction="row" sx={{ justifyContent: "space-between", alignItems: "flex-start" }}>
                  <Stack direction="row" spacing={1} sx={{ alignItems: "center" }}>
                    {n.isUrgent && <Chip size="small" label="Urgent" color="error" sx={{ fontWeight: 700 }} />}
                    <Typography variant="subtitle1" sx={{ fontWeight: 800, color: "#2E3A25" }}>
                      {n.title}
                    </Typography>
                  </Stack>
                  <IconButton size="small" sx={{ color: "#C05F3C", "&:hover": { bgcolor: "rgba(192,95,60,0.08)" } }} onClick={() => handleDelete(n.noticeId)}>
                    <DeleteIcon fontSize="small" />
                  </IconButton>
                </Stack>
                <Typography variant="body2" sx={{ mt: 1, color: "#2E3A25" }}>{n.body}</Typography>
                <Typography variant="caption" sx={{ color: "#6B7A5C" }}>
                  <Stack direction="row" spacing={0.7} sx={{ alignItems: "center", mt: 1 }}>
                    <Box sx={{ width: 6, height: 6, borderRadius: "50%", bgcolor: n.isUrgent ? "#C05F3C" : "#B08442" }} />
                    <Typography variant="caption" sx={{ color: "#6B7A5C", fontWeight: 600 }}>
                      Published {new Date(n.publishedAt).toLocaleDateString()}
                    </Typography>
                  </Stack>
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
        {notices.length === 0 && (
          <Grid size={12}><Typography sx={{ color: "#6B7A5C" }}>No active notices.</Typography></Grid>
        )}
      </Grid>

      <Dialog
  open={open}
  onClose={() => setOpen(false)}
  maxWidth="sm"
  fullWidth
  slotProps={{
    paper: {
      sx: { borderRadius: 3 },
    },
  }}
>
        <DialogTitle sx={{ fontWeight: 800, color: "#2E3A25" }}>Publish notice</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ mt: 1 }}>
            <TextField label="Title" fullWidth value={form.title} onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))} />
            <TextField label="Body" fullWidth multiline rows={3} value={form.body} onChange={(e) => setForm((f) => ({ ...f, body: e.target.value }))} />
            <FormControlLabel
              control={<Switch checked={form.isUrgent} onChange={(e) => setForm((f) => ({ ...f, isUrgent: e.target.checked }))} />}
              label="Mark as urgent"
            />
          </Stack>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setOpen(false)} sx={{ color: "#6B7A5C", fontWeight: 600 }}>Cancel</Button>
          <Button variant="contained" onClick={handlePublish} sx={{ bgcolor: "#C05F3C", "&:hover": { bgcolor: "#A24A2C" }, fontWeight: 700 }}>
            Publish
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar open={!!toast} autoHideDuration={4000} onClose={() => setToast("")}>
        <Alert severity="info" onClose={() => setToast("")} sx={{ bgcolor: "#2E3A25", color: "#fff", fontWeight: 600 }}>
          {toast}
        </Alert>
      </Snackbar>
    </Box>
  );
}