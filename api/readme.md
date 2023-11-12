### How to start server
- Install Node.js v18.16.0
- Type in console `node app.js` 

### Available endpoints

- `/` - default with welcome message  
- `/article` - accepts only article id e.g. `/article?id=1,2,3`
- `/article/latest` - accepts only limit e.g. `/article/latest?limit=10`, limit cannot be greater than 100.
- `/media_site` - accepts only article id e.g. `/media_site?id=1,2,3`
