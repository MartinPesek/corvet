<template>
  <div>
    <div
      v-if="$store.state.urls == null || $store.state.urls.length == 0"
      class="alert alert-info"
    >No uploads yet.</div>
    <div v-if="$store.state.urls != null && $store.state.urls.length > 0">
      <table class="table">
        <thead>
          <tr>
            <th>#</th>
            <th>Image</th>
            <th>Url</th>
            <th></th>
          </tr>
        </thead>
        <t2 tag="tbody" group>
          <tr v-for="(u, i) in $store.state.urls" :key="u.url">
            <td>{{ i + 1 }}</td>
            <td>
              <img :src="u.url" class="img-responsive">
            </td>
            <td>{{ u.url }}</td>
            <td>
              <button
                type="button"
                class="btn btn-primary btn-xs"
                @click.prevent="copyToClipboard(u.url)"
              >Copy</button>
            </td>
          </tr>
        </t2>
      </table>
      <button type="button" class="btn btn-primary" @click.prevent="clearAllUrls()">Clear all</button>
    </div>
  </div>
</template>
<script>
  import Vue from 'vue'
  import VueClipboard from 'vue-clipboard2'

  VueClipboard.config.autoSetContainer = true
  Vue.use(VueClipboard)

  import { SlideYUpTransition } from 'vue2-transitions'
  Vue.component('t2', SlideYUpTransition)

  export default {
    methods: {
      copyToClipboard (url) {
        let self = this
        this.$copyText(url).then(
          function () {
            self.$store.commit('notify', [
              'success',
              `Url ${url} copied to clipboard.`
            ])
          },
          function () {
            self.$store.commit('notify', [
              'success',
              'Url copying to clipboard failed.'
            ])
          }
        )
      },
      clearAllUrls () {
        this.$store.commit('clearUrls')
        this.$store.commit('notify', [
          'success',
          'Urls trashed in history like hentai.'
        ])
      }
    }
  }
</script>
