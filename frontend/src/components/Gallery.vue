<script setup lang="ts">
import { ref } from 'vue'
import { api } from '@/http-api';
import { ImageType } from '@/image'
import Image from './Image.vue';
import router from '@/router';

const imageList = ref<ImageType[]>([]);

api.getImageList()
  .then((data) => {
    imageList.value = data;
  })
  .catch(e => {
    console.log(e.message);
  });

function gotoImage(imageID:number) {
  router.push({ name: 'image', params: { id: imageID } })
}
</script>

<template>
  <div>
    <h3>Gallery</h3>
    <div v-if="imageList" class="image_container">
      <Image v-for="image in imageList" :id="image.id" class="vignette" @click="gotoImage(image.id)"/>
    </div>
  </div>
</template>

<style scoped>
  .vignette{
    cursor: pointer;
    width: 250px;
    height: 250px;
    margin: 0 auto;
  }
  .image_container{
    display: flex;
  }
</style>
