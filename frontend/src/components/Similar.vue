<script setup lang="ts">
import { ref } from 'vue';
import { defineProps } from 'vue';
import { api } from '@/http-api';
import { ImageType } from '@/image'
import Image from './Image.vue';
import router from '@/router';

const props = defineProps<{ id: number }>()

const imageList = ref<ImageType[]>([]);

interface SelectedDescr {
  name: string;
}
const descriptor = ref<SelectedDescr>({ name: '' });
function showSimilar() {
	let nbImages = parseInt((document.getElementById("idImages") as HTMLInputElement).value);
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
      const galleryElt = document.getElementById("gallery-"+props.id);
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

function gotoImage(imageID:number) {
  router.push({ name: 'image', params: { id: imageID } })
}
</script>

<template>
  <figure :id="'gallery-'+id"></figure>  
  <hr />
  <h3>Show similar images</h3>
    <select v-model="descriptor">
      <option disabled value="">Selectionner un descripteur</option>
	  <option :value="{name : 'histogram2D'}">Histogramme 2D Teinte/Saturation</option>
	  <option :value="{name : 'histogram3D'}">Histogramme 3D RGB</option>
    </select>
	<input type="number" id="nbImages" placeholder="Nombre d'images à afficher" min="1"/>
    <button v-if="descriptor" @click="showSimilar()">Afficher</button>
    <button v-else disabled>Afficher</button>
	<hr />
	<div v-if="descriptor" class="image_container">
      <Image v-for="image in imageList" :id="image.id" class="vignette" @click="gotoImage(image.id)"/>
    </div>
</template>

<style>
	.vignette {
		cursor: pointer;
	}
</style>