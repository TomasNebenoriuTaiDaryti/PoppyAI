import React, { useState, useEffect } from 'react';
import {
  Typography,
  Button,
  Container,
  Box,
  Paper,
  CircularProgress,
  Card,
  CardMedia,
  CardContent
} from '@mui/material';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import ThumbDownIcon from '@mui/icons-material/ThumbDown';
import api from '../api/api';

function MovieQuiz() {
  const [sessionId, setSessionId] = useState(null);
  const [question, setQuestion] = useState('');
  const [questionNumber, setQuestionNumber] = useState(1);
  const [loading, setLoading] = useState(true);
  const [recommendedMovies, setRecommendedMovies] = useState([]);
  const [quizFinished, setQuizFinished] = useState(false);
  const [addedMovies, setAddedMovies] = useState(new Set());

  useEffect(() => {
    fetch('http://localhost:8080/api/conversation/start', { method: 'POST' })
      .then(res => res.json())
      .then(data => {
        setSessionId(data.sessionId);
        setQuestion(data.question);
        setQuestionNumber(data.questionNumber);
        setLoading(false);
      })
      .catch(err => {
        console.error("Error starting conversation:", err);
        setLoading(false);
      });
  }, []);

  const fetchOmdbOneByOne = async (titles) => {
    const results = [];
  
    for (const title of titles) {
      const cleanTitle = title.replace(/['"]+/g, '').trim();
      const url = `http://localhost:8080/api/movies/search?title=${encodeURIComponent(cleanTitle)}`;
  
      try {
        const res = await fetch(url);
        const data = await res.json();
  
        console.log(`OMDb response for "${cleanTitle}":`, data);
  
        if (data && (data.title || data.Title)) {
          results.push({
            title: data.title || data.Title,
            genre: data.genre || data.Genre,
            plot: data.plot || data.Plot,
            year: data.year || data.Year,
            runtime: data.runtime || data.Runtime,
            imdbRating: data.imdbRating,
            imdbVotes: data.imdbVotes,
            imdbID: data.imdbID,
            poster: data.poster || data.Poster,
          });
        }
        
      } catch (err) {
        console.error(`Error fetching OMDb data for "${cleanTitle}":`, err);
      }
    }
  
    return results;
  };
  

  const handleAnswer = (answer) => {
    if (!sessionId) return;

    setLoading(true);
    fetch('http://localhost:8080/api/conversation/answer', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sessionId, answer })
    })
      .then(res => res.json())
      .then(async (data) => {
        if (data.recommendations) {
          const titles = Array.isArray(data.recommendations)
          ? data.recommendations
          : String(data.recommendations)
              .split('\n')
              .map(t => t.trim())
              .filter(Boolean);


          console.log("Movie titles from backend:", titles);

          const movies = await fetchOmdbOneByOne(titles);

          if (movies.length > 0) {
            console.log("Valid OMDb movies:", movies);
            setRecommendedMovies(movies);
            setQuizFinished(true);
            setQuestion(null);
          } else {
            console.warn("No valid movies returned. Not finishing quiz.");
            setQuizFinished(false);
          }

        } else {
          setQuestion(data.question);
          setQuestionNumber(data.questionNumber);
        }
        setLoading(false);
      })
      .catch(err => {
        console.error("Error sending answer:", err);
        setLoading(false);
      });
  };

  const handleAddToWatchlist = async (movie) => {
    const username = localStorage.getItem('username');
    try {
        await api.post(`/watchlist/${username}/add`, {
            title: movie.title,
            genre: movie.genre,
            description: movie.plot,
            year: movie.year,
            runtime: movie.runtime,
            imdbRating: movie.imdbRating,
            imdbID: movie.imdbID,
            poster: movie.poster
        });
        setAddedMovies(prev => new Set([...prev, movie.imdbID]));
    } catch (error) {
        console.error('Error adding to watchlist:', error);
    }
};

  return (
    <Container maxWidth="sm" sx={{ mt: 4 }}>
      <Paper elevation={4} sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom>Movie Preference Quiz</Typography>

        {loading ? (
          <Box textAlign="center" mt={3}>
            <CircularProgress color="primary" />
          </Box>
        ) : quizFinished ? (
          <Box mt={2}>
            <Typography variant="h6" gutterBottom>
              🎬 Based on your answers, here are 3 movie recommendations:
            </Typography>
            {recommendedMovies.map((movie, index) => (
              <Card key={index} sx={{ mb: 2 }}>
                {movie.poster && movie.poster !== "N/A" && (
                  <CardMedia
                    component="img"
                    height="350"
                    image={movie.poster}
                    alt={movie.title}
                  />
                )}
                <CardContent sx={{ backgroundColor: '#1e1e1e', color: 'white' }}>
                  <Typography variant="h6">{movie.title} ({movie.year})</Typography>
                  <Typography variant="body2"><strong>Genre:</strong> {movie.genre}</Typography>
                  <Typography variant="body2"><strong>Runtime:</strong> {movie.runtime}</Typography>
                  <Typography variant="body2"><strong>IMDb:</strong> {movie.imdbRating} ({movie.imdbVotes})</Typography>
                  <Typography variant="body2"><strong>Plot:</strong> {movie.plot}</Typography>
                  {movie.imdbID && (
                    <Typography variant="body2" sx={{ mt: 1 }}>
                      🔗 <a href={`https://www.imdb.com/title/${movie.imdbID}`} target="_blank" rel="noopener noreferrer" style={{ color: '#90caf9' }}>
                        View on IMDb
                      </a>
                    </Typography>
                  )}
                  <Button
                    variant="contained"
                    size="small"
                    onClick={() => handleAddToWatchlist(movie)}
                    sx={{ 
                      mt: 1,
                      bgcolor: addedMovies.has(movie.imdbID) ? '#4caf50' : 'primary.main',
                      '&:hover': {
                          bgcolor: addedMovies.has(movie.imdbID) ? '#388e3c' : 'primary.dark'
                      }
                    }}
                  >
                    {addedMovies.has(movie.imdbID) ? 'Added ✓' : 'Add to Watchlist'}
                  </Button>
                </CardContent>
              </Card>
            ))}
          </Box>
        ) : (
          <>
            <Typography variant="body1">
              <strong>Question {questionNumber}/10:</strong> {question}
            </Typography>
            <Box display="flex" justifyContent="center" gap={2}>
              <Button
                variant="contained"
                color="success"
                onClick={() => handleAnswer("Yes")}
                startIcon={<ThumbUpIcon />}
              >
                YES
              </Button>
              <Button
                variant="contained"
                color="error"
                onClick={() => handleAnswer("No")}
                startIcon={<ThumbDownIcon />}
              >
                NO
              </Button>
            </Box>
          </>
        )}
      </Paper>
    </Container>
  );
}

export default MovieQuiz;
