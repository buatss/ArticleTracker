const express = require('express');
const router = express.Router();
const mysql = require('mysql');
const config = require('../config');

const connection = mysql.createConnection({
  host: config.host,
  user: config.user,
  password: config.password,
  database: config.database,
});

connection.connect((err) => {
  if (err) {
    console.error('Error connection with db: ', err);
    return;
  }
  console.log('media_site: Connection with database is successful!');
});

router.get('/', (req, res) => {
  const mediaSiteId = req.query.id;
  if (!mediaSiteId) {
    res.status(400).send('No "id" parameter in query.');
    return;
  }

  const query = `SELECT * FROM media_site WHERE id IN (${mediaSiteId})`;

  connection.query(query, (err, results) => {
    if (err) {
      console.error('Query error: ', err);
      res.status(500).send('Query error');
      return;
    }

    if (results.length === 0) {
      res.send('No media site with such id');
    } else {
      res.json(results);
    }
  });
});

module.exports = router;
