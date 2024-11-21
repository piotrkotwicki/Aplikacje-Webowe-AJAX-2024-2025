<script lang="ts" setup>
import router from "@/router";
import axios from "axios";
import { reactive } from "vue";

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
      window.location.reload;
    } else {
      throw new Error("Invalid credentials");
    }
  } catch (error) {
    console.error("Error during registration:", error);
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
</template>
