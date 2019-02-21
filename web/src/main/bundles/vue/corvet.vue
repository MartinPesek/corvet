<template>
  <div>
    <loading :active="$store.state.working" type="rotating-square" :fullscreen="true"></loading>
    <div class="row">
      <div class="col-md-6">
        <h2>Uploader</h2>
        <div class="help-block">Currently works in Firefox, Chrome and Edge</div>
        <uploader @file-uploaded="fileUploaded"></uploader>
      </div>
      <div class="col-md-6">
        <h2>History</h2>
        <div class="help-block">History of uploads</div>
        <history></history>
      </div>
    </div>
    <notify :event-bus="$store.state.bus" position="top-right" :duration="5000">
      <template slot="content" slot-scope="{data}">
        <p>{{ data.text }}</p>
      </template>
    </notify>
  </div>
</template>
<script>
  import Vue from 'vue'
  import { Loading, Notify } from 'vue-nani-kore'

  import Uploader from './_uploader.vue'
  import History from './_history.vue'

  import VueClipboard from 'vue-clipboard2'
  VueClipboard.config.autoSetContainer = true
  Vue.use(VueClipboard)

  export default {
    components: {
      Loading,
      Notify,
      Uploader,
      History
    },
    methods: {
      fileUploaded (url) {
        let self = this
        this.url = url
        this.$store.commit('addUrl', url)

        this.$copyText(url).then(
          function () {
            self.$store.commit('notify', [
              'success',
              `File uploaded and url ${url} copied to clipboard.`
            ])
          },
          function () {
            self.$store.commit('notify', [
              'success',
              'File uploaded and copying to clipboard failed.'
            ])
          }
        )
      }
    }
  }
</script>
