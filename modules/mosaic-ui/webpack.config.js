var ENV = process.env.npm_lifecycle_event;
var isProd = ENV === 'build';

function getWebpackConfig() {
    if (isProd) {
        return require('./config/webpack.prod.js')
    } else {
        return require('./config/webpack.dev.js')
    }
}

module.exports = getWebpackConfig();
