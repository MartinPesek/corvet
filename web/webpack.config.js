const webpack = require('webpack')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
let dev = !(process.env.NODE_ENV === 'production')
const { VueLoaderPlugin } = require('vue-loader')
const OptimizeCssAssetsPlugin = require('optimize-css-assets-webpack-plugin')

module.exports = {
    entry: {
        corvet: ['babel-polyfill', './src/main/bundles/js/_bootstrap.js', 'bootstrap-loader/lib/bootstrap.loader?configFilePath=../../../.bootstraprc!bootstrap-loader/no-op.js']
    },
    output: {
        path: __dirname + '/src/main/resources/public/assets/',
        publicPath: './assets/',
        filename: '[name].js'
    },
    resolve: {
        extensions: ['.js', '.vue'],
        alias: {
            'vue$': 'vue/dist/vue.esm.js'
        }
    },
    module: {
        rules: [
            {
                test: /\.vue$/,
                use: 'vue-loader'
            },
            {
                test: /\.(css|sass|scss)$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    'css-loader',
                    'resolve-url-loader?sourceMap',
                    'sass-loader?sourceMap',
                ]
            },
            {
                test: /\.js$/,
                use: 'babel-loader',
                exclude: /node_modules/
            },            
            {
                test: /\.(eot|woff|woff2|ttf|svg|otf|png|jpe?g|gif|htc)$/,                
                use: dev ? 'file-loader?name=[name].[ext]&publicPath=/assets/' : 'file-loader?name=[name]-[md5:hash:base64:10].[ext]&publicPath=/assets/'
            },
            {
                test: /\.(jpe?g|png|gif|svg)$/i,
                use: [
                    'img-loader'
                ]
            }
        ]
    },
    plugins: [
        new webpack.ProvidePlugin({
            $: 'jquery',
            jQuery: 'jquery'
        }),
        new MiniCssExtractPlugin({
            filename: '[name].css'
        }),                
        new VueLoaderPlugin()
    ],
    performance: {
        hints: false
    },
    devtool: dev ? '#eval-source-map' : false
}

if (!dev) {
    module.exports.plugins = (module.exports.plugins || []).concat([
        new webpack.LoaderOptionsPlugin({
            minimize: true
        }),
        new OptimizeCssAssetsPlugin()
    ])
}