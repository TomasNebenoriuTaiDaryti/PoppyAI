import React, { useState } from 'react';
import { 
  Container,
  TextField, 
  Button, 
  Typography, 
  Box, 
  Paper 
} from '@mui/material';

const MovieSearch = () => {
  const [title, setTitle] = useState('');
  const [movie, setMovie] = useState(null);
  const [error, setError] = useState(null);

  const handleSearch = async () => {
    if (!title) return;

    try {
      const response = await fetch(`http://localhost:8080/api/movies/search?title=${encodeURIComponent(title)}`);
      const data = await response.json();
      
      if (data.Title) {
        setMovie(data);
        setError(null);
      } else {
        setMovie(null);
        setError("Movie not found.");
      }
    } catch (err) {
      console.error("Search error:", err);
      setError("Something went wrong.");
      setMovie(null);
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>
        Movie Search
      </Typography>
      
      <Box sx={{ display: 'flex', gap: 2, mb: 4 }}>
        <TextField
          fullWidth
          variant="outlined"
          label="Search Movie Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
        <Button
          variant="contained"
          size="large"
          onClick={handleSearch}
          sx={{ px: 4 }}
        >
          Search
        </Button>
      </Box>

      {error && <Typography color="error">{error}</Typography>}

      {movie && (
        <Paper sx={{ p: 3, mt: 2 }}>
          <Box sx={{ display: 'flex', gap: 3 }}>
            {movie.Poster && <img src={movie.Poster} alt={movie.Title} style={{ maxWidth: 200 }} />}
            <Box>
              <Typography variant="h5">{movie.Title} ({movie.Year})</Typography>
              <Typography><strong>Genre:</strong> {movie.Genre}</Typography>
              <Typography><strong>Runtime:</strong> {movie.Runtime}</Typography>
              <Typography><strong>IMDb Rating:</strong> {movie.imdbRating}</Typography>
              <Typography sx={{ mt: 2 }}>{movie.Plot}</Typography>
            </Box>
          </Box>
        </Paper>
      )}
    </Container>
  );
};

export default MovieSearch;