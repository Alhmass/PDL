<script setup lang="ts">
import { ref } from 'vue';
import { api } from '@/http-api';
import { ImageType } from '@/image'
import Image from './Image.vue';
import { utils } from '@/utils';
import Upload from './Upload.vue';
import { computeStyles } from '@popperjs/core';

interface SelectedOption {
  id: number;
  name: string;
}
const selected = ref<SelectedOption>({ id: -1, name: '' });
const selectedFilter = ref("");
const imageList = ref<ImageType[]>([]);
const imageToDisplay = ref<ImageType>();
const range = ref<number[]>([]);
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
      if (newImage.name === image.name) {
        imageToDisplay.value = image;
        selected.value.id = image.id;
        selected.value.name = image.name;
      }
    });
  } catch (e) {
    console.log(e);
  }
}

function parse(num: number) {
  let res = "";
  if (num < 0) res = res.concat("-");
  return res.concat(Math.abs(num).toString().padStart(3, '0'));
}

function applyFilter() {
  if (imageToDisplay.value) {
    let numbers;
    if (selectedFilter.value === "colors") {
      const formattedNumbers = range.value.map(num => parse(num));
      numbers = formattedNumbers.join('');
    } else
      numbers = range.value.join('');
    api.getImageFilter(imageToDisplay.value.id, selectedFilter.value, numbers).then((data) => {
      utils.getImages(imageList.value);
      imageToDisplay.value = data[0];
      selectedFilter.value = "";
      selected.value.id = data[0].id;
      selected.value.name = data[0].name;
    }).catch(e => {
      console.log(e.message);
    })
  }
}

function resetRange() {
  if (selectedFilter.value === "GrayLevel")
    range.value = []
  else if (selectedFilter.value === "colors")
    range.value = [0, 0, 0];
  else if (selectedFilter.value === "mean")
    range.value = [1];
  else
    range.value = [0];
}

</script>

<template>
  <div class="filterContainer">
    <div class="imageLoading">
      <div class="selectorContainer">
        <h3>Choose an image</h3>
        <select v-model="selected" @change="loadImage()">
          <option disabled value="">Choose an image</option>
          <option v-for="image in imageList" :value="{ id: image.id, name: image.name }" :key="image.id">{{ image.name
            }}
          </option>
        </select>
      </div>
      <h3 class="ortext">OR</h3>
      <Upload @response="(image) => imageUploaded(image)" :showDiv="false" />
    </div>
    <hr />
    <div v-if="imageToDisplay" class="editorContainer">
      <div class="toolboxContainer">
        <h3>Filter to apply</h3>
        <select v-model="selectedFilter" @change="resetRange()">
          <option disabled value="">Filter</option>
          <option value="GrayLevel">GrayLevel</option>
          <option value="brightness">Brightness</option>
          <option value="mean">Mean</option>
          <option value="colors">Colors</option>
        </select>
        <div v-if="selectedFilter" class="filterContainer">
          <div v-if='selectedFilter === "colors"' class="sliderColors">
            <div class="sliderContainer">
              <span class="value-display" id="redValueDisplay">{{ range[0] }}</span>
              <input v-model="range[0]" type="range" id="redRange" min="-255" max="255" value="0">
              <label for="redRange" class="slider-descr">R</label>
            </div>
            <div class="sliderContainer">
              <span class="value-display" id="greenValueDisplay">{{ range[1] }}</span>
              <input v-model="range[1]" type="range" id="greenRange" min="-255" max="255" value="0">
              <label for="greenRange" class="slider-descr">G</label>
            </div>
            <div class="sliderContainer">
              <span class="value-display" id="blueValueDisplay">{{ range[2] }}</span>
              <input v-model="range[2]" type="range" id="blueRange" min="-255" max="255" value="0">
              <label for="blueRange" class="slider-descr">B</label>
            </div>
          </div>
          <div v-else-if='selectedFilter === "brightness"' class="brightSlider">
            <div class="sliderContainer">
              <span class="value-display" id="brightValueDisplay">{{ range[0] }}</span>
              <input v-model="range[0]" type="range" id="brightSlider" name="brightSlider" min="0" max="255"
                value="0" />
              <label for="brightSlider" class="slider-descr">{{ selectedFilter }}</label>
            </div>
          </div>
          <div v-else-if='selectedFilter === "mean"' class="meanSlider">
            <div class="sliderContainer">
              <span class="value-display" id="meanValueDisplay">{{ range[0] }}</span>
              <input v-model="range[0]" type="range" id="meanSlider" name="meanSlider" min="1" max="21" step="2"
                value="1" />
              <label for="meanSlider" class="slider-descr">{{ selectedFilter }}</label>
            </div>
          </div>
        </div>
        <button v-if="selectedFilter" @click="applyFilter()">Apply</button>
      </div>
      <div class="image_containers">
        <Image :key="imageToDisplay.id" :id="imageToDisplay.id" @click="utils.gotoImage(imageToDisplay.id)" />
        <div>
          <button @click="downloadImage()">Download</button>
        </div>
      </div>
    </div>
  </div>
</template>