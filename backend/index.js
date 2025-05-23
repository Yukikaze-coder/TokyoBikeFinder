const express = require('express');
const cors = require('cors');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

const spotRoutes = require('./routes/spots');
app.use('/api/spots', spotRoutes);

const favoritesRouter = require('./routes/favorites');
app.use('/api/favorites', favoritesRouter);

// Test route to confirm the server is working
app.get('/api/test', (req, res) => {
  res.send('TokyoBikeFinder backend is running!');
});

const userRoutes = require('./routes/users');
app.use('/users', userRoutes);


const PORT = process.env.PORT || 4000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
