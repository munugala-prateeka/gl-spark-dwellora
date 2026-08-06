import { useEffect, useState } from "react";
import {
  Box, Typography, Card, CardContent, Grid, Chip, Avatar, Stack, Divider, Snackbar, Alert,
} from "@mui/material";
import ApartmentIcon from "@mui/icons-material/Apartment";
import EmailIcon from "@mui/icons-material/Email";
import PhoneIcon from "@mui/icons-material/Phone";
import HomeIcon from "@mui/icons-material/Home";
import BadgeIcon from "@mui/icons-material/Badge";
import ShieldIcon from "@mui/icons-material/VerifiedUser";
import AppShell from "../components/AppShell";
import { userApi } from "../api/userApi";
import { apartmentApi } from "../api/onboardingApi";
import { useAuth } from "../auth/AuthContext";
import type { UserResponse, ApartmentResponse } from "../api/types";
import { statusColor } from "../theme/theme";

function InfoRow({ icon, label, value }: { icon: React.ReactNode; label: string; value: string | number | null | undefined }) {
  return (
    <Stack direction="row" spacing={1.5} sx={{ alignItems: "center", p: 1.8, bgcolor: "#FAF5EC", borderRadius: 2.5, border: "1px solid #F0EDD8" }}>
      <Box sx={{ width: 38, height: 38, borderRadius: 2, bgcolor: "#FFFDF9", border: "1px solid #E6DCC9", display: "grid", placeItems: "center", color: "#B08442", flexShrink: 0 }}>
        {icon}
      </Box>
      <Box sx={{ minWidth: 0 }}>
        <Typography variant="caption" sx={{ color: "#B08442", fontWeight: 800, letterSpacing: 0.6, fontSize: 10 }}>{label.toUpperCase()}</Typography>
        <Typography variant="body2" sx={{ color: "#2E3A25", fontWeight: 700, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>{value ?? "—"}</Typography>
      </Box>
    </Stack>
  );
}

export default function ProfilePage() {
  const { user } = useAuth();
  const [profile, setProfile] = useState<UserResponse | null>(null);
  const [apartment, setApartment] = useState<ApartmentResponse | null>(null);
  const [toast, setToast] = useState("");

  useEffect(() => {
    if (!user) return;
    userApi.getById(user.userId).then(setProfile).catch(() => setToast("Could not load your profile."));
  }, [user]);

  useEffect(() => {
    if (!profile?.apartmentId) return;
    apartmentApi.getById(profile.apartmentId).then(setApartment).catch(() => {});
  }, [profile]);

  const navItems = [{ label: "Back to dashboard", path: user?.role === "MANAGER" ? "/manager" : user?.role === "RESIDENT" ? "/resident" : "/admin", icon: <ApartmentIcon /> }];

  return (
    <AppShell title="My Profile" navItems={navItems}>
      <Box sx={{ maxWidth: 820 }}>
        <Typography variant="h5" sx={{ fontWeight: 800, color: "#2E3A25" }}>My profile</Typography>
        <Typography sx={{ color: "#6B7A5C", mb: 3 }}>Your account and community details</Typography>

        <Grid container spacing={3}>
          <Grid size={{ xs: 12, md: 6 }}>
            <Card sx={{ borderRadius: 3, border: "1px solid #E6DCC9", bgcolor: "#FFFDF9", overflow: "hidden", boxShadow: "0 4px 16px rgba(46,58,37,0.05)" }}>
              <Box sx={{ height: 5, bgcolor: "#C05F3C" }} />
              <CardContent sx={{ p: 3 }}>
                <Stack direction="row" spacing={2} sx={{ alignItems: "center", mb: 2.5 }}>
                  <Avatar sx={{ width: 60, height: 60, bgcolor: "#C05F3C", color: "#fff", fontSize: 24, fontWeight: 800, border: "2px solid #E6DCC9" }}>
                    {profile?.fullName?.charAt(0) ?? "?"}
                  </Avatar>
                  <Box sx={{ minWidth: 0 }}>
                    <Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25", lineHeight: 1.2 }}>{profile?.fullName ?? "—"}</Typography>
                    <Stack direction="row" spacing={1} sx={{ mt: 0.8, flexWrap: "wrap" }}>
                      <Chip size="small" label={profile?.role?.replace("_", " ") ?? "—"} sx={{ bgcolor: "#E9EBDD", color: "#2E3A25", fontWeight: 700, height: 22, border: "1px solid #DDE0CB" }} />
                      {profile && <Chip size="small" label={profile.accountStatus} color={statusColor[profile.accountStatus] as any} sx={{ fontWeight: 700, height: 22 }} />}
                    </Stack>
                  </Box>
                </Stack>
                <Divider sx={{ borderColor: "#E6DCC9", mb: 2.5 }} />
                <Stack spacing={1.5}>
                  <InfoRow icon={<EmailIcon sx={{ fontSize: 18 }} />} label="Email" value={profile?.email} />
                  <InfoRow icon={<PhoneIcon sx={{ fontSize: 18 }} />} label="Phone" value={profile?.phone} />
                  {profile?.flatNumber && <InfoRow icon={<HomeIcon sx={{ fontSize: 18 }} />} label="Flat / unit" value={profile.flatNumber} />}
                  <Stack direction="row" spacing={1.5} sx={{ alignItems: "center", p: 1.8, bgcolor: "#E9EBDD", borderRadius: 2.5, border: "1px solid #DDE0CB" }}>
                    <Box sx={{ width: 38, height: 38, borderRadius: 2, bgcolor: "#FFFDF9", display: "grid", placeItems: "center", color: "#6B8F52" }}><ShieldIcon sx={{ fontSize: 18 }} /></Box>
                    <Box><Typography variant="caption" sx={{ color: "#6B7A5C", fontWeight: 800, fontSize: 10, letterSpacing: 0.6 }}>ACCOUNT STATUS</Typography><Box sx={{ mt: 0.3 }}>{profile && <Chip size="small" label={profile.accountStatus} color={statusColor[profile.accountStatus] as any} sx={{ fontWeight: 700, height: 22 }} />}</Box></Box>
                  </Stack>
                </Stack>
              </CardContent>
            </Card>
          </Grid>

          {apartment && (
            <Grid size={{ xs: 12, md: 6 }}>
              <Card sx={{ borderRadius: 3, border: "1px solid #E6DCC9", bgcolor: "#FFFDF9", overflow: "hidden", boxShadow: "0 4px 16px rgba(46,58,37,0.05)" }}>
                <Box sx={{ height: 5, bgcolor: "#B08442" }} />
                <CardContent sx={{ p: 3 }}>
                  <Stack direction="row" spacing={1.5} sx={{ alignItems: "center", mb: 2.5 }}>
                    <Box sx={{ width: 42, height: 42, borderRadius: 2, bgcolor: "#FAF5EC", border: "1px solid #E6DCC9", display: "grid", placeItems: "center", color: "#C05F3C" }}><ApartmentIcon /></Box>
                    <Box>
                      <Typography variant="h6" sx={{ fontWeight: 800, color: "#2E3A25", lineHeight: 1.2 }}>{apartment.apartmentName}</Typography>
                      <Typography variant="caption" sx={{ color: "#B08442", fontWeight: 700, letterSpacing: 0.5 }}>{apartment.status}</Typography>
                    </Box>
                  </Stack>
                  <Divider sx={{ borderColor: "#E6DCC9", mb: 2.5 }} />
                  <Stack spacing={1.5}>
                    <InfoRow icon={<HomeIcon sx={{ fontSize: 18 }} />} label="Address" value={apartment.address} />
                    <Grid container spacing={1.5}>
                      <Grid size={6}><InfoRow icon={<BadgeIcon sx={{ fontSize: 16 }} />} label="City" value={apartment.city} /></Grid>
                      <Grid size={6}><InfoRow icon={<BadgeIcon sx={{ fontSize: 16 }} />} label="State" value={apartment.state} /></Grid>
                      <Grid size={6}><InfoRow icon={<BadgeIcon sx={{ fontSize: 16 }} />} label="Pincode" value={apartment.pincode} /></Grid>
                      <Grid size={6}><InfoRow icon={<ShieldIcon sx={{ fontSize: 16 }} />} label="Status" value={apartment.status} /></Grid>
                      <Grid size={6}><InfoRow icon={<ApartmentIcon sx={{ fontSize: 16 }} />} label="Total blocks" value={apartment.totalBlocks} /></Grid>
                      <Grid size={6}><InfoRow icon={<ApartmentIcon sx={{ fontSize: 16 }} />} label="Total units" value={apartment.totalUnits} /></Grid>
                    </Grid>
                  </Stack>
                </CardContent>
              </Card>
            </Grid>
          )}

          {!apartment && profile && !profile.apartmentId && (
            <Grid size={{ xs: 12, md: 6 }}>
              <Card sx={{ borderRadius: 3, border: "1px dashed #E6DCC9", bgcolor: "#FFFDF9", p: 3, textAlign: "center" }}>
                <Typography sx={{ color: "#6B7A5C", fontWeight: 500 }}>Platform admin accounts aren't scoped to a specific apartment community.</Typography>
              </Card>
            </Grid>
          )}
        </Grid>
      </Box>

      <Snackbar open={!!toast} autoHideDuration={4000} onClose={() => setToast("")}>
        <Alert severity="info" onClose={() => setToast("")} sx={{ bgcolor: "#2E3A25", color: "#fff", fontWeight: 600 }}>{toast}</Alert>
      </Snackbar>
    </AppShell>
  );
}