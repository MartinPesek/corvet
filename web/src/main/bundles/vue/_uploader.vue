<template>
  <div>
    <div ref="rte" contenteditable="true" @paste="pasteContent" class="well"></div>
    <div class="checkbox">
      <label>
        <input id="uploader-hack" type="checkbox" v-model="hack"> HACK!
      </label>
    </div>
  </div>
</template>
<script>
  import Vue from 'vue'
  import axios from 'axios'

  export default {
    data () {
      return {
        hack: false
      }
    },
    mounted () {
      $(this.$refs.rte).focus()
    },
    methods: {
      pasteContent (e) {
        var imageData
        let self = this

        if (
          e &&
          e.clipboardData &&
          e.clipboardData.items &&
          e.clipboardData.items.length > 0
        ) {
          let item = e.clipboardData.items[0]

          if (item.type == 'text/html') {
            setTimeout(function () {
              self.uploadImage(null)
            }, 0)

            return
          }

          if (
            item.type != 'image/png' &&
            item.type != 'image/jpeg' &&
            item.type != 'image/gif'
          ) {
            self.$store.commit('notify', [
              'danger',
              `Not supported type: ${item.type}`
            ])
            return
          }

          let fr = new FileReader()

          fr.onload = function (fre) {
            imageData = fre.target.result
            self.uploadImage(imageData)
          }

          fr.readAsDataURL(item.getAsFile())
        } else {
          setTimeout(function () {
            self.uploadImage(null)
          }, 0)
        }
      },
      uploadImage (data) {
        let self = this

        if (data == null) {
          let rteImage = $(this.$refs.rte).find('img')
          data = rteImage.attr('src')
          rteImage.remove()
        }

        if (data == null) {
          self.$store.commit('notify', ['danger', 'No data loaded!'])
          return
        }

        self.$store.commit('working', true)

        let form = new FormData()

        if (self.hack) {
          form.append('data', '1')
        } else {
          form.append('data', data)
        }

        axios
          .post('/', form, { headers: { 'content-type': 'multipart/form-data' } })
          .then(response => {
            self.$store.commit('working', false)

            if (!self.hack) {
              self.$emit('file-uploaded', response.data.url)
            }
          })
          .catch(function (error) {
            self.$store.commit('working', false)
            // hack: only first message is being added
            self.$store.commit('notify', [
              'danger',
              error.response.data.failures[0].message
            ])
            try {
              self.errors = error.response.data
            } catch (e) {
              console.log('ERROR', e)
            }
          })
      }
    }
  }
</script>
