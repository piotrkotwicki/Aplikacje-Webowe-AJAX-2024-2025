<script lang="ts" setup>
import Footer from "@/components/Footer.vue";
import Header from "@/components/Header.vue";
import AddDirectorForm from "../components/AddDirectorForm.vue";
</script>

<template>
  <!-- Wrapper -->
  <div id="wrapper">
    <Header> </Header>

    <!-- Main -->
    <div id="main">
      <h1 class="text-3xl mb-8">Zarejestruj się</h1>
      <AddDirectorForm />
    </div>
    <Footer> </Footer>
  </div>
</template>
