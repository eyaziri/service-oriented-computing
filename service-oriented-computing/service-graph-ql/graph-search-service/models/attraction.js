const { DataTypes } = require('sequelize');
const { sequelize } = require('./index');

const Attraction = sequelize.define('Attraction', {
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true
  },
  name: { type: DataTypes.STRING, allowNull: false },
  city: { type: DataTypes.STRING },
  category: { type: DataTypes.STRING },
  price: { type: DataTypes.DECIMAL(8,2), allowNull: true },
  tags: { type: DataTypes.STRING }, 
  lat: { type: DataTypes.DOUBLE, allowNull: true },
  lng: { type: DataTypes.DOUBLE, allowNull: true }
}, {
  tableName: 'attractions',
  timestamps: true
});

module.exports = Attraction;
