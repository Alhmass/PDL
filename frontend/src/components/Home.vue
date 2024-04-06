<script setup lang="ts">
import { ref } from 'vue';
import { api } from '@/http-api';
import { ImageType } from '@/image'
import Image from './Image.vue';
import { utils } from '@/utils';

interface SelectedOption {
  id: number;
  name: string;
}
const selected = ref<SelectedOption>({ id: -1, name: '' });
const imageList = ref<ImageType[]>([]);
const imageToDisplay = ref<ImageType[]>([]);
const searchQuery = ref("");
utils.getImages(imageList.value);
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
    imageToDisplay.value = imageToDisplay.value.filter(item => item.id !== selected.value.id);
    imageList.value = imageList.value.filter(item => item.id !== selected.value.id);
    selected.value.id = -1;
    selected.value.name = '';
  }).catch(e => {
    console.log(e.message);
  })
}
function loadImage() {
  imageToDisplay.value.splice(0, imageToDisplay.value.length)
  imageList.value.forEach(image => {
    if (image.id === selected.value.id) {
      imageToDisplay.value.push(image);
    }
  });
}

function loadImageByTag() {
  if (searchQuery.value !== "") {
    api.getImageByTags(searchQuery.value).then((data) => {
      imageToDisplay.value = data;
      selected.value.id = -1;
      selected.value.name = '';
    }).catch(e => {
      console.log(e.message);
    })
  }
}
</script>

<template>
  <div class="searchByTag">
    <h3>Search image by tag</h3>
    <div class="searchContainer">
      <input v-model="searchQuery" type="search" class="searchBar" placeholder="Search..."
        @keyup.enter="loadImageByTag()">
      <button type="submit" class="searchBtn" @click="loadImageByTag()">search</button>
    </div>
  </div>
  <div>
    <h3>Choose an image</h3>
    <select v-model="selected" @change="loadImage()">
      <option disabled value="">Selectionner une image</option>
      <option v-for="image in imageList" :value="{ id: image.id, name: image.name }" :key="image.id">{{ image.name }}
      </option>
    </select>
    <button v-if="selected" @click="downloadImage()">Télécharger</button>
    <button v-else disabled>Télécharger</button>
    <button v-if="selected" @click="deleteImage()">Supprimer</button>
    <button v-else disabled>Supprimer</button>
    <hr />
  </div>
  <div v-if="imageToDisplay" class="image_container">
    <Image v-for="image in imageToDisplay" :key="image.id" :id="image.id" @click="utils.gotoImage(image.id)" />
  </div>
</template>

<style scoped>
button {
  cursor: pointer;
}

.image_container {
  display: flex;
  max-width: 100%;
  justify-content: center;
  margin: 0 auto;
  flex-wrap: wrap;
  padding: 0 2em;
}

.image_container:deep(figure img) {
  cursor: pointer;
  width: auto;
  max-height: 500px;
}
</style>