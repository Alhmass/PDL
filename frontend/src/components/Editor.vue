<script setup lang="ts">
import { ref } from 'vue';
import { api } from '@/http-api';
import { ImageType } from '@/image'
import Image from './Image.vue';
import { utils } from '@/utils';
import Upload from './Upload.vue';

interface SelectedOption {
  id: number;
  name: string;
}
const selected = ref<SelectedOption>({ id: -1, name: '' });
const selectedFilter = ref("");
const imageList = ref<ImageType[]>([]);
const imageToDisplay = ref<ImageType>();
const redRange = ref(0);
const blueRange = ref(0);
const greenRange = ref(0);
const otherRange = ref(0);
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
function loadImage() {
  imageList.value.forEach(image => {
    if (image.id === selected.value.id) {
      imageToDisplay.value = image;
    }
  });
}
async function imageUploaded(newImage: File) {
  try {
    await utils.getImages(imageList.value);
    imageList.value.forEach(image => {
      if (newImage.name === image.name)
        imageToDisplay.value = image;
    });
  } catch (e) {
    console.log(e);
  }
}
</script>

<template>
  <div class="imageLoading">
    <div class="selectorContainer">
      <h3>Choose an image</h3>
      <select v-model="selected" @change="loadImage()">
        <option disabled value="">Choose an image</option>
        <option v-for="image in imageList" :value="{ id: image.id, name: image.name }" :key="image.id">{{ image.name }}
        </option>
      </select>
    </div>
    <h3 class="ortext">OR</h3>
    <Upload @response="(image) => imageUploaded(image)" />
  </div>
  <hr />
  <div v-if="imageToDisplay" class="editorContainer">
    <div class="toolboxContainer">
      <h3>Filter to apply</h3>
      <select v-model="selectedFilter">
        <option disabled value="">Filter</option>
        <option value="GrayLevel">GrayLevel</option>
        <option value="brightness">Brightness</option>
        <option value="mean">Mean</option>
        <option value="colors">Colors</option>
      </select>
      <div v-if="selectedFilter" class="filterContainer">
        <div v-if='selectedFilter === "colors"' class="sliderColors">
          <div>
            <span class="value-display" id="redValueDisplay">{{ redRange }}</span>
            <input v-model="redRange" type="range" id="redRange" min="0" max="255" value="0">
            <label for="redRange">R</label>
          </div>
          <div>
            <span class="value-display" id="greenValueDisplay">{{ greenRange }}</span>
            <input v-model="greenRange" type="range" id="greenRange" min="0" max="255" value="0">
            <label for="greenRange">G</label>
          </div>
          <div>
            <span class="value-display" id="blueValueDisplay">{{ blueRange }}</span>
            <input v-model="blueRange" type="range" id="blueRange" min="0" max="255" value="0">
            <label for="blueRange">B</label>
          </div>
        </div>
        <div v-else class="otherSlider">
          <div>
            <span class="value-display" id="otherValueDisplay">{{ otherRange }}</span>
            <input v-model="otherRange" type="range" id="oslider" name="oslider" min="0" max="100" value="0" />
            <label for="oslider">{{ selectedFilter }}</label>
          </div>
        </div>
      </div>
      <button v-if="selectedFilter">Apply</button>
    </div>
    <div class="image_container">
      <Image :key="imageToDisplay.id" :id="imageToDisplay.id" @click="utils.gotoImage(imageToDisplay.id)" />
      <div><button @click="downloadImage()">Download</button></div>
    </div>
  </div>
</template>

<style scoped>
button {
  cursor: pointer;
}

.editorContainer {
  display: flex;
  justify-content: center;
  padding: 0 2em;
}

.toolboxContainer {
  width: 50%;
}

.sliderColors {
  display: flex;
  flex-direction: column;
  padding: 0 2em;
}

.image_container {
  display: flex;
  width: 50%;
  justify-content: center;
  margin: 0 auto;
  flex-direction: column;
  padding: 0 2em;
}

.image_container:deep(figure img) {
  cursor: pointer;
  width: 100%;
  max-height: 500px;
}

.imageLoading {
  display: flex;
  justify-content: center;
  padding: 0 2em;
}
</style>