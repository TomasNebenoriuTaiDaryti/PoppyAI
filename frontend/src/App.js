import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useState, useEffect, useMemo } from 'react';
import { CssBaseline, Container, CircularProgress, ThemeProvider } from '@mui/material';
import { darkTheme, lightTheme } from './theme';
import Login from './components/Login';
import Register from './components/Register';
import MainPage from './components/MainPage';
import MovieSearch from './components/MovieSearch';
import Profile from './components/Profile';
import MovieQuiz from './components/MovieQuiz';
import ProfileEdit from './components/ProfileEdit';

const App = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);
  const [darkMode, setDarkMode] = useState(true);
  const theme = useMemo(
    () => darkMode ? darkTheme : lightTheme,
    [darkMode]
  );

  useEffect(() => {
    const token = localStorage.getItem('isAuthenticated');
    setIsAuthenticated(!!token);
    setLoading(false);
  }, []);

  const handleLogin = (username) => {
    localStorage.setItem('isAuthenticated', 'true');
    localStorage.setItem('username', username);
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    localStorage.removeItem('isAuthenticated');
    setIsAuthenticated(false);
  };
  const toggleDarkMode = () => {
    setDarkMode(!darkMode);
  };

  if (loading) {
    return (
      <Container sx={{ display: 'flex', justifyContent: 'center', mt: 10 }}>
        <CircularProgress />
      </Container>
    );
  }

  return (
    <ThemeProvider theme={theme}>
    <Router>
      <CssBaseline />
      <Routes>
        <Route path="/login" element={
          isAuthenticated ? <Navigate to="/" /> : <Login onLogin={handleLogin} />
        } />
        <Route path="/register" element={
          isAuthenticated ? <Navigate to="/" /> : <Register />
        } />
        <Route path="/" element={
          isAuthenticated ? <MainPage onLogout={handleLogout} darkMode={darkMode} toggleDarkMode={toggleDarkMode} /> : <Navigate to="/login" />
        } />
        <Route path="/search" element={
          isAuthenticated ? <MovieSearch /> : <Navigate to="/login" />
        } />
        <Route path="/profile" element={
          isAuthenticated ? <Profile onLogout={handleLogout} /> : <Navigate to="/login" />
        } />
        <Route path="/quiz" element={
          isAuthenticated ? <MovieQuiz /> : <Navigate to="/login" />
        } />
        <Route path="/profile/edit" element={
          isAuthenticated ? <ProfileEdit /> : <Navigate to="/login" />
        } />
      </Routes>
    </Router>
    </ThemeProvider>
  );
};

export default App;