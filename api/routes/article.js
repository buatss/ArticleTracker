const express = require('express');
const router = express.Router();
const mysql = require('mysql2');
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
  const title = req.query.title;
  const page = parseInt(req.query.page, 10) || 1;
  const pageSize = parseInt(req.query.pageSize, 10) || 20;

  if (!articleId && !title) {
    res.status(400).send('No "id" or "title" parameter in query. Valid parameters are e.g. "id=5" or like "id=1,2,3" or "title=Example"');
    return;
  }

  const offset = (page - 1) * pageSize;

  if (articleId) {
    const idArray = articleId.split(',').map(id => parseInt(id, 10));

    if (!idArray.every(id => !isNaN(id))) {
      res.status(400).send('Invalid "id" parameter in query.');
      return;
    }

    const queryById = `SELECT * FROM article WHERE id IN (${idArray.join(',')})`;

    connection.query(queryById, (err, results) => {
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
  } else if (title) {
    const queryByTitle = `SELECT * FROM article WHERE title LIKE '%${title}%' LIMIT ${pageSize} OFFSET ${offset}`;

    connection.query(queryByTitle, (err, results) => {
      if (err) {
        console.error('Query error: ', err);
        res.status(500).send('Query error');
        return;
      }

      if (results.length === 0) {
        res.send('No articles with such title');
      } else {
        res.json(results);
      }
    });
  }
});

router.get('/latest', (req, res) => {
  const page = parseInt(req.query.page, 10) || 1;
  const pageSize = parseInt(req.query.pageSize, 10) || 20;
  const realLimit = Math.min(pageSize, 100);

  const offset = (page - 1) * realLimit;
  const queryLatest = `SELECT * FROM article ORDER BY upload_date DESC LIMIT ${realLimit} OFFSET ${offset}`;

  connection.query(queryLatest, (err, results) => {
    if (err) {
      console.error('Query error: ', err);
      res.status(500).send('Query error');
      return;
    }

    if (results.length === 0) {
      res.send('No articles available');
    } else {
      res.json(results);
    }
  });
});


module.exports = router;
