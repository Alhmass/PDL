<img src="banner.png"></img>
<h1>README - Projet Développement Logiciel</h1>
<hr></hr>

<h2>Sommaire</h2>

- [Introduction](#introduction)
- [Technologies](#technologies)
  - [Language](#language)
  - [Autres](#autres)
- [Utilisation](#utilisation)
- [Configuration](#configuration)
- [Compatibilité](#compatibilité)
  - [OS](#os)
  - [Navigateurs](#navigateurs)
- [Auteurs](#auteurs)


### Introduction

Le projet ici présenté est une application web de gestion d'images. Elle permet de stocker des images dans une base de données, de les visualiser et de rechercher des images similaires présentes dans la base de données à partir d'une image de référence.
Ce projet est composé de deux parties : 
- une partie backend qui est une API dévéloppée en Java avec le framework Spring Boot, il permet de traités les requêtes envoyé par le frontend et de gérer la manipulation de la base de données.
- une partie frontend qui est une application web dévéloppée en TypeScript avec le framework Vue.js, elle permet de gérer l'interface utilisateur et de communiquer avec le backend pour envoyer des requêtes et afficher les résultats, elle permet également de visualiser les images stockées dans la base de données et de rechercher des images similaires à partir d'une image de référence. On peut aussi depuis cette page ajouter ou supprimer des images ce qui sera actualisé dans la base de données par le backend.

### Technologies

Les technologies utilisées pour ce projet sont : 

#### Language

[![Java](https://img.shields.io/badge/Java_JDK17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/fr/) [![TypeScript](https://img.shields.io/badge/TypeScript-007ACC?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/) [![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=vue-dot-js&logoColor=white)](https://vuejs.org/) [![PostgreSQL](https://img.shields.io/badge/PostgreSQL_42.7.1-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/) [![CSS](https://img.shields.io/badge/CSS-1572B6?style=for-the-badge&logo=css3&logoColor=white)](https://developer.mozilla.org/fr/docs/Web/CSS)

#### Autres

[![GitLab](https://img.shields.io/badge/GitLab-FCA121?style=for-the-badge&logo=gitlab&logoColor=white)](https://gitlab.com/) [![VSCode](https://img.shields.io/badge/Visual_Studio_Code-0078D4?style=for-the-badge&logo=visual%20studio%20code&logoColor=white)](https://code.visualstudio.com/) [![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/) [![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.2.1-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot) [![BoofCV](https://img.shields.io/badge/BoofCV_1.1.2-6CBB5A?style=for-the-badge)](https://boofcv.org/) [![PgVector](https://img.shields.io/badge/PgVector_0.1.4-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://example.com)


### Utilisation

Télécharger le projet en utilisant la commande suivante : 

- depuis HTTPS :
`git clone [Repo url]`

- depuis SSH :
`git clone [Repo url]`

## Configuration

Avant de compiler le projet, configurez une base de données PostgreSQL avec l'extension pgVector et définissez les variables d’environnement suivantes :

- `DATABASE_NAME` : nom de la base
- `DATABASE_USER` : nom d'utilisateur
- `DATABASE_PORT` : port PostgreSQL (par défaut : 5432)
- `DATABASE_PASSWORD` : mot de passe d’accès

Pour compiler le projet :
`mvn clean compile`

Dès que le projet est compilé, il suffit de lancer le frontend et le backend pour pouvoir utiliser l'application avec la commande suivante : 
`mvn --projects backend spring-boot:run`

A noter : l'addresse de l'application pour la machine locale est http://localhost:8080

### Compatibilité

Le projet a était testé et fonctionne sur les systèmes d'exploitations et navigateurs suivants : 

#### OS

[![Garuda Linux](https://img.shields.io/badge/Garuda_Linux-6A5ACD?style=for-the-badge&logo=arch-linux&logoColor=white)](https://garudalinux.org/) [![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white)](https://ubuntu.com/) [![Linux Mint](https://img.shields.io/badge/Linux_Mint-87CF3E?style=for-the-badge&logo=linux-mint&logoColor=white)](https://linuxmint.com/) [![Debian](https://img.shields.io/badge/Debian-A81D33?style=for-the-badge&logo=debian&logoColor=white)](https://www.debian.org/) [![macOS](https://img.shields.io/badge/macOS_12.7.3-000000?style=for-the-badge&logo=apple&logoColor=white)](https://www.apple.com/macos/) [![Windows 10](https://img.shields.io/badge/Windows_10_&_11-0066CC?style=for-the-badge&logo=windows&logoColor=white)](https://www.microsoft.com/windows/)

<p style="color:red">Attention : Pour l'utilisation sur Windows, il est nécessaire d'installer un terminal compatible avec les commandes UNIX, tel que Git Bash ou WSL.</p>

#### Navigateurs

[![Firefox](https://img.shields.io/badge/Firefox-FF7139?style=for-the-badge&logo=firefox-browser&logoColor=white)](https://www.mozilla.org/firefox/) [![Brave](https://img.shields.io/badge/Brave-FB542B?style=for-the-badge&logo=brave&logoColor=white)](https://brave.com/) [![Safari](https://img.shields.io/badge/Safari-0066CC?style=for-the-badge&logo=safari&logoColor=white)](https://www.apple.com/safari/) [![Google Chrome](https://img.shields.io/badge/Google_Chrome-4285F4?style=for-the-badge&logo=google-chrome&logoColor=white)](https://www.google.com/chrome/)

### Auteurs

- [Marie Sanson]() - masanson@u-bordeaux.fr 
- [Lumet Hugo](https://github.com/Alhmass) - hugo.lumet@protonmail.com
- [Alexandre Lou-Poueyou](https://github.com/AlexLoup33) - alexandre.lou-poueyou@etu.u-bordeaux.fr