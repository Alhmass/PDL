<script setup lang="ts">
import { ref } from 'vue'
import { api } from '@/http-api';
import { ImageType } from '@/image'
import Image from './Image.vue';
import { utils } from '@/utils'

const imageList = ref<ImageType[]>([]);
utils.getImages(imageList.value);
</script>

<template>
  <div>
    <h3>Gallery</h3>
    <div v-if="imageList" class="image_container">
      <Image v-for="image in imageList" :id="image.id" @click="utils.gotoImage(image.id)" />
    </div>
  </div>
</template>

<style scoped>
.image_container {
  display: flex;
  max-width: 100%;
  justify-content: center;
  margin: 0 auto;
  flex-wrap: wrap;
  padding: 0 1em;
}

.image_container:deep(figure img) {
  width: 250px;
  height: 250px;
  cursor: pointer;
}
</style>
