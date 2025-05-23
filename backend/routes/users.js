const express = require('express');
const router = express.Router();
const pool = require('../db');
const verifyFirebaseToken = require('../verifyFirebaseToken');

// POST /users — create a new user (requires auth)
router.post('/', verifyFirebaseToken, async (req, res) => {
  try {
    const { display_name, photo_url } = req.body;
    const firebase_uid = req.firebaseUid; // set by middleware

    // Find or create the user in your DB
    let user = await pool.query('SELECT * FROM users WHERE firebase_uid = $1', [firebase_uid]);
    if (user.rows.length === 0) {
      user = await pool.query(
        `INSERT INTO users (firebase_uid, display_name, photo_url)
         VALUES ($1, $2, $3)
         RETURNING *`,
        [firebase_uid, display_name, photo_url]
      );
    }
    res.status(201).json(user.rows[0]);
  } catch (err) {
    console.error('Error creating user:', err);
    res.status(500).json({ error: 'Server error' });
  }
});


module.exports = router;
