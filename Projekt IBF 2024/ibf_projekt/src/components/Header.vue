<script setup lang="ts">
import { useAuthStore } from '@/stores/auth';
import { onMounted } from 'vue';

const username = localStorage.getItem('user') ? localStorage.getItem('user').slice(1, -1) : null;
const role = localStorage.getItem('authority') ? localStorage.getItem('authority').slice(1, -1) : null;
const auth = useAuthStore();
function logout() {
  auth.logout();
}

onMounted(() => {
  auth.isTokenExpired();
});

</script>

<template>
  	<!-- Header -->
      <header id="header" class="alt">
						<!-- <span class="logo"><img src="images/logo.svg" alt="" /></span> -->
						<h1>IBF </h1>
						<p>Internetowa Baza Filmów<br />
						Autor: Piotr Kotwicki</p>
					</header>

				<!-- Nav -->
				<nav id="nav">
            <ul v-if="username">
                <RouterLink to="/"><li><a >Opis</a></li></RouterLink>
                <RouterLink to="/movies"><li><a >Filmy</a></li></RouterLink>
                <RouterLink to="/directors"><li><a >Reżyserzy</a></li></RouterLink>
                <RouterLink to="/logout"><li><a @click="logout">Wyloguj</a></li></RouterLink>
				<RouterLink to="/admin" v-if="role=='ADMIN'"><li><a >Panel Sterowania</a></li></RouterLink>
                <p>Użytkownik: {{ username }}</p>
                <p>Rola: {{ role }}</p>

            </ul>
            <ul v-if="!username">
                <RouterLink to="/"><li><a >Opis</a></li></RouterLink>
                <RouterLink to="/login"><li><a>Zaloguj się</a></li></RouterLink>
                <RouterLink to="/register"><li><a>Zarejestruj się</a></li></RouterLink>
            </ul>
        </nav>
</template>



<style>

</style>