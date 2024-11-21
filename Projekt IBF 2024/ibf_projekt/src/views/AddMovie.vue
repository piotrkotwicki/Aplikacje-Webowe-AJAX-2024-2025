<script lang="ts" setup>
import Footer from "@/components/Footer.vue";
import Header from "@/components/Header.vue";
import AddMovieForm from "../components/AddMovieForm.vue";
</script>

<template>
  <!-- Wrapper -->
  <div id="wrapper">
    <Header> </Header>

    <!-- Main -->
    <div id="main">
      <h1 class="text-3xl mb-8">Zarejestruj się</h1>
      <AddMovieForm />
    </div>
    <Footer> </Footer>
  </div>
</template>
