const http = require('http');
const app = require('./app');

// set the port
const port = process.env.PORT || 3000;

// create Server for app
const server = http.createServer(app);

// listen at port for requests
server.listen(port, () =>{
    console.log('Nyansapo API is listening on port ${port}')
});