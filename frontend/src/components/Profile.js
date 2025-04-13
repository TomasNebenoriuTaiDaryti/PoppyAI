import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Container, 
  Typography, 
  Button, 
  CircularProgress, 
  Grid, 
  Card, 
  CardContent, 
  IconButton, 
  Box,
  Link
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import api from '../api/api';

const Profile = ({ onLogout }) => {
    const [watchlist, setWatchlist] = useState([]);
    const [loading, setLoading] = useState(true);
    const [userData, setUserData] = useState(null);
    const navigate = useNavigate();
    const username = localStorage.getItem('username');

    useEffect(() => {
        const loadData = async () => {
            try {
                const [userResponse, watchlistResponse] = await Promise.all([
                    api.get(`/profile/${username}`),
                    api.get(`/watchlist/${username}`)
                ]);
                
                setUserData(userResponse.data);
                setWatchlist(watchlistResponse.data);
            } catch (error) {
                console.error('Loading error:', error);
            } finally {
                setLoading(false);
            }
        };

        loadData();
    }, [username, navigate]);

    const handleRemoveMovie = async (movieId) => {
        try {
            await api.delete(`/watchlist/${username}/${movieId}`);
            const watchlistResponse = await api.get(`/watchlist/${username}`);
            setWatchlist(watchlistResponse.data);
        } catch (error) {
            console.error('Delete error:', error);
        }
    };

    if (loading) {
        return (
            <Container sx={{ display: 'flex', justifyContent: 'center', mt: 10 }}>
                <CircularProgress />
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            <Box sx={{ 
                display: 'flex', 
                justifyContent: 'space-between', 
                alignItems: 'center', 
                mb: 4,
                flexWrap: 'wrap',
                gap: 2
            }}>
                <Typography variant="h4" component="h1">
                    Welcome, {userData?.username}
                </Typography>
                <Box sx={{ display: 'flex', gap: 2 }}>
                    <Button
                        variant="contained"
                        startIcon={<EditIcon />}
                        onClick={() => navigate('/profile/edit')}
                    >
                        Edit Profile
                    </Button>
                    <Button
                        variant="outlined"
                        onClick={onLogout}
                    >
                        Logout
                    </Button>
                </Box>
            </Box>

            <Typography variant="h5" gutterBottom sx={{ mb: 3 }}>
                Your Watchlist ({watchlist.length})
            </Typography>

            {watchlist.length === 0 ? (
                <Typography variant="body1" color="text.secondary">
                    Your watchlist is empty. Start adding movies!
                </Typography>
            ) : (
                <Grid container spacing={3}>
                    {watchlist.map(movie => (
                        <Grid item xs={12} sm={6} md={4} key={movie.id}>
                            <Card sx={{ 
                                height: '100%', 
                                display: 'flex', 
                                flexDirection: 'column',
                                p: 2
                            }}>
                                <CardContent>
                                    <Typography variant="h6" gutterBottom>
                                        {movie.title}
                                    </Typography>
                                    
                                    <Typography variant="body2" color="text.secondary" gutterBottom>
                                        Genre: {movie.genre}
                                    </Typography>

                                    <Typography variant="body2" paragraph>
                                        {movie.description}
                                    </Typography>

                                    {movie.imdbID && (
                                        <Box sx={{ 
                                            display: 'flex', 
                                            justifyContent: 'space-between', 
                                            alignItems: 'center'
                                        }}>
                                            <Link
                                                href={`https://www.imdb.com/title/${movie.imdbID}`}
                                                target="_blank"
                                                rel="noopener"
                                                sx={{ 
                                                    color: '#f5c518',
                                                    textDecoration: 'none',
                                                    '&:hover': { textDecoration: 'underline' }
                                                }}
                                            >
                                                View on IMDb
                                            </Link>
                                            <IconButton 
                                                onClick={() => handleRemoveMovie(movie.id)}
                                                color="error"
                                            >
                                                <DeleteIcon />
                                            </IconButton>
                                        </Box>
                                    )}
                                </CardContent>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            )}
        </Container>
    );
};

export default Profile;