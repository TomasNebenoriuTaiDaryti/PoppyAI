import { Container, Typography, Button } from '@mui/material';

const MainPage = ({ onLogout }) => {
  return (
    <Container maxWidth="md" sx={{ mt: 8 }}>
      <Typography variant="h4" gutterBottom>
        Welcome to PoppyAI
      </Typography>
      <Button 
        variant="contained" 
        color="error"
        onClick={onLogout}
      >
        Logout
      </Button>
    </Container>
  );
};

export default MainPage;