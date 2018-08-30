var commonConfig = require('./webpack.common.js');
var helpers = require('./helpers');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var HtmlWebpackPlugin = require('html-webpack-plugin');
var ENV = process.env.npm_lifecycle_event;
var isTest = ENV === 'test' || ENV === 'test-watch';


var webpackConfig = commonConfig;

if (!isTest) {
    webpackConfig.plugins.push(
        // Inject script and link tags into html files
        // Reference: https://github.com/ampedandwired/html-webpack-plugin
        new HtmlWebpackPlugin({
            template: './src/main/assets/public/index.html',
            chunksSortMode: 'dependency'
        }),

        // Extract css files
        // Reference: https://github.com/webpack/extract-text-webpack-plugin
        // Disabled when in test mode or not in build mode
        new ExtractTextPlugin({filename: 'css/[name].[hash].css', disable: true})
    );

    webpackConfig.entry.main.unshift("webpack-dev-server/client?http://localhost:9001/");


}


module.exports = webpackConfig;
