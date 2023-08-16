require('dotenv').config({ path: 'config/.env' });
const app = require('./app');
const port = process.env.PORT;

// socket v4.7
// const http = require('http');
// const server = http.createServer(app);
// const { Server } = require('socket.io');
// const io = new Server(server);

// socket v2
const http = require('http');
const server = http.createServer(app);
io = require('socket.io').listen(server);

app.get('/', (req, res) => {
  res.sendFile(__dirname + '/index.html');
});

const sleep = (ms) => {
  return new Promise((resolve) => setTimeout(resolve, ms));
};

let x_axis = 0;
io.on('connection', (socket) => {
  console.log('a user to connected: ' + socket.id);
  // nhan du lieu tu client
  socket.on('chat', async (msg) => {
    console.log(msg);
    while (true) {
      const y_axis = Math.random() * 15 + 1;
      // gui du lieu den all client
      io.emit('chat', { x_axis, y_axis });
      x_axis++;
      await sleep(5000);
    }
  });
  socket.on('disconnect', () => {
    console.log('user disconnected');
  });
});

server.listen(port, () => {
  console.log('Server is on ' + port);
});
