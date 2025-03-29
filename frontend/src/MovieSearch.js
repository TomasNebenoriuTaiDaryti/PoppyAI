import React, { useState } from 'react';
import './MovieSearch.css';

function MovieSearch() {
  const [title, setTitle] = useState('');
  const [movie, setMovie] = useState(null);
  const [error, setError] = useState(null);

  const handleSearch = () => {
    if (!title) return;

    console.log("Sending request to backend for:", title);

    fetch(`http://localhost:8080/api/movies/search?title=${title}`)
      .then(res => res.json())
      .then(data => {
        console.log("Parsed JSON:", data);
        if (data.Title) {
          setMovie(data);
          setError(null);
        } else {
          setMovie(null);
          setError("Movie not found.");
        }
      })
      .catch(err => {
        console.error("Fetch error:", err);
        setError("Something went wrong.");
        setMovie(null);
      });
  };

  return (
    <div className="movie-search">
      <h1>Search for a Movie</h1>

      {/* 🔧 Wrapped in a flex container */}
      <div className="search-bar">
        <input
          type="text"
          placeholder="Enter a movie title..."
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
        <button onClick={handleSearch}>Search</button>
      </div>

      {error && <p className="error">{error}</p>}

      {movie && (
        <div className="movie-card">
          <img src={movie.Poster} alt={movie.Title} />
          <h2>{movie.Title} ({movie.Year})</h2>
          <p><strong>Genre:</strong> {movie.Genre}</p>
          <p><strong>Runtime:</strong> {movie.Runtime}</p>
          <p><strong>IMDb:</strong> {movie.imdbRating} ({movie.imdbVotes})</p>
          <p>{movie.Plot}</p>
          {movie.imdbID && (
            <p>
              <a
                href={`https://www.imdb.com/title/${movie.imdbID}`}
                target="_blank"
                rel="noopener noreferrer"
                className="imdb-link"
              >
                View on IMDb
              </a>
            </p>
          )}
        </div>
      )}
    </div>
  );
}

export default MovieSearch;
