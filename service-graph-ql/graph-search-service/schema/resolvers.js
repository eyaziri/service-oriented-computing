const Attraction = require('../models/attraction');

function parseTagsField(tagsString) {
  if (!tagsString) return [];
  return tagsString.split(',').map(t => t.trim()).filter(Boolean);
}

function tagsArrayToString(tagsArray) {
  if (!tagsArray) return null;
  return tagsArray.join(',');
}

const resolvers = {
  Query: {
    async searchAttractions(_, { filter = {}, limit = 50 }) {
      const where = {};
      const { category, city, maxPrice, tags } = filter;

      if (category) where.category = category;
      if (city) where.city = city;
      if (maxPrice !== undefined && maxPrice !== null) where.price = { $lte: maxPrice };

      const { Op } = require('sequelize');
      if (maxPrice !== undefined && maxPrice !== null) {
        where.price = { [Op.lte]: maxPrice };
      }

      let results = await Attraction.findAll({ where, limit });

      if (tags && tags.length > 0) {
        results = results.filter(a => {
          const aTags = parseTagsField(a.tags);
          return tags.every(t => aTags.includes(t));
        });
      }

      return results.map(a => ({
        id: a.id,
        name: a.name,
        city: a.city,
        category: a.category,
        price: a.price ? Number(a.price) : null,
        tags: parseTagsField(a.tags),
        location: { lat: a.lat, lng: a.lng }
      }));
    },

    async attraction(_, { id }) {
      const a = await Attraction.findByPk(id);
      if (!a) return null;
      return {
        id: a.id,
        name: a.name,
        city: a.city,
        category: a.category,
        price: a.price ? Number(a.price) : null,
        tags: parseTagsField(a.tags),
        location: { lat: a.lat, lng: a.lng }
      };
    }
  },

  Mutation: {
    async createAttraction(_, { input }) {
      const record = await Attraction.create({
        name: input.name,
        city: input.city,
        category: input.category,
        price: input.price,
        tags: tagsArrayToString(input.tags),
        lat: input.lat,
        lng: input.lng
      });

      return {
        id: record.id,
        name: record.name,
        city: record.city,
        category: record.category,
        price: record.price ? Number(record.price) : null,
        tags: parseTagsField(record.tags),
        location: { lat: record.lat, lng: record.lng }
      };
    }
  }
};

module.exports = resolvers;
