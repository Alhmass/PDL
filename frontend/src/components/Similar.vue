<script setup lang="ts">
import { ref } from 'vue';
import { defineProps } from 'vue';
import { api } from '@/http-api';
import { ImageSimilarType, ImageType } from '@/image'
import Image from './Image.vue';
import router from '@/router';

const props = defineProps<{ id: number }>()

const imageList = ref<ImageSimilarType[]>([]);
const tags = ref<string[]>([]);
const nbInput = ref(1);

const maxImage = ref(0);

const inputTagVal = ref('');

interface SelectedDescr {
  name: string;
}
const descriptor = ref<SelectedDescr>({ name: '' });
const tagSelect = ref("");
function showSimilar() {
  let nbImages = parseInt((document.getElementById("nbImages") as HTMLInputElement).value);
  api.getImageListSimilar(props.id, descriptor.value.name, nbImages, tagSelect?.value)
    .then((data) => {
      imageList.value = data;
    })
    .catch(e => {
      console.log(e.message);
    });
}

function inc() {
  if(nbInput.value < maxImage.value) nbInput.value += 1;
}
function dec() {
  if(nbInput.value > 1) nbInput.value -= 1;
}

api.getImage(props.id)
  .then((data: Blob) => {
    const reader = new window.FileReader();
    reader.readAsDataURL(data);
    reader.onload = () => {
      const galleryElt = document.getElementById("gallery-" + props.id);
      if (galleryElt !== null) {
        const imgElt = document.createElement("img");
        galleryElt.appendChild(imgElt);
        if (imgElt !== null && reader.result as string) {
          imgElt.setAttribute("src", (reader.result as string));
        }
      }
    };
  })
  .catch(e => {
    console.log(e.message);
  });

api.getImageList()
  .then((data) => {
    maxImage.value = data.length - 1;
  })
  .catch(e => {
    console.log(e.message);
  });



api.getImageTags(props.id)
  .then((data) => {
    tags.value = data;
  })
  .catch(e => {
    console.log(e.message);
  })

function modifyTags() {
  let formData = new FormData();
  formData.append("tags", tags.value.join('@'));
  inputTagVal.value = "";
  api.editTags(props.id, formData)
    .then((data) => {
    })
    .catch(e => {
      console.log(e.message);
    })
}

function add_tag() {
  if (inputTagVal.value.length !== 0) {
    if (!tags.value.includes(inputTagVal.value)) {
      tags.value.push(inputTagVal.value);
      modifyTags();
    }
  }
}
function delete_tag(tagToRemove: string) {
  tags.value = tags.value.filter((element) => element !== tagToRemove);
  modifyTags();
}
</script>

<template>
  <figure :id="'gallery-' + id"></figure>
  <hr />
  <div class="similarContainer">
    <h3>Show similar images</h3>
    <div class="ParamSelectors">
      <select v-model="descriptor">
        <option disabled value="">Select a descriptor</option>
        <option :value="{ name: 'histogram2D' }">Histogram 2D Hue/Saturation</option>
        <option :value="{ name: 'histogram3D' }">Histogram 3D RGB</option>
        <option :value="{ name: 'tags' }">Tags</option>
      </select>
      <div class="inputContainer">
        <span class="decrement" @click="dec()">-</span>
        <input  v-model="nbInput" type="number" id="nbImages" min="1" :max="maxImage" />
        <span class="increment" @click="inc()">+</span>
      </div>
      <select v-if="descriptor.name == 'tags' && tags.length > 1" v-model="tagSelect">
        <option disabled value="">Select a tag</option>
        <option v-for="tag in tags" :value="tag" :key="tag">{{ tag }}
        </option>
      </select>
      <button v-if="descriptor && maxImage >= 1 && descriptor.name !== 'tags'" @click="showSimilar()">View</button>
      <button v-else-if="descriptor.name === 'tags' && tags.length > 1" @click="showSimilar()">View</button>
      <button v-else disabled>View</button>
      <span v-if="maxImage < 1">No other image found on the server!</span>
      <span v-else-if="descriptor.name === 'tags' && tags.length <= 1">This image doesn't have any tag!</span>
    </div>
    <hr />
    <div v-if="descriptor" class="image_container">
      <div v-for="image in imageList">
        <Image :id="image.id" />
        <p v-if="descriptor.name !== 'tags'" class="similar_score">score : {{ Math.round(image.similar_score) }}</p>
      </div>
    </div>
  </div>
  <div class="tagsContainer">
    <ul class="tagList" v-if="tags">
      <li class="tag" v-for="tag in tags">
        <span class="tagText">{{ tag }}</span>
        <span class="deleteIcon" @click="delete_tag(tag)">&times;</span>
      </li>
    </ul>
    <input v-model="inputTagVal" type="text" class="taginput" @keyup.enter="add_tag()">
    <button @click="add_tag()">modify</button>
  </div>
</template>
