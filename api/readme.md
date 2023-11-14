### How to start server
- Install Node.js v18.16.0
- Type in console `node app.js` 

### Available endpoints

- `/` - default with welcome message  
- `/article` - accepts params such as **id** or **title**, **page**, **pageSize**  
  Examples:
  - `/article?id=1,2,3`
  - `/article?title=Example&page=1&pageSize=5` _params **page** and **pageSize** are optional, default **pageSize**=20_
- `/article/latest` - _accepts optional **page** and **pageSize**, default **pageSize**=20 and maximum is 100_
  Examples:
  - `/article/latest` _returns 20 latest records_
  - `/article/latest?page=2&pageSize=10` 
- `/media_site` - accepts only article **id**
  Example:
  - `/media_site?id=1,2,3`
