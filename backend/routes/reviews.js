const express = require('express');
const router = express.Router();
const pool = require('../db');
const verifyFirebaseToken = require('../verifyFirebaseToken');

// POST /reviews — create a new review (requires auth)
router.post('/', verifyFirebaseToken, async (req, res) => {
  try {
    const { spot_id, rating, comment } = req.body;
    const firebase_uid = req.firebaseUid;

    // Find user by Firebase UID (or auto-create, if you want)
    let user = await pool.query('SELECT id FROM users WHERE firebase_uid = $1', [firebase_uid]);
    if (user.rows.length === 0) {
      user = await pool.query('INSERT INTO users (firebase_uid) VALUES ($1) RETURNING id', [firebase_uid]);
    }
    const user_id = user.rows[0].id;

    const result = await pool.query(
      `INSERT INTO reviews (spot_id, user_id, rating, comment)
       VALUES ($1, $2, $3, $4)
       RETURNING *`,
      [spot_id, user_id, rating, comment]
    );
    res.status(201).json(result.rows[0]);
  } catch (err) {
    console.error('Error creating review:', err);
    res.status(500).json({ error: 'Server error' });
  }
});

module.exports = router;
