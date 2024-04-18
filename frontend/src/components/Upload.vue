<script setup lang="ts">
import { ref } from 'vue';
import { api } from '@/http-api';

const target = ref<HTMLInputElement>();
const emit = defineEmits(['response']);
const tags = ref<string[]>([]);
const props = defineProps({ showDiv: { type: Boolean, default: true } });
const inputTagVal = ref("");
function submitFile() {
  if (target.value !== null && target.value !== undefined && target.value.files !== null) {
    const file = target.value.files[0];
    if (file === undefined)
      return;
    let formData = new FormData();
    formData.append("file", file);
    if (tags.value.length > 1)
      formData.append("tags", tags.value.join('@'));
    tags.value = [];
    api.createImage(formData).then(() => {
      if (target.value !== undefined)
        target.value.value = '';
      emit('response', file);
    }).catch(e => {
      console.log(e.message);
    });
  }
}

function handleFileUpload(event: Event) {
  target.value = (event.target as HTMLInputElement);
}

function add_tag() {
  if (inputTagVal.value.length !== 0) {
    if (!tags.value.includes(inputTagVal.value)) {
      tags.value.push(inputTagVal.value);
      inputTagVal.value = "";
    }
  }
}
function delete_tag(tagToRemove: string) {
  tags.value = tags.value.filter((element) => element !== tagToRemove);
}
</script>

<template>
  <div class="uploadContainer">
    <h3>Upload an image</h3>
    <div class="fileInputContainer">
      <input type="file" id="file" ref="file" @change="handleFileUpload" />
    </div>
    <div>
      <div v-if="target?.value && props.showDiv" class="tags">
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
      </div>
      <button v-if="target?.value" @click="submitFile">Submit</button>
      <button v-else disabled>Submit</button>
    </div>
  </div>
</template>
