<script setup lang="ts">
import { useAuthStore } from "@/stores/auth";
import axios, { AxiosError } from "axios";
import { onMounted, ref } from "vue";

const auth = useAuthStore();
const token = auth.token;
const movies = ref([]);

interface CustomAxiosError extends AxiosError {
  value?: string;
}

const handleError = (error: CustomAxiosError) => {
  if (
    error.response &&
    error.response.data &&
    typeof error.response.data === "object"
  ) {
    const responseData: any = error.response.data;
    error.value = "Failed to fetch movies: " + responseData.message;
  } else if (error.request) {
    error.value = "No response from server";
  } else {
    error.value = "Error making request: " + error.message;
  }
};

const getMovies = async (page: number = 0) => {
  try {
    const response = await axios.get("http://localhost:8080/movies", {
      params: {
        page: page,
        size: 9,
      },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    console.log("Filmy z API:", response.data);
    movies.value = response.data;
  } catch (error) {
    handleError(error as AxiosError);
  }
};

onMounted(() => {
  getMovies();
});
</script>

<template>
  <div class="movies-container">
    <div class="grid grid-cols-3 gap-0">
      <div
        v-for="movie in movies.slice(0, 9)"
        :key="movie.idmovie"
        class="movie-item mx-auto"
      >
        <img
          class="w-full h-auto object-contain"
          :src="`${movie.posterLocation}`"
          loading="lazy"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.movies-container {
  @apply w-full max-w-screen-lg mx-auto;
}
</style>
