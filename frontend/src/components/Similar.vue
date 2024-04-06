<script setup lang="ts">
import { ref } from 'vue';
import { defineProps } from 'vue';
import { api } from '@/http-api';
import { ImageSimilarType, ImageType } from '@/image'
import Image from './Image.vue';
import router from '@/router';

const props = defineProps<{ id: number }>()

const imageList = ref<ImageSimilarType[]>([]);

const maxImage = ref(0);

interface SelectedDescr {
  name: string;
}
const descriptor = ref<SelectedDescr>({ name: '' });
function showSimilar() {
  let nbImages = parseInt((document.getElementById("nbImages") as HTMLInputElement).value);
  api.getImageListSimilar(props.id, descriptor.value.name, nbImages)
    .then((data) => {
      imageList.value = data;
    })
    .catch(e => {
      console.log(e.message);
    });
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
</script>

<template>
  <figure :id="'gallery-' + id"></figure>
  <hr />
  <h3>Show similar images</h3>
  <select v-model="descriptor">
    <option disabled value="">Selectionner un descripteur</option>
    <option :value="{ name: 'histogram2D' }">Histogramme 2D Teinte/Saturation</option>
    <option :value="{ name: 'histogram3D' }">Histogramme 3D RGB</option>
  </select>
  <input type="number" id="nbImages" placeholder="Nombre d'images à afficher" min="1" :max="maxImage" />
  <button v-if="descriptor && maxImage >= 1" @click="showSimilar()">Afficher</button>
  <button v-else disabled>Afficher</button>
  <p v-if="maxImage < 1">Aucune autre image n'est présente sur le serveur</p>
  <hr />
  <div v-if="descriptor" class="image_container">
    <div v-for="image in imageList">
      <Image :id="image.id" />
      <p class="similar_score">score : {{ Math.round(image.similar_score) }}</p>
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
}
</style>