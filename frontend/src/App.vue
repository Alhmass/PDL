<script setup lang="ts">

function toggleBurgerMenu() {
  //switch the value of display in class burger-nav
  const burgerNav = document.querySelector('.burger-nav');
  if (burgerNav) {
    burgerNav.classList.toggle('active');
  }
}
</script>

<template>
  <div id="app">
    <nav class="header">
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

    <div class="burger-menu">
      <div class="burger-icon" @click="toggleBurgerMenu()">
        <label for="checkbox">
          <input type="checkbox" id="check" />
          <span></span>
          <span></span>
          <span></span>
        </label>
      </div>
      <div class="burger-nav">
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
      </div>
    </div>
    <div>
      <router-view />
    </div>
  </div>
</template>