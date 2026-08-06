import { type ReactNode, useState, useEffect, useCallback } from "react";
import {
  AppBar, Toolbar, Typography, Box, Drawer, List, ListItemButton,
  ListItemIcon, ListItemText, IconButton, Badge, Avatar, Menu, MenuItem,
  Divider,
} from "@mui/material";
import ApartmentIcon from "@mui/icons-material/Apartment";
import NotificationsIcon from "@mui/icons-material/Notifications";
import LogoutIcon from "@mui/icons-material/Logout";
import PersonIcon from "@mui/icons-material/Person";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { notificationApi } from "../api/communityApi";
import { apartmentApi } from "../api/onboardingApi";
import type { NotificationResponse } from "../api/types";

const DRAWER_WIDTH = 260;
const CREAM = "#FAF5EC";
const PAPER = "#FFFDF9";
const MOSS = "#2E3A25";
const MOSS_LIGHT = "#6B7A5C";
const TERRA = "#C05F3C";
const TERRA_DARK = "#A24A2C";
const GOLD = "#B08442";
const OLIVE = "#E9EBDD";
const OLIVE_BORDER = "#DDE0CB";

export interface NavItem {
  label: string;
  path: string;
  icon: ReactNode;
}

export default function AppShell({
  children, navItems, title,
}: {
  children: ReactNode; navItems: NavItem[]; title: string;
}) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [notifAnchor, setNotifAnchor] = useState<null | HTMLElement>(null);
  const [notifications, setNotifications] = useState<NotificationResponse[]>([]);
  const [apartmentName, setApartmentName] = useState("");

  useEffect(() => {
    if (!user?.apartmentId) {
      setApartmentName("");
      return;
    }
    apartmentApi.getById(user.apartmentId)
      .then((a) => setApartmentName(a.apartmentName))
      .catch(() => setApartmentName(""));
  }, [user?.apartmentId]);

  const loadNotifications = useCallback(() => {
    if (!user) return;
    notificationApi.getByUser(user.userId).then(setNotifications).catch(() => setNotifications([]));
  }, [user]);

  useEffect(() => {
    loadNotifications();
    const interval = setInterval(loadNotifications, 20000);
    return () => clearInterval(interval);
  }, [loadNotifications]);

  const unreadCount = notifications.filter((n) => !n.read).length;

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const handleProfile = () => {
    setAnchorEl(null);
    navigate("/profile");
  };

  const handleNotifOpen = (e: React.MouseEvent<HTMLElement>) => {
    setNotifAnchor(e.currentTarget);
    loadNotifications();
  };

  const handleMarkRead = (id: number) => {
    notificationApi.markRead(id).then(loadNotifications);
  };

  return (
    <Box sx={{ display: "flex", minHeight: "100vh", bgcolor: CREAM }}>
      <AppBar
        position="fixed"
        sx={{
          zIndex: (t) => t.zIndex.drawer + 1,
          bgcolor: PAPER,
          borderBottom: `1px solid ${OLIVE_BORDER}`,
          boxShadow: "0 4px 20px rgba(46,58,37,0.06)",
          color: MOSS,
        }}
      >
        <Toolbar sx={{ gap: 2, py: 0.5 }}>
          <Box sx={{ width: 38, height: 38, borderRadius: 2.5, bgcolor: TERRA, display: "grid", placeItems: "center", color: "#fff", boxShadow: "0 4px 12px rgba(192,95,60,0.25)" }}>
            <ApartmentIcon sx={{ fontSize: 20 }} />
          </Box>
          <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 800, letterSpacing: 0.2, color: MOSS }}>
            Dwellora
            <Typography component="span" sx={{ opacity: 0.5, ml: 1.2, fontWeight: 600, fontSize: 13 }} variant="body2">
              {title}{apartmentName ? ` · ${apartmentName}` : ""}
            </Typography>
          </Typography>

          <IconButton onClick={handleNotifOpen} sx={{ color: MOSS, bgcolor: OLIVE, width: 42, height: 42, border: `1px solid ${OLIVE_BORDER}`, "&:hover": { bgcolor: "#E1E4D2" } }}>
            <Badge badgeContent={unreadCount} sx={{ "& .MuiBadge-badge": { bgcolor: TERRA, color: "#fff", fontWeight: 800, fontSize: 11, minWidth: 18, height: 18 } }}>
              <NotificationsIcon sx={{ fontSize: 20 }} />
            </Badge>
          </IconButton>
          <Menu
            anchorEl={notifAnchor}
            open={!!notifAnchor}
            onClose={() => setNotifAnchor(null)}
            slotProps={{
              paper: { sx: { width: 360, maxHeight: 440, borderRadius: 3, border: `1px solid ${OLIVE_BORDER}`, boxShadow: "0 20px 48px rgba(46,58,37,0.16)", overflow: "hidden", display: "flex", flexDirection: "column" } },
              list: { sx: { py: 0, overflowY: "auto", maxHeight: 440, "&::-webkit-scrollbar": { width: 6 }, "&::-webkit-scrollbar-thumb": { bgcolor: OLIVE_BORDER, borderRadius: 3 } } }
            }}
          >
            {notifications.length === 0 && (
              <MenuItem disabled sx={{ color: MOSS_LIGHT, py: 2, justifyContent: "center" }}>No notifications yet</MenuItem>
            )}
            {notifications.map((n) => (
              <MenuItem
                key={n.notificationId}
                onClick={() => handleMarkRead(n.notificationId)}
                sx={{
                  whiteSpace: "normal",
                  alignItems: "flex-start",
                  py: 1.8,
                  px: 2.2,
                  bgcolor: n.read ? "transparent" : "rgba(176,132,66,0.08)",
                  borderBottom: `1px solid ${OLIVE_BORDER}`,
                  "&:hover": { bgcolor: n.read ? CREAM : "rgba(176,132,66,0.14)" }
                }}
              >
                <Box>
                  <Typography variant="subtitle2" sx={{ color: MOSS, fontWeight: 800, lineHeight: 1.3 }}>{n.title}</Typography>
                  <Typography variant="body2" sx={{ color: MOSS_LIGHT, mt: 0.4, lineHeight: 1.5 }}>{n.message}</Typography>
                </Box>
              </MenuItem>
            ))}
          </Menu>

          <IconButton onClick={(e) => setAnchorEl(e.currentTarget)} sx={{ ml: 0.5, p: 0.4 }}>
            <Avatar sx={{ width: 40, height: 40, bgcolor: TERRA, color: "#fff", fontWeight: 800, fontSize: 14, border: `2px solid ${OLIVE_BORDER}`, boxShadow: "0 2px 10px rgba(192,95,60,0.2)" }}>
              {user?.fullName?.charAt(0) ?? "?"}
            </Avatar>
          </IconButton>
          <Menu anchorEl={anchorEl} open={!!anchorEl} onClose={() => setAnchorEl(null)} slotProps={{ paper: { sx: { borderRadius: 3, border: `1px solid ${OLIVE_BORDER}`, minWidth: 210, overflow: "hidden", boxShadow: "0 20px 48px rgba(46,58,37,0.16)" } } }}>
            <Box sx={{ px: 2.2, py: 1.8, bgcolor: CREAM }}>
              <Typography variant="subtitle2" sx={{ fontWeight: 800, color: MOSS, lineHeight: 1.2 }}>{user?.fullName}</Typography>
              <Typography variant="caption" sx={{ color: GOLD, fontWeight: 800, letterSpacing: 0.6, textTransform: "uppercase", fontSize: 11 }}>
                {user?.role.replace("_", " ")}
              </Typography>
            </Box>
            <Divider sx={{ borderColor: OLIVE_BORDER }} />
            <MenuItem onClick={handleProfile} sx={{ color: MOSS, fontWeight: 600, py: 1.4 }}>
              <PersonIcon fontSize="small" sx={{ mr: 1.5, color: GOLD }} /> My profile
            </MenuItem>
            <MenuItem onClick={handleLogout} sx={{ color: MOSS, fontWeight: 600, py: 1.4 }}>
              <LogoutIcon fontSize="small" sx={{ mr: 1.5, color: TERRA }} /> Log out
            </MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>

      <Drawer
        variant="permanent"
        sx={{
          width: DRAWER_WIDTH,
          flexShrink: 0,
          "& .MuiDrawer-paper": {
            width: DRAWER_WIDTH,
            boxSizing: "border-box",
            bgcolor: OLIVE,
            color: MOSS,
            borderRight: `1px solid ${OLIVE_BORDER}`,
          },
        }}
      >
        <Toolbar />
        <Box sx={{ px: 2.5, pt: 2.5, pb: 1.5 }}>
          <Typography variant="caption" sx={{ color: GOLD, fontWeight: 800, letterSpacing: 1.2, fontSize: 11 }}>
            NAVIGATION
          </Typography>
        </Box>
        <List sx={{ px: 1.5, pt: 0.5 }}>
          {navItems.map((item) => {
            const active = location.pathname === item.path;
            return (
              <ListItemButton
                key={item.path}
                selected={active}
                onClick={() => navigate(item.path)}
                sx={{
                  borderRadius: 2.5,
                  mb: 0.8,
                  py: 1.4,
                  px: 1.8,
                  color: active ? "#fff" : MOSS_LIGHT,
                  bgcolor: active ? TERRA : "transparent",
                  boxShadow: active ? "0 8px 20px rgba(192,95,60,0.28)" : "none",
                  border: active ? "none" : `1px solid transparent`,
                  "&:hover": { bgcolor: active ? TERRA_DARK : PAPER, color: active ? "#fff" : MOSS, borderColor: active ? "transparent" : OLIVE_BORDER, boxShadow: active ? "0 8px 20px rgba(192,95,60,0.28)" : "0 2px 10px rgba(46,58,37,0.04)" },
                  "&.Mui-selected": { bgcolor: TERRA, color: "#fff" },
                  "&.Mui-selected:hover": { bgcolor: TERRA_DARK },
                }}
              >
                <ListItemIcon sx={{ color: "inherit", minWidth: 36 }}>{item.icon}</ListItemIcon>
                <ListItemText primary={item.label} sx={{ "& .MuiTypography-root": { fontWeight: 700, fontSize: 14 } }} />
              </ListItemButton>
            );
          })}
        </List>
      </Drawer>

      <Box component="main" sx={{ flexGrow: 1, bgcolor: CREAM, minHeight: "100vh" }}>
        <Toolbar />
        <Box sx={{ p: { xs: 2, md: 4 } }}>{children}</Box>
      </Box>
    </Box>
  );
}