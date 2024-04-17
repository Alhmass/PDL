<script setup lang="ts">
</script>

<template>
  <div id="app">
    <nav>
      <ul>
        <li>
          <router-link to="/">Home</router-link>
        </li>
        <li>
          <router-link to="/gallery">Gallery</router-link>
        </li>
        <li>
          <router-link to="/upload">Upload</router-link>
        </li>
        <li>
          <router-link to="/editor">Editor</router-link>
        </li>
      </ul>
    </nav>

    <div>
      <h1>PDL - L3</h1>
      <router-view />
    </div>
  </div>
</template>
