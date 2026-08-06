import { useState } from "react";
import {
  Box,
  alpha,
  Paper,
  TextField,
  Button,
  Typography,
  Alert,
  Stack,
  Grid,
  Link as MLink,
} from "@mui/material";
import ApartmentIcon from "@mui/icons-material/Apartment";
import { Link } from "react-router-dom";
import { onboardingApi } from "../api/onboardingApi";
import type { OnboardingRequest } from "../api/types";

const CREAM = "#FAF5EC";
const PAPER = "#FFFDF9";
const MOSS = "#2E3A25";
const MOSS_LIGHT = "#6B7A5C";
const TERRA = "#C05F3C";
const TERRA_DARK = "#A24A2C";
const GOLD = "#B08442";

const empty: OnboardingRequest = {
  apartmentName: "",
  address: "",
  city: "",
  state: "",
  pincode: "",
  totalBlocks: 1,
  totalUnits: 1,
  managerName: "",
  managerEmail: "",
  managerPhone: "",
};

export default function OnboardingRequestPage() {
  const [form, setForm] = useState<OnboardingRequest>(empty);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const set = (field: keyof OnboardingRequest, value: string | number) =>
    setForm((f) => ({ ...f, [field]: value }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await onboardingApi.submitRequest(form);
      setSuccess(true);
    } catch (err: any) {
      setError(err?.response?.data?.details || "Could not submit your request. Please check the form and try again.");
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <Box sx={{ minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center", bgcolor: CREAM, px: 2 }}>
        <Paper sx={{ p: 5, maxWidth: 480, width: "100%", bgcolor: PAPER, borderRadius: 3, border: `1px solid #E6DCC9`, boxShadow: "0 10px 32px rgba(46,58,37,0.06)" }}>
          <Stack spacing={1} sx={{ alignItems: "center", textAlign: "center" }}>
            <ApartmentIcon sx={{ fontSize: 48, color: TERRA }} />
            <Typography variant="h5" sx={{ fontWeight: 800, color: MOSS }}>
              Request received
            </Typography>
            <Typography sx={{ color: MOSS_LIGHT }}>
              Thanks — our team will review your community's details shortly. You'll get an email with an activation link once it's approved.
            </Typography>
            <Button component={Link} to="/login" variant="contained" sx={{ mt: 2, bgcolor: TERRA, "&:hover": { bgcolor: TERRA_DARK } }}>
              Back to login
            </Button>
          </Stack>
        </Paper>
      </Box>
    );
  }

  return (
    <Box sx={{ minHeight: "100vh", bgcolor: CREAM, py: 6, px: 2, display: "flex", justifyContent: "center" }}>
      <Paper sx={{ p: { xs: 3, sm: 5 }, maxWidth: 640, width: "100%", borderRadius: 3, bgcolor: PAPER, border: `1px solid #E6DCC9` }}>
        <Stack spacing={1} sx={{ alignItems: "center", mb: 3 }}>
          <ApartmentIcon sx={{ fontSize: 44, color: TERRA }} />
          <Typography variant="h5" sx={{ fontWeight: 800, color: MOSS }}>
            Bring your community to Dwellora
          </Typography>
          <Typography variant="body2" sx={{ color: MOSS_LIGHT, textAlign: "center", maxWidth: 480 }}>
            Tell us about your apartment community and yourself. Our team reviews every request before your manager account is created.
          </Typography>
        </Stack>

        {error && <Alert severity="error" sx={{ mb: 2, borderRadius: 2 }}>{error}</Alert>}

        <form onSubmit={handleSubmit}>
          <Grid container spacing={2.5}>
            <Grid size={12}>
              <Typography variant="subtitle2" sx={{ color: MOSS_LIGHT, fontWeight: 700 }}>
                Community details
              </Typography>
            </Grid>
            <Grid size={12}>
              <TextField
                label="Apartment / society name"
                fullWidth
                required
                value={form.apartmentName}
                onChange={(e) => set("apartmentName", e.target.value)}
                sx={{
                  "& label": { color: MOSS_LIGHT },
                  "& label.Mui-focused": { color: TERRA },
                  "& .MuiOutlinedInput-root": {
                    "&.Mui-focused fieldset": { borderColor: GOLD },
                  },
                }}
              />
            </Grid>
            <Grid size={12}>
              <TextField
                label="Address"
                fullWidth
                required
                value={form.address}
                onChange={(e) => set("address", e.target.value)}
                sx={{
                  "& label": { color: MOSS_LIGHT },
                  "& label.Mui-focused": { color: TERRA },
                  "& .MuiOutlinedInput-root": {
                    "&.Mui-focused fieldset": { borderColor: GOLD },
                  },
                }}
              />
            </Grid>
            <Grid size={4}>
              <TextField
                label="City"
                fullWidth
                required
                value={form.city}
                onChange={(e) => set("city", e.target.value)}
                sx={{
                  "& label": { color: MOSS_LIGHT },
                  "& label.Mui-focused": { color: TERRA },
                  "& .MuiOutlinedInput-root": {
                    "&.Mui-focused fieldset": { borderColor: GOLD },
                  },
                }}
              />
            </Grid>
            <Grid size={4}>
              <TextField
                label="State"
                fullWidth
                required
                value={form.state}
                onChange={(e) => set("state", e.target.value)}
                sx={{
                  "& label": { color: MOSS_LIGHT },
                  "& label.Mui-focused": { color: TERRA },
                  "& .MuiOutlinedInput-root": {
                    "&.Mui-focused fieldset": { borderColor: GOLD },
                  },
                }}
              />
            </Grid>
            <Grid size={4}>
              <TextField
                label="Pincode"
                fullWidth
                required
                value={form.pincode}
                onChange={(e) => set("pincode", e.target.value)}
                sx={{
                  "& label": { color: MOSS_LIGHT },
                  "& label.Mui-focused": { color: TERRA },
                  "& .MuiOutlinedInput-root": {
                    "&.Mui-focused fieldset": { borderColor: GOLD },
                  },
                }}
              />
            </Grid>
            <Grid size={6}>
              <TextField
                label="Total blocks"
                type="number"
                fullWidth
                required
                value={form.totalBlocks}
                onChange={(e) => set("totalBlocks", Number(e.target.value))}
                sx={{
                  "& label": { color: MOSS_LIGHT },
                  "& label.Mui-focused": { color: TERRA },
                  "& .MuiOutlinedInput-root": {
                    "&.Mui-focused fieldset": { borderColor: GOLD },
                  },
                }}
              />
            </Grid>
            <Grid size={6}>
              <TextField
                label="Total units"
                type="number"
                fullWidth
                required
                value={form.totalUnits}
                onChange={(e) => set("totalUnits", Number(e.target.value))}
                sx={{
                  "& label": { color: MOSS_LIGHT },
                  "& label.Mui-focused": { color: TERRA },
                  "& .MuiOutlinedInput-root": {
                    "&.Mui-focused fieldset": { borderColor: GOLD },
                  },
                }}
              />
            </Grid>

            <Grid size={12} sx={{ mt: 1 }}>
              <Typography variant="subtitle2" sx={{ color: MOSS_LIGHT, fontWeight: 700 }}>
                Your details (manager)
              </Typography>
            </Grid>
            <Grid size={12}>
              <TextField
                label="Full name"
                fullWidth
                required
                value={form.managerName}
                onChange={(e) => set("managerName", e.target.value)}
                sx={{
                  "& label": { color: MOSS_LIGHT },
                  "& label.Mui-focused": { color: TERRA },
                  "& .MuiOutlinedInput-root": {
                    "&.Mui-focused fieldset": { borderColor: GOLD },
                  },
                }}
              />
            </Grid>
            <Grid size={6}>
              <TextField
                label="Email"
                type="email"
                fullWidth
                required
                value={form.managerEmail}
                onChange={(e) => set("managerEmail", e.target.value)}
                sx={{
                  "& label": { color: MOSS_LIGHT },
                  "& label.Mui-focused": { color: TERRA },
                  "& .MuiOutlinedInput-root": {
                    "&.Mui-focused fieldset": { borderColor: GOLD },
                  },
                }}
              />
            </Grid>
            <Grid size={6}>
              <TextField
                label="Phone"
                fullWidth
                required
                value={form.managerPhone}
                onChange={(e) => set("managerPhone", e.target.value)}
                sx={{
                  "& label": { color: MOSS_LIGHT },
                  "& label.Mui-focused": { color: TERRA },
                  "& .MuiOutlinedInput-root": {
                    "&.Mui-focused fieldset": { borderColor: GOLD },
                  },
                }}
              />
            </Grid>

            <Grid size={12}>
              <Button
                type="submit"
                variant="contained"
                size="large"
                fullWidth
                disabled={loading}
                sx={{
                  mt: 1,
                  py: 1.5,
                  fontWeight: 800,
                  borderRadius: 2.5,
                  bgcolor: TERRA,
                  boxShadow: `0 8px 20px ${alpha(TERRA, 0.22)}`,
                  "&:hover": { bgcolor: TERRA_DARK },
                }}
              >
                {loading ? "Submitting..." : "Submit request"}
              </Button>
            </Grid>
          </Grid>
        </form>

        <Typography variant="body2" sx={{ textAlign: "center", mt: 3, color: MOSS_LIGHT }}>
          Already onboarded?{" "}
          <MLink component={Link} to="/login" sx={{ color: GOLD, fontWeight: 700, "&:hover": { color: GOLD } }}>
            Sign in
          </MLink>
        </Typography>
      </Paper>
    </Box>
  );
}