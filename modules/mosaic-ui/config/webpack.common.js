var webpack = require('webpack');
var helpers = require('./helpers');
var path = require('path');
var ProgressPlugin = require('webpack/lib/ProgressPlugin');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var ENV = process.env.npm_lifecycle_event;
var isTest = ENV === 'test' || ENV === 'test-watch';


module.exports = {

    devtool: 'cheap-module-source-map',

    cache: true,

    output: {
        filename: '[name].bundle.js',
        sourceMapFilename: '[name].map',
        chunkFilename: '[id].chunk.js',
        path: helpers.root('./target/dist'),
        publicPath: '/mosaic/ui/'
    },

    resolve: {
        modules: [
            path.join(__dirname, 'src'),
            'node_modules'
        ],
        extensions: ['.ts', '.js'], // The extension '.d.ts' MUST NOT be added here!
        alias: {
            api: path.join(__dirname, '../target/src_managed')
        }
    },

    devServer: {
        historyApiFallback: { // Dit moet hetzelfde zijn als config.output.publicPath anders werkt navigatie naar urls niet.
            index: '/mosaic/ui/'
        },
        watchOptions: {aggregateTimeout: 300, poll: 1000}
    },

    node: {
        global: true,
        crypto: 'empty',
        module: false,
        Buffer: false,
        clearImmediate: false,
        setImmediate: false,
    },

    plugins: [
        new webpack.ProvidePlugin({
            'window.jQuery': 'jquery',
            Hammer: 'hammerjs/hammer'
        }),
        new webpack.LoaderOptionsPlugin({
            debug: true
        }),
        new ProgressPlugin(),
        new webpack.optimize.CommonsChunkPlugin({name: ['main', 'vendor', 'polyfills']})
    ],

    entry: {
        'polyfills': ['./src/main/assets/app/polyfills.ts'],
        'vendor': ['./src/main/assets/app/vendor.ts'],
        'main': ['./src/main/assets/app/main.ts']
    },

    module: {
        rules: [
            // .ts files for TypeScript
            {test: /\.ts$/, enforce: 'pre', use: ['tslint-loader']},
            {test: /\.png$/, use: ['file-loader']},
            {test: /\.ts$/, use: ['awesome-typescript-loader', 'angular2-template-loader']},
            /*{ test: /\.css$/, use: ['to-string-loader', 'css-loader'] },*/
            {test: /\.html$/, use: ['raw-loader']},
            {test: /\.json$/, use: ['json-loader']},

            // Support for CSS as raw text
            // use 'null' loader in test mode (https://github.com/webpack/null-loader)
            // all css in src/style will be bundled in an external css file
            {
                test: /\.css$/,
                exclude: helpers.root('src', 'main/assets/app'),
                use: isTest ? ['null-loader'] : ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: [
                        {loader: 'css-loader', options: {sourceMap: true}},
                        'postcss-loader'
                    ]
                })
            },
            // all css required in src/app files will be merged in js files
            {
                test: /\.css$/,
                include: helpers.root('src', 'main/assets/app'),
                use: ['raw-loader', 'postcss-loader']
            },

            // support for .scss files
            // use 'null' loader in test mode (https://github.com/webpack/null-loader)
            // all css in src/style will be bundled in an external css file
            {
                test: /\.scss$/,
                exclude: helpers.root('src', 'main/assets/app'),
                use: isTest ? ['null-loader'] : ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: [
                        {loader: 'css-loader', options: {sourceMap: true}},
                        'postcss-loader',
                        'sass-loader'
                    ]
                })
            },
            // all css required in src/app files will be merged in js files
            {
                test: /\.scss$/,
                exclude: helpers.root('src', 'main/assets/style'),
                use: ['raw-loader', 'postcss-loader', 'sass-loader']
            },
            {
                test: /\.woff2?(\?v=[0-9]\.[0-9]\.[0-9])?$/,
                // Limiting the size of the woff fonts breaks font-awesome ONLY for the extract text plugin
                // loader: "url?limit=10000"
                use: ['url-loader']
            },
            {
                test: /\.(ttf|eot|svg)(\?[\s\S]+)?$/,
                use: ['file-loader']
            }
        ]
    }

};
