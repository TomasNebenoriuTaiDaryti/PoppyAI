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
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <AppBar position="static">
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

      <Box sx={{
        flexGrow: 1,
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        py: 8,
        px: 2
      }}>
        <Container maxWidth="md">
          <Typography variant="h2" gutterBottom sx={{ 
            fontWeight: 'bold', 
            textAlign: 'center',
            mb: 3
          }}>
            Welcome to PoppyAI
          </Typography>
          <Typography variant="h5" sx={{ 
            mb: 6, 
            color: 'text.secondary',
            textAlign: 'center'
          }}>
            Discover your next favorite movie
          </Typography>

          <Box sx={{ 
            display: 'flex', 
            gap: 4, 
            justifyContent: 'center',
            flexWrap: 'wrap',
            mt: 4
          }}>
            <Button
              variant="contained"
              size="large"
              onClick={() => navigate('/quiz')}
              sx={{ 
                minWidth: 280,
                py: 2.5, 
                fontSize: '1.25rem',
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
                minWidth: 280,
                py: 2.5, 
                fontSize: '1.25rem',
                bgcolor: 'secondary.main',
                '&:hover': { bgcolor: 'secondary.dark' }
              }}
            >
              Search Movie Database
            </Button>
          </Box>
        </Container>
      </Box>
    </Box>
  );
};

export default MainPage;