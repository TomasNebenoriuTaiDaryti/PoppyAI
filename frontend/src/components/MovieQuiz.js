import React, { useState, useEffect } from 'react';
import { Button, Typography, CircularProgress, Container, Box } from '@mui/material';

const MovieQuiz = () => {
  const [currentQuestion, setCurrentQuestion] = useState('');
  const [sessionId, setSessionId] = useState('');
  const [loading, setLoading] = useState(true);
  const [recommendations, setRecommendations] = useState([]);
  const [questionNumber, setQuestionNumber] = useState(1);
  const [error, setError] = useState('');

  useEffect(() => {
    const startQuiz = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/conversation/start', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include'
        });
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        setSessionId(data.sessionId);
        setCurrentQuestion(data.question);
        setQuestionNumber(data.questionNumber);
        setLoading(false);
      } catch (error) {
        console.error('Error starting quiz:', error);
        setError('Failed to start quiz. Please refresh the page.');
        setLoading(false);
      }
    };

    startQuiz();
  }, []);

  const handleAnswer = async (answer) => {
    try {
      setLoading(true);
      setError('');
      
      const response = await fetch('http://localhost:8080/api/conversation/answer', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId, answer }),
        credentials: 'include'
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Unknown error');
      }
      
      const data = await response.json();
      
      if (data.recommendations) {
        setRecommendations(data.recommendations);
      } else {
        setCurrentQuestion(data.question);
        setQuestionNumber(data.questionNumber);
      }
    } catch (error) {
      console.error('Error submitting answer:', error);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  if (error) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Typography variant="h4" color="error" gutterBottom>
          Error
        </Typography>
        <Typography variant="h6">{error}</Typography>
      </Container>
    );
  }

  if (recommendations.length > 0) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Typography variant="h4" gutterBottom>
          Recommended Movies
        </Typography>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          {recommendations.map((movie, index) => (
            <Typography key={index} variant="h6">🎬 {movie}</Typography>
          ))}
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>
        Movie Preference Quiz
      </Typography>
      
      {loading ? (
        <CircularProgress />
      ) : (
        <>
          <Typography variant="h5" sx={{ mb: 4 }}>
            Question {questionNumber}/10: {currentQuestion}
          </Typography>
          
          <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center' }}>
            <Button 
              variant="contained" 
              color="success" 
              size="large"
              onClick={() => handleAnswer('yes')}
              disabled={loading}
            >
              Yes
            </Button>
            <Button 
              variant="contained" 
              color="error" 
              size="large"
              onClick={() => handleAnswer('no')}
              disabled={loading}
            >
              No
            </Button>
          </Box>
        </>
      )}
    </Container>
  );
};

export default MovieQuiz;