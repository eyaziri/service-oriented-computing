const { gql } = require('apollo-server');

const typeDefs = gql`
  type Query {
    searchAttractions(filter: AttractionFilter, limit: Int): [Attraction!]!
    attraction(id: ID!): Attraction
  }

  input AttractionFilter {
    category: String
    city: String
    maxPrice: Float
    tags: [String]
  }

  type Attraction {
    id: ID!
    name: String!
    city: String
    category: String
    price: Float
    tags: [String]
    location: Location
  }

  type Location {
    lat: Float
    lng: Float
  }

  type Mutation {
    createAttraction(input: CreateAttractionInput!): Attraction!
  }

  input CreateAttractionInput {
    name: String!
    city: String
    category: String
    price: Float
    tags: [String]
    lat: Float
    lng: Float
  }
`;

module.exports = typeDefs;
