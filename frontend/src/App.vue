<script setup lang="ts">

function toggleBurgerMenu() {
  //switch the value of display in class burger-nav
  const burgerNav = document.querySelector('.burger-nav');
  if (burgerNav) {
    burgerNav.classList.toggle('active');
  }
}

//Check the value of the rooter-link and display the appropriate footer
// function footerResponsivePosition() {
//   const footer = document.querySelector('footer');
//   if (footer) {
//     //Look at rooter-link value by checking the URL
//     if (window.location.href.includes('upload') || window.location.href.includes('editor')) {
//       footer.style.position = 'absolute';
//     } else {
//       footer.style.position = 'fixed';
//     }

//   }
// }

//setInterval(footerResponsivePosition, 1000);
</script>

<template>
  <header>
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
    <div class="burger-icon" @click="toggleBurgerMenu()">
      <label for="checkbox">
        <input type="checkbox" id="check" />
        <span></span>
        <span></span>
        <span></span>
      </label>
    </div>
    <div class="burger-menu">
      <div class="burger-nav">
        <ul>
          <li>
            <router-link to="/" @click="toggleBurgerMenu()">Home</router-link>
          </li>
          <li>
            <router-link to="/gallery" @click="toggleBurgerMenu()">Gallery</router-link>
          </li>
          <li>
            <router-link to="/upload" @click="toggleBurgerMenu()">Upload</router-link>
          </li>
          <li>
            <router-link to="/editor" @click="toggleBurgerMenu()">Editor</router-link>
          </li>
        </ul>
      </div>
    </div>
  </header>
  <div id="app">
    <div>
      <router-view />
    </div>
  </div>
  <footer>
    <div class="footer-container">
      <div class="footer-element">
        <p>Contributeurs : Sanson Marie | Lumet Hugo | Lou-Poueyou Alexandre</p>
        <p><a href="https://gitlab.emi.u-bordeaux.fr/pdl-l3/teams/2024/l1/l1e">Code source</a> - © PDL L1E - 2024</p>
      </div>
    </div>
  </footer>
</template>