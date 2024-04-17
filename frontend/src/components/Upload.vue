<script setup lang="ts">
import { ref } from 'vue';
import { api } from '@/http-api';

const target = ref<HTMLInputElement>();
const emit = defineEmits(['response']);
const tags = ref<string>("");
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
      formData.append("tags", tags.value);
    tags.value = "";
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
  if (!tags.value.includes(inputTagVal.value)) {
    if (tags.value)
      tags.value = tags.value + '@' + inputTagVal.value;
    else
      tags.value = inputTagVal.value;
  }
  inputTagVal.value = "";
}
</script>

<template>
  <div class="uploadContainer">
    <h3>Upload an image</h3>
    <div class="fileInput">
      <input type="file" id="file" ref="file" @change="handleFileUpload" />
    </div>
    <div>
      <div v-if="target?.value && props.showDiv" class="tags">
        <div class="tagtextarea">
          <span v-if="tags" class="tagview">{{ tags }}</span>
          <input v-model="inputTagVal" type="text" class="taginput" @keyup.enter="add_tag()">
        </div>
        <button @click="add_tag()">add tag</button>
      </div>
      <button v-if="target?.value" @click="submitFile">Submit</button>
      <button v-else disabled>Submit</button>
    </div>
  </div>
</template>
