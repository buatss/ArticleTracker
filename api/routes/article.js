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
  console.log('article: Connection with database is successful!');
});

router.get('/', (req, res) => {
  const articleId = req.query.id;
  if (!articleId) {
    res.status(400).send('No "id" parameter in query. Valid parameter is e.g. "id=5" or like "id=1,2,3"');
    return;
  }

  const query = `SELECT * FROM article WHERE id IN (${articleId})`;

  connection.query(query, (err, results) => {
    if (err) {
      console.error('Query error: ', err);
      res.status(500).send('Query error');
      return;
    }

    if (results.length === 0) {
      res.send('No article with such id');
    } else {
      res.json(results);
    }
  });
});

router.get('/latest', (req, res) => {
  const limit = req.query.limit;
  const realLimit = Math.min(parseInt(req.query.limit, 10) || 20, 100);

  if (!limit) {
    res.status(400).send('No limit param, valid is e.g. "limit=10", it cannot be creater than 100.');
    return;
  }

  const query = `SELECT * FROM article ORDER BY upload_date DESC LIMIT ${realLimit}`;

  connection.query(query, (err, results) => {
    if (err) {
      console.error('Query error: ', err);
      res.status(500).send('Query error');
      return;
    }

    if (results.length === 0) {
      res.send('No articles with such id');
    } else {
      res.json(results);
    }
  });
});

module.exports = router;
