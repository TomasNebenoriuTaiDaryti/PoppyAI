import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Container, Typography, Box, AppBar, Toolbar } from '@mui/material';


const MainPage = ({ onLogout, darkMode, toggleDarkMode }) => {
  const navigate = useNavigate();
  const handleLogout = () => {
    onLogout();
    navigate('/login');
  };
  return (
    <Container maxWidth="lg">
      <AppBar position="static" sx={{ mb: 4 }}>
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Poppy
          </Typography>
          <Button 
            color="inherit" 
            onClick={() => navigate('/quiz')}
            sx={{ fontWeight: 'bold', mx: 2 }}
          >
            Movie Quiz
          </Button>
          <Button 
            color="inherit" 
            onClick={() => navigate('/profile')}
            sx={{ fontWeight: 'bold', mx: 2 }}
          >
            View Profile
          </Button>
          <Button 
            color="inherit" 
            onClick={() => navigate('/search')}
            sx={{ fontWeight: 'bold', mx: 2 }}
          >
            Movie Search
          </Button>
          <Button 
            color="inherit" 
            onClick={handleLogout}
            sx={{ fontWeight: 'bold' }}
          >
            Logout
          </Button>
          <Button 
            color="inherit" 
            onClick={toggleDarkMode}
            sx={{ fontWeight: 'bold', mx: 2 }}
          >
            {darkMode ? '☀️ Light' : '🌙 Dark'}
          </Button>
        </Toolbar>
      </AppBar>

      <Box sx={{ textAlign: 'center', mt: 8 }}>
        <Typography variant="h3" gutterBottom sx={{ fontWeight: 'bold' }}>
          Welcome to PoppyAI
        </Typography>
        <Typography variant="h5" sx={{ mb: 4, color: 'text.secondary' }}>
          Discover your next favorite movie
        </Typography>

        <Box sx={{ display: 'flex', gap: 4, justifyContent: 'center', mt: 8 }}>
          <Button
            variant="contained"
            size="large"
            onClick={() => navigate('/quiz')}
            sx={{ 
              px: 6, 
              py: 3, 
              fontSize: '1.2rem',
              bgcolor: 'primary.main',
              '&:hover': { bgcolor: 'primary.dark' }
            }}
          >
            Start AI Movie Quiz
          </Button>
          
          <Button
            variant="contained"
            color="secondary"
            size="large"
            onClick={() => navigate('/search')}
            sx={{ 
              px: 6, 
              py: 3, 
              fontSize: '1.2rem',
              bgcolor: 'secondary.main',
              '&:hover': { bgcolor: 'secondary.dark' }
            }}
          >
            Search Movie Database
          </Button>
        </Box>
      </Box>
    </Container>
  );
};

export default MainPage;