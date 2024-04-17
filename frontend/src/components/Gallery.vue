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
  <div class="galleryContainer">
    <h3>Gallery</h3>
    <div v-if="imageList" class="image_container">
      <Image v-for="image in imageList" :id="image.id" @click="utils.gotoImage(image.id)" />
    </div>
  </div>
</template>
