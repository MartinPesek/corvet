// eslint-disable-next-line no-global-assign
$ = window.jQuery = window.$ = require('jquery')

import Ready from 'document-ready'
import Vue from 'vue'
import Vuex from 'vuex'
import VuexPersistence from 'vuex-persist'

import Corvet from '../vue/corvet.vue'

Vue.use(Vuex)

let bus = new Vue()

const store = new Vuex.Store({
    state: {
        working: false,
        bus: bus,
        urls: []
    },
    mutations: {
        working (state, status) {
            state.working = status
        },
        notify (state, args) {
            state.bus.$emit('show-notification', { type: args[0], data: { text: args[1] } })
        },
        addUrl (state, url) {
            state.urls.push({ url: url })
        },
        clearUrls (state) {
            state.urls = []
        }
    },
    plugins: [new VuexPersistence({ reducer: (state) => ({ urls: state.urls }) }).plugin]
})

Ready(function () {
    if (document.getElementById('corvet')) {
        new Vue({
            store,
            el: '#corvet',
            render: corvet => corvet(Corvet)
        })
    }
})