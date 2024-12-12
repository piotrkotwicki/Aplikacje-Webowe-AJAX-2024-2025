<script lang="ts" setup>
import router from "@/router";
import axios from "axios";
import { reactive, ref } from "vue";

const errorPresent = ref(false);
const errorMessage = ref("");

const user = reactive({
  username: "",
  password: "",
});

const onSubmit = async () => {
  try {
    const response = await axios.post("http://localhost:8080/register", {
      username: user.username,
      password: user.password,
    });

    if (response.status === 200) {
      console.log("Successful registration");
      router.push("/login");
      window.location.reload();
    } else {
      throw new Error("Unexpected error during registration");
    }
  } catch (error: any) {
    errorPresent.value = true;

    if (error.response && error.response.status === 409) {
      errorMessage.value =
        "Ta nazwa użytkownika jest już zajęta! Wybierz inną.";
    } else if (error.response && error.response.status === 400) {
      errorMessage.value = "Proszę wypełnić wszystkie pola.";
    } else {
      errorMessage.value = "Wystąpił błąd. Spróbuj ponownie.";
    }
  }
};
</script>

<template>
  <form class="bg-white p-8 rounded-md shadow-md" @submit.prevent="onSubmit">
    <div class="mb-4">
      <label for="username" class="block text-gray-700 text-sm font-bold mb-2"
        >Login</label
      >
      <input
        type="text"
        id="username"
        placeholder="Login"
        v-model="user.username"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
      />
    </div>
    <div class="mb-6">
      <label for="password" class="block text-gray-700 text-sm font-bold mb-2"
        >Hasło</label
      >
      <input
        pattern=".{3,}"
        required
        title="3 characters minimum"
        type="password"
        id="password"
        placeholder="Hasło"
        v-model="user.password"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
      />
    </div>
    <div class="flex items">
      <button
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        type="submit"
      >
        Zarejestruj się
      </button>
    </div>
  </form>
  <div
    class="flex items-center p-4 mb-4 text-sm text-red-800 border border-red-300 rounded-lg bg-red-50 dark:bg-gray-800 dark:text-red-400 dark:border-red-800"
    role="alert"
    v-if="errorPresent"
  >
    <svg
      class="flex-shrink-0 inline w-4 h-4 me-3"
      aria-hidden="true"
      xmlns="http://www.w3.org/2000/svg"
      fill="currentColor"
      viewBox="0 0 20 20"
    >
      <path
        d="M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5ZM9.5 4a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3ZM12 15H8a1 1 0 0 1 0-2h1v-3H8a1 1 0 0 1 0-2h2a1 1 0 0 1 1 1v4h1a1 1 0 0 1 0 2Z"
      />
    </svg>
    <span class="sr-only">Info</span>
    <div>
      <span class="font-medium">{{ errorMessage }}</span>
    </div>
  </div>
</template>
