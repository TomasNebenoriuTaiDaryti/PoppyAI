import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { CssBaseline, Container, CircularProgress } from '@mui/material';
import Login from './components/Login';
import Register from './components/Register';
import MainPage from './components/MainPage';
import MovieSearch from './MovieSearch';

const App = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('isAuthenticated');
    setIsAuthenticated(!!token);
    setLoading(false);
  }, []);

  const handleLogin = () => {
    localStorage.setItem('isAuthenticated', 'true');
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    localStorage.removeItem('isAuthenticated');
    setIsAuthenticated(false);
  };

  if (loading) {
    return (
      <Container sx={{ display: 'flex', justifyContent: 'center', mt: 10 }}>
        <CircularProgress />
      </Container>
    );
  }

  return (
    <Router>
      <CssBaseline />
      <Routes>
        <Route path="/login" element={
          isAuthenticated ? <Navigate to="/search" /> : <Login onLogin={handleLogin} />
        } />
        <Route path="/register" element={
          isAuthenticated ? <Navigate to="/" /> : <Register />
        } />
        <Route path="/" element={
          isAuthenticated ? <MainPage onLogout={handleLogout} /> : <Navigate to="/login" />
        } />
      </Routes>
    </Router>
  );
};

export default App;