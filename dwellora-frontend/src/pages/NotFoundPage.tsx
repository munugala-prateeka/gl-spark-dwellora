import { Box, Typography, Button } from "@mui/material";
import { Link } from "react-router-dom";

export default function NotFoundPage() {
  return (
    <Box sx={{ minHeight: "100vh", display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", bgcolor: "#232A3B", color: "#fff" }}>
      <Typography variant="h3" gutterBottom>404</Typography>
      <Typography sx={{ mb: 3 }}>This page doesn't exist.</Typography>
      <Button component={Link} to="/login" variant="contained" color="secondary">Back to login</Button>
    </Box>
  );
}
