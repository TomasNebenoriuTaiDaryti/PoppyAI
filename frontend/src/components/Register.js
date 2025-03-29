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

const Register = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    email: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await axios.post('http://localhost:8080/api/register', formData);
      
      if (response.data === "User registered successfully") {
        navigate('/login');
      }
    } catch (err) {
      setError(err.response?.data || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="xs" sx={{ mt: 8, minHeight: '100vh' }}>
      <Typography variant="h4" component="h1" sx={{ mb: 4 }}>
        Register
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
          label="Email"
          type="email"
          name="email"
          margin="normal"
          value={formData.email}
          onChange={(e) => setFormData({...formData, email: e.target.value})}
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
          {loading ? <CircularProgress size={24} /> : 'Register'}
        </Button>
      </Box>
    </Container>
  );
};

export default Register;