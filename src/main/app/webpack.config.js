const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin')
const {CleanWebpackPlugin} = require('clean-webpack-plugin')

module.exports = {
  entry: {
    'import': './src/index.jsx',
  },
  output: {
    filename: 'import.bundle.js',
    path: path.resolve(__dirname, 'dist'),
    publicPath: "/app"
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: [
          {
            loader: 'babel-loader',
            options: {
              presets: [
                ['@babel/preset-env', {
                  "targets": {
                    "node": "current"
                  }
                }],
                ['@babel/preset-react'],
              ],
              env: {
                "test": {
                  "plugins": ["transform-es2015-modules-commonjs"]
                }
              }
            },
          }
        ],
      },
      {
        test: /\.css$/,
        use: [
          'raw-loader',
        ],
      },
    ],
  },
  resolve: {
    extensions: ['.tsx', '.ts', '.js', '.jsx'],
    plugins: []
  },
  plugins: [
    new webpack.HotModuleReplacementPlugin(),
    new CleanWebpackPlugin(),
    new HtmlWebpackPlugin(
        {
          template: "./public/index.html",
          path: path.resolve(__dirname, 'dist'),
        }),
  ],
};
