const path = require('path')

module.exports = {

  clearMocks: true,

  coverageDirectory: "coverage",

  moduleNameMapper: {
    '^.+\\.(css|less|scss)$': path.resolve('.', 'css-stub.js')
  },

  transform: {
    '^.+\\.(js|jsx)$': 'babel-jest'
  },

  testMatch: [
    '**/src/test/**/*.test.js',
  ],

  transformIgnorePatterns: [
    "node_modules/(?!(jest-)?react-native|@?react-navigation)"
  ],
};
