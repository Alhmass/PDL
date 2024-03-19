<script setup lang="ts">
import { ref } from 'vue';
import { api } from '@/http-api';
import { ImageType } from '@/image'
import { blob } from 'stream/consumers';
import Image from './Image.vue'
import router from '@/router';

interface SelectedOption {
  id: number;
  name: string;
}
const selected = ref<SelectedOption>({ id: -1, name: '' });
const imageList = ref<ImageType[]>([]);
getImageList();
function getImageList() {
  api.getImageList().then((data) => {
    imageList.value = data;
  }).catch(e => {
    console.log(e.message);
  });
}
function downloadImage() {
    api.getImage(selected.value.id).then((data) => {
        const response = data;
        const reader = new window.FileReader();
        reader.readAsDataURL(response);
        reader.onload = function () {
            const attribute = URL.createObjectURL(response);
            const hdata = document.createElement('a');
            hdata.href = attribute;
            hdata.setAttribute('download', selected.value.name);
            document.body.appendChild(hdata);
            hdata.click();
            URL.revokeObjectURL(attribute);
  }
    }).catch(e => {
        console.log(e.message);
    });
}

function deleteImage() {
    api.deleteImage(selected.value.id).then((data) => {
        getImageList();
        let imageEl = document.querySelector(".preview");
        imageEl?.setAttribute("src", '');
    }).catch(e => {
        console.log(e.message);
    })
}
function loadImage() {
    api.getImage(selected.value.id).then((data) => { 
        let imageEl = document.querySelector(".preview")
        const reader = new window.FileReader();
        reader.readAsDataURL(data);
        reader.onload = function () {
        const imageDataUrl = (reader.result as string);
        imageEl?.setAttribute("src", imageDataUrl);
        }
    }).catch(e => {
        console.log(e.message);
    });
}
function gotoImage() {
    router.push({ name: 'image', params: { id: selected.value.id } })
}
</script>

<template>
  <div>
    <h3>Choose an image</h3>
    <select v-model="selected" @change="loadImage()">
      <option disabled value="">Selectionner une image</option>
      <option v-for="image in imageList" :value="{ id: image.id, name: image.name }" :key="image.id">{{ image.name }}</option>
    </select>
    <button v-if="selected" @click="downloadImage()">Télécharger</button>
    <button v-else disabled>Télécharger</button>
    <button v-if="selected" @click="deleteImage()">Supprimer</button>
    <button v-else disabled>Supprimer</button>
    <hr />
    <a v-if="selected" href="#">
      <img class="preview" src="" @click="gotoImage()">
    </a>
  </div>
</template>

<style scoped>
</style>