const express = require('express');
const router = express.Router();
const pool = require('../db');
const verifyFirebaseToken = require('../verifyFirebaseToken');

// Get current user's favorites
router.get('/', verifyFirebaseToken, async (req, res) => {
  const { firebaseUid } = req;
  // Get user_id
  const userRes = await pool.query('SELECT id FROM users WHERE firebase_uid = $1', [firebaseUid]);
  if (!userRes.rows.length) return res.json([]);
  const user_id = userRes.rows[0].id;

  const favRes = await pool.query(
    `SELECT s.* FROM favorites f JOIN spots s ON f.spot_id = s.id WHERE f.user_id = $1 ORDER BY f.created_at DESC`,
    [user_id]
  );
  res.json(favRes.rows);
});

// Add to favorites
router.post('/', verifyFirebaseToken, async (req, res) => {
  const { firebaseUid } = req;
  const { spot_id } = req.body;
  // Get user_id
  const userRes = await pool.query('SELECT id FROM users WHERE firebase_uid = $1', [firebaseUid]);
  if (!userRes.rows.length) return res.status(400).json({error: 'User not found'});
  const user_id = userRes.rows[0].id;
  await pool.query(
    `INSERT INTO favorites (user_id, spot_id) VALUES ($1, $2) ON CONFLICT DO NOTHING`,
    [user_id, spot_id]
  );
  res.json({success: true});
});

// Remove from favorites
router.delete('/:spotId', verifyFirebaseToken, async (req, res) => {
  const { firebaseUid } = req;
  const { spotId } = req.params;
  // Get user_id
  const userRes = await pool.query('SELECT id FROM users WHERE firebase_uid = $1', [firebaseUid]);
  if (!userRes.rows.length) return res.status(400).json({error: 'User not found'});
  const user_id = userRes.rows[0].id;
  await pool.query(
    `DELETE FROM favorites WHERE user_id = $1 AND spot_id = $2`,
    [user_id, spotId]
  );
  res.json({success: true});
});

module.exports = router;