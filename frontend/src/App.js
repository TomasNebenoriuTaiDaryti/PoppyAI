import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import MovieSearch from './MovieSearch';
// import Header from './components/Header';
// import Login from './components/Login';
// import QuestionForm from './components/QuestionForm';
// import MovieRecommendation from './components/MovieRecommendation';
// import WatchList from './components/WatchList';

function App() {
  return (
    <Router>
      {/* <Header /> */}
      <Routes>
        <Route path="/" element={<Navigate to="/search" />} />
        <Route path="/search" element={<MovieSearch />} />
        {/* <Route path="/login" element={<Login />} /> */}
        {/* <Route path="/questions" element={<QuestionForm />} /> */}
        {/* <Route path="/recommend" element={<MovieRecommendation />} /> */}
        {/* <Route path="/watchlist" element={<WatchList />} /> */}
      </Routes>
    </Router>
  );
}

export default App;
