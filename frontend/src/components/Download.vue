<script setup lang="ts">
import { ref } from 'vue';
import { api } from '@/http-api';
import { ImageType } from '@/image'
import { blob } from 'stream/consumers';
const selected = ref('');
const imageList = ref<ImageType[]>([]);
getImageList();
function getImageList() {
  api.getImageList().then((data) => {
    imageList.value = data;
  }).catch(e => {
    console.log(e.message);
  });
}
function downloadImage(selectedImage: any) {
    api.getImage(selectedImage.id).then((data) => {
        const response = data;
        const reader = new window.FileReader();
        reader.readAsDataURL(response);
        reader.onload = function () {
            const attribute = URL.createObjectURL(response);
            const hdata = document.createElement('a');
            hdata.href = attribute;
            hdata.setAttribute('download', selectedImage.name);
            document.body.appendChild(hdata);
            hdata.click();
            URL.revokeObjectURL(attribute);
  }
    }).catch(e => {
        console.log(e.message);
    });
}

</script>

<template>
  <div>
    <h3>Download an image</h3>
    <select v-model="selected">
    <option disabled value="">Selectionner une image</option>
    <option v-for="image in imageList" :value="{ id: image.id, name: image.name }" :key="image.id">{{ image.name }}</option>
  </select>
  <button v-if="selected" @click="downloadImage(selected)">Télécharger</button>
  <button v-else disabled>Télécharger</button>
  </div>
</template>

<style scoped>
</style>
