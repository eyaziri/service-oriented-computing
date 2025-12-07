const Eureka = require('eureka-js-client').Eureka;

const eurekaClient = new Eureka({
  instance: {
    app: 'GRAPHQL-SERVICE', // Nom qui apparaîtra dans Eureka
    hostName: 'localhost',
    ipAddr: '127.0.0.1',
    port: {
      '$': process.env.PORT || 4000,
      '@enabled': true,
    },
    vipAddress: 'graphql-service',
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
    },
    statusPageUrl: `http://localhost:${process.env.PORT || 4000}/graphql`,
    healthCheckUrl: `http://localhost:${process.env.PORT || 4000}/.well-known/apollo/server-health`,
    homePageUrl: `http://localhost:${process.env.PORT || 4000}/`,
  },
  eureka: {
    host: 'localhost', // Important: changer 'eureka-server' en 'localhost' pour test local
    port: 8761,
    servicePath: '/eureka/apps/',
    maxRetries: 10,
    requestRetryDelay: 2000,
  },
});

module.exports = eurekaClient;