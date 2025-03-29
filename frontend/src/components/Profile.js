import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Typography, List, ListItem, ListItemText, Button, CircularProgress, IconButton } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import api from '../api/api';

const Profile = ({ onLogout }) => {
    const [watchlist, setWatchlist] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    const username = localStorage.getItem('username');

    useEffect(() => {
        if (!username) {
            navigate('/login');
            return;
        }
        
        api.get(`/watchlist/${username}`)
        .then(response => {
            setWatchlist(response.data);
            setLoading(false);
        })
        .catch(error => {
            console.error('Error:', error);
            setLoading(false);
        });
    }, [username, navigate]);

    const handleLogout = () => {
        localStorage.removeItem('isAuthenticated');
        localStorage.removeItem('username');
        onLogout();
        navigate('/login');
    };
    const handleRemoveMovie = (movieId) => {
        api.delete(`/watchlist/${username}/${movieId}`)
            .then(() => {
                setWatchlist(prev => prev.filter(m => m.id !== movieId));
            })
            .catch(error => {
                console.error('Delete error:', error);
            });
    };

    if (loading) {
        return (
            <Container sx={{ display: 'flex', justifyContent: 'center', mt: 10 }}>
                <CircularProgress />
            </Container>
        );
    }

    return (
        <Container>
            <Typography variant="h4" gutterBottom>
                Welcome, {username}
            </Typography>
            <Button variant="contained" onClick={handleLogout} sx={{ mb: 2 }}>
                Logout
            </Button>
            <Typography variant="h5" gutterBottom>
                Your Watchlist
            </Typography>
            <List>
                {watchlist.map(movie => (
                    <ListItem key={movie.id} sx={{ backgroundColor: 'background.paper' }}>
                        <ListItemText
                            primary={`${movie.title}`}
                            secondary={
                                <>
                                    <Typography variant="body2">
                                        Genre: {movie.genre} 
                                    </Typography>
                                    <Typography variant="body2">
                                        Rating: {movie.rating}
                                    </Typography>
                                    <Typography variant="body2">
                                        Description: {movie.description}
                                    </Typography>
                                </>
                            }
                        />
                        <IconButton 
                            edge="end"
                            onClick={() => handleRemoveMovie(movie.id)}
                            color="error"
                        >
                            <DeleteIcon />
                        </IconButton>
                    </ListItem>
                ))}
            </List>
        </Container>
    );
};

export default Profile;