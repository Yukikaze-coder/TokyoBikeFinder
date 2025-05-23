const express = require('express');
const router = express.Router();
const pool = require('../db');
const verifyFirebaseToken = require('../verifyFirebaseToken');

// GET /spots — get all parking spots
router.get('/', async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM spots ORDER BY created_at DESC');
    res.json(result.rows);
  } catch (err) {
    console.error('Error fetching spots:', err);
    res.status(500).json({ error: 'Server error' });
  }
});

// POST /spots — create a new parking spot (now accepts capacity)
router.post('/', verifyFirebaseToken, async (req, res) => {
  try {
    const {
      name,
      lat,
      lng,
      address,
      photo_url = null,
      price = null,
      type = null,
      capacity = null
    } = req.body;

    const creator_firebase_uid = req.firebaseUid; // Set by middleware

    // Find or create the user in your DB
    let user = await pool.query('SELECT id FROM users WHERE firebase_uid = $1', [creator_firebase_uid]);
    if (user.rows.length === 0) {
      // Optionally, auto-create user entry here
      user = await pool.query(
        'INSERT INTO users (firebase_uid) VALUES ($1) RETURNING id',
        [creator_firebase_uid]
      );
    }
    const creator_user_id = user.rows[0].id;

    const result = await pool.query(
      `INSERT INTO spots
        (name, lat, lng, address, photo_url, price, type, capacity, creator_user_id)
        VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
        RETURNING *`,
      [name, lat, lng, address, photo_url, price, type, capacity, creator_user_id]
    );
    res.status(201).json(result.rows[0]);
  } catch (err) {
    console.error('Error creating spot:', err);
    res.status(500).json({ error: 'Server error' });
  }
});

module.exports = router;
