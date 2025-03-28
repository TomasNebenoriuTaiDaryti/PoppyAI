import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Container,
  Typography,
  TextField,
  Button,
  Box,
  CircularProgress
} from '@mui/material';
import axios from 'axios';

const Login = ({ onLogin }) => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await axios.post('http://localhost:8080/api/login', formData);
      
      if (response.data === "Login successful") {
        onLogin();
        navigate('/');
      }
    } catch (err) {
      setError(err.response?.data || 'Invalid username or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="xs" sx={{ mt: 8, minHeight: '100vh' }}>
      <Typography variant="h4" component="h1" sx={{ mb: 4 }}>
        Login
      </Typography>
      <Box component="form" onSubmit={handleSubmit} sx={{ p: 3, boxShadow: 3, borderRadius: 2 }}>
        <TextField
          fullWidth
          label="Username"
          name="username"
          margin="normal"
          value={formData.username}
          onChange={(e) => setFormData({...formData, username: e.target.value})}
          disabled={loading}
        />
        <TextField
          fullWidth
          label="Password"
          type="password"
          name="password"
          margin="normal"
          value={formData.password}
          onChange={(e) => setFormData({...formData, password: e.target.value})}
          disabled={loading}
        />
        {error && <Typography color="error" sx={{ mt: 2 }}>{error}</Typography>}
        <Button
          fullWidth
          variant="contained"
          size="large"
          type="submit"
          disabled={loading}
          sx={{ mt: 3 }}
        >
          {loading ? <CircularProgress size={24} /> : 'Sign In'}
        </Button>
        <Button
          fullWidth
          variant="outlined"
          sx={{ mt: 2 }}
          onClick={() => navigate('/register')}
        >
          Create New Account
        </Button>
      </Box>
    </Container>
  );
};

export default Login;