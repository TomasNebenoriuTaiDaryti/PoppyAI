import React, { useState } from 'react';
import {
  Container,
  TextField,
  Button,
  Typography,
  Box,
  Paper
} from '@mui/material';

function MovieSearch() {
  const [title, setTitle] = useState('');
  const [movie, setMovie] = useState(null);
  const [error, setError] = useState(null);

  const handleSearch = async () => {
    if (!title.trim()) return;

    try {
      const res = await fetch(`http://localhost:8080/api/movies/search?title=${encodeURIComponent(title)}`);
      const data = await res.json();

      if (data.Title) {
        setMovie(data);
        setError(null);
      } else {
        setMovie(null);
        setError('Movie not found.');
      }
    } catch (err) {
      console.error(err);
      setMovie(null);
      setError('Something went wrong.');
    }
  };

  return (
    <Container maxWidth="sm" sx={{ mt: 8 }}>
      <Typography variant="h4" gutterBottom align="center">
        Movie Search
      </Typography>

      <Box display="flex" gap={2} mb={4}>
        <TextField
          label="Enter a movie title"
          variant="outlined"
          fullWidth
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
        <Button variant="contained" color="primary" onClick={handleSearch}>
          Search
        </Button>
      </Box>

      {error && (
        <Typography color="error" align="center" mb={2}>
          {error}
        </Typography>
      )}

      {movie && (
        <Paper elevation={3} sx={{ p: 3 }}>
          <img
            src={movie.Poster}
            alt={movie.Title}
            style={{ width: '100%', borderRadius: 8, marginBottom: 16 }}
          />
          <Typography variant="h5">{movie.Title} ({movie.Year})</Typography>
          <Typography variant="subtitle1"><strong>Genre:</strong> {movie.Genre}</Typography>
          <Typography variant="subtitle1"><strong>Runtime:</strong> {movie.Runtime}</Typography>
          <Typography variant="subtitle1"><strong>IMDb:</strong> {movie.imdbRating} ({movie.imdbVotes})</Typography>
          <Typography variant="body1" sx={{ mt: 2 }}>{movie.Plot}</Typography>

          {/* ✅ IMDb Link */}
          {movie.imdbID && (
            <Typography sx={{ mt: 2 }}>
              <a
                href={`https://www.imdb.com/title/${movie.imdbID}`}
                target="_blank"
                rel="noopener noreferrer"
                style={{ color: '#f5c518', textDecoration: 'none', fontWeight: 'bold' }}
              >
                View on IMDb
              </a>
            </Typography>
          )}
        </Paper>
      )}
    </Container>
  );
}

export default MovieSearch;