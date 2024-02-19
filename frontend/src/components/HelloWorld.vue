<script setup lang="ts">
import { ref } from 'vue'
import { getImage } from '../http-api'
const selected = ref('')
//let imageEl = document.querySelector("img");

defineProps<{ msg: string, images: Array<any> }>()
async function downloadImage(selectedOption: any): Promise<void> {
  let imageUrl = '/images/' + selectedOption.id
  let imageEl = document.querySelector(".preview")
  const response = await getImage(imageUrl)
  const reader = new window.FileReader();
  reader.readAsDataURL(response);
  reader.onload = function () {
    const imageDataUrl = (reader.result as string);
    imageEl?.setAttribute("src", imageDataUrl)
    const attribute = URL.createObjectURL(response);
    const hdata = document.createElement('a');
    hdata.href = attribute;
    hdata.setAttribute('download', selectedOption.name);
    document.body.appendChild(hdata);
    hdata.click();
    URL.revokeObjectURL(attribute);
  }
}

</script>

<template>
  <h1>{{ msg }}</h1>
  <select v-if="images" v-model="selected">
    <option disabled value="">Selectionner une image</option>
    <option v-for="image in images" :value="{ id: image.id, name: image.name }">{{ image.name }}</option>
  </select>
  <button v-if="selected" @click="downloadImage(selected)">Télécharger</button>
  <button v-else disabled>Télécharger</button>
  <img v-if="selected" class="preview" src="">
</template>

<style scoped></style>
