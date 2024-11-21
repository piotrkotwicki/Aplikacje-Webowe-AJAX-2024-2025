<script lang="ts" setup>
import Footer from "@/components/Footer.vue";
import Header from "@/components/Header.vue";
import LoginForm from "../components/LoginForm.vue";
</script>

<template>
  <!-- Wrapper -->
  <div id="wrapper">
    <Header> </Header>

    <!-- Main -->
    <div id="main">
      <h1 class="text-3xl mb-8">Zaloguj sie</h1>
      <Login-form />
    </div>
    <Footer> </Footer>
  </div>
</template>
