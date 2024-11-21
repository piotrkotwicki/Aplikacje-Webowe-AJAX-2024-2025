<script lang="ts" setup>
import Footer from "@/components/Footer.vue";
import Header from "@/components/Header.vue";
import RegisterForm from "../components/RegisterForm.vue";
</script>

<template>
  <!-- Wrapper -->
  <div id="wrapper">
    <Header> </Header>

    <!-- Main -->
    <div id="main">
      <h1 class="text-3xl mb-8">Zarejestruj się</h1>
      <Register-form />
    </div>
    <Footer> </Footer>
  </div>
</template>
