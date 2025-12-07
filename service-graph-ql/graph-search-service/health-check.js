// health-check.js
const http = require('http');

module.exports = {
  checkHealth: () => {
    return new Promise((resolve, reject) => {
      const options = {
        hostname: 'localhost',
        port: 4000,
        path: '/.well-known/apollo/server-health',
        method: 'GET',
        timeout: 5000
      };

      const req = http.request(options, (res) => {
        if (res.statusCode === 200) {
          resolve('UP');
        } else {
          resolve('DOWN');
        }
      });

      req.on('error', () => {
        resolve('DOWN');
      });

      req.on('timeout', () => {
        req.destroy();
        resolve('DOWN');
      });

      req.end();
    });
  }
};