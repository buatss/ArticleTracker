const express = require('express');
const app = express();
const port = 3000;
const config = require('./config');
const articleRoutes = require('./routes/article');
const defaultRoutes = require('./routes/default')

app.use('/', defaultRoutes);
app.use('/article', articleRoutes);

app.listen(port, () => {
  console.log(`Server is running on:${port}`);
});
