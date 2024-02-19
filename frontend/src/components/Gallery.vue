<script setup lang="ts">
import { ref } from 'vue'
import { getImages, getImage, putImage } from '../http-api'
const imagesData = ref<Array<string>>([])
const file = ref('')
const fileInput = ref<HTMLInputElement | null>(null);
const emit = defineEmits(['imageUpdated'])
const getImagesData = async () => {
    let images = []
    images = await getImages()
    for (const element of images) {
        const image = await getImage('/images/' + element.id);
        const reader = new window.FileReader()
        reader.readAsDataURL(image)
        reader.onload = function () {
            const imageDataUrl = (reader.result as string)
            if (!imagesData.value.includes(imageDataUrl)) {
                imagesData.value.push(imageDataUrl)
            }
        }
    }
};

getImagesData();

async function uploadImage() {
    await putImage(file.value)
    getImagesData()
    if (fileInput.value) {
        fileInput.value.value = ''
    }
    emit("imageUpdated", "msg from child")
}

function handleFileUpload(event: any) {
    file.value = event.target.files[0]
}
</script>

<template>
    <ul v-if="imagesData" class="gallery">
        <li v-for="imageData in imagesData">
            <img :src="imageData" alt="Image" />
        </li>
    </ul>
    <div>
        <h2>Téléverser un fichier</h2>
        <hr />
        <label>Fichier
            <input type="file" @change="handleFileUpload($event)" ref="fileInput" />
        </label>
        <br>
        <button @click="uploadImage()">Envoyer</button>
    </div>
</template>

<style scoped>
.gallery {
    list-style: none;
}
</style>
