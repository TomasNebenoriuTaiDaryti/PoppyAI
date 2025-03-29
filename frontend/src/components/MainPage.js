import React from 'react';
import { Button, Container, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const MainPage = ({ onLogout }) => {
    const navigate = useNavigate();

    const handleLogout = () => {
        onLogout();
        navigate('/login');
    };

    return (
        <Container>
            <Typography variant="h4" gutterBottom>
                Welcome to PoppyAI App
            </Typography>
            <Button 
                variant="contained" 
                onClick={() => navigate('/search')}
                sx={{ m: 1 }}
            >
                Search Movies
            </Button>
            <Button 
                variant="contained" 
                onClick={() => navigate('/profile')}
                sx={{ m: 1 }}
            >
                View Profile
            </Button>
            <Button 
                variant="contained" 
                onClick={handleLogout}
                sx={{ m: 1 }}
            >
                Logout
            </Button>
        </Container>
    );
};

export default MainPage;