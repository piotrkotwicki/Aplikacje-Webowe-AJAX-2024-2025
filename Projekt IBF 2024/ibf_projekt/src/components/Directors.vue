<script setup lang="ts">
import { useAuthStore } from "@/stores/auth";
import axios, { AxiosError } from "axios";
import { onMounted, reactive, ref } from "vue";

const auth = useAuthStore();
const token = auth.token;
const userRole = localStorage.getItem("authority")
  ? localStorage.getItem("authority")?.slice(1, -1)
  : null;
const addNewDirectorForm = ref(true);
const directorData = reactive({
  iddirector: null,
  name: "",
  surname: "",
  birthdate: "",
});
const directors = ref([]);
const error = ref<string | null>(null);
const errorPresent = ref(false);
const currentPage = ref(0);
const SINGLE_PAGE_SIZE = ref(0);
const CURRENT_PAGE_NUMBER = ref(0);
let tableCountRequested = false;
const directorDataEditForm = ref(true);
let idOfDirector = null;

const deleteDirector = async (id: number) => {
  if (confirm("Are you sure you want to delete this record?")) {
    try {
      await axios.delete(`http://localhost:8080/directors/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      location.reload();
    } catch (error) {
      handleError(error as AxiosError);
    }
  }
};

const addNewDirector = async () => {
  try {
    const response = await axios.post(
      `http://localhost:8080/directors`,
      directorData,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    if (response.status == 201) {
      location.reload();
    } else {
      throw new Error("Director not added");
    }
  } catch (error) {
    handleError(error as AxiosError);
  }
};

const getDirectorsCount = async () => {
  auth.isTokenExpired();
  try {
    const response = await axios.get("http://localhost:8080/directors/count", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    SINGLE_PAGE_SIZE.value = response.data;
    tableCountRequested = true;
  } catch (error) {
    handleError(error as AxiosError);
  }
};

const getDirectors = async (page: number = 0) => {
  try {
    if (!tableCountRequested) {
      await getDirectorsCount();
    }

    const response = await axios.get("http://localhost:8080/directors", {
      params: {
        page: page,
      },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    CURRENT_PAGE_NUMBER.value = directors.value.length;
    directors.value = response.data;
    currentPage.value = page;
  } catch (error) {
    handleError(error as AxiosError);
  }
};

const enableEditOfDirector = async (iddirector: number) => {
  errorPresent.value = false;
  directorDataEditForm.value = !directorDataEditForm.value;
  idOfDirector = iddirector;
};

const updateDirector = async () => {
  directorDataEditForm.value = !directorDataEditForm.value;
  try {
    await axios.put(
      `http://localhost:8080/directors`,
      {
        birthdate: directorData.birthdate,
        iddirector: idOfDirector,
        name: directorData.name,
        surname: directorData.surname,
      },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    window.location.reload();
  } catch (error) {
    handleError(error as AxiosError);
  }
};

interface CustomAxiosError extends AxiosError {
  value?: string;
}

const handleError = (error: CustomAxiosError) => {
  errorPresent.value = true;

  if (
    error.response &&
    error.response.data &&
    typeof error.response.data === "object"
  ) {
    const responseData: any = error.response.data;
    error.value = responseData.message || "An error occurred";
  } else if (error.request) {
    error.value = "No response from server";
  } else {
    error.value = "Error making request: " + error.message;
  }

  error.value ? (errorPresent.value = true) : (errorPresent.value = false);
};

const handleClick = () => {
  errorPresent.value = false;
  addNewDirectorForm.value = !addNewDirectorForm;
};

const booleanManager = () => {
  directorDataEditForm.value = true;
  errorPresent.value = false;
};

onMounted(() => {
  getDirectors();
});
</script>

<template>
  <div v-if="directorDataEditForm">
    <table v-if="addNewDirectorForm">
      <thead>
        <tr>
          <th>ID reżysera</th>
          <th>Imię</th>
          <th>Nazwisko</th>
          <th>Data Urodzenia</th>
          <th v-if="userRole == 'ADMIN'">Opcje</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="directorData in directors" :key="directorData.iddirector">
          <td>{{ directorData.iddirector }}</td>
          <td>{{ directorData.name }}</td>
          <td>{{ directorData.surname }}</td>
          <td>
            {{ new Date(directorData.birthdate).toLocaleDateString("pl-PL") }}
          </td>
          <td v-if="userRole == 'ADMIN'">
            <button
              class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
              @click="deleteDirector(directorData.iddirector)"
              type="button"
            >
              Usuń
            </button>
          </td>
          <td v-if="userRole == 'ADMIN'">
            <button
              class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
              @click="enableEditOfDirector(directorData.iddirector)"
              type="button"
            >
              Edytuj
            </button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="error" class="error-message">{{ error }}</div>

    <button
      v-if="userRole == 'ADMIN'"
      @click="handleClick()"
      class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
      type="button"
    >
      Dodaj Reżysera
    </button>

    <nav v-if="addNewDirectorForm" aria-label="Page navigation example">
      <ul class="inline-flex -space-x-px text-sm">
        <li>
          <button
            @click.prevent="getDirectors(currentPage - 1)"
            @click="errorPresent = false"
            :disabled="currentPage == 0"
            href="#"
            class="flex items-center justify-center px-3 h-8 ms-0 leading-tight text-gray-500 bg-white border border-e-0 border-gray-300 rounded-s-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
          >
            Wstecz
          </button>
        </li>
        <li>
          <button
            aria-current="page"
            class="flex items-center justify-center px-3 h-8 text-blue-600 border border-gray-300 bg-blue-50 hover:bg-blue-100 hover:text-blue-700 dark:border-gray-700 dark:bg-gray-700 dark:text-white"
          >
            {{ currentPage + 1 }}
          </button>
        </li>
        <li>
          <button
            :disabled="
              SINGLE_PAGE_SIZE - (currentPage + 1) * CURRENT_PAGE_NUMBER < 1
            "
            @click.prevent="getDirectors(currentPage + 1)"
            @click="errorPresent = false"
            class="flex items-center justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 rounded-e-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
          >
            Dalej
          </button>
        </li>
      </ul>
    </nav>

    <form
      class="bg-white p-8 rounded-md shadow-md"
      @submit.prevent="addNewDirector"
      v-if="!addNewDirectorForm"
    >
      <div class="mb-4">
        <label for="Name" class="block text-gray-700 text-sm font-bold mb-2"
          >Imię</label
        >
        <input
          type="text"
          id="name"
          placeholder="Imie"
          v-model="directorData.name"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="mb-6">
        <label for="Surname" class="block text-gray-700 text-sm font-bold mb-2"
          >Nazwisko</label
        >
        <input
          type="text"
          id="surname"
          placeholder="Nazwisko"
          v-model="directorData.surname"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="mb-6">
        <label
          for="Birthdate"
          class="block text-gray-700 text-sm font-bold mb-2"
          >Data Urodzenia</label
        >
        <input
          type="date"
          id="birthdate"
          placeholder="Data Urodzenia"
          v-model="directorData.birthdate"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="flex items">
        <button
          class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          type="submit"
        >
          Dodaj Reżysera
        </button>
        <button
          @click="addNewDirectorForm = !addNewDirectorForm"
          class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          type="button"
        >
          Cofnij
        </button>
      </div>
    </form>
  </div>
  <div v-if="!directorDataEditForm">
    <form
      class="bg-white p-8 rounded-md shadow-md mt-4"
      @submit.prevent="updateDirector"
    >
      <div class="mb-4 pt-4">
        <label for="Name" class="block text-gray-700 text-sm font-bold mb-2"
          >Imię</label
        >
        <input
          type="text"
          id="name"
          placeholder="Imie"
          v-model="directorData.name"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="mb-6">
        <label for="Surname" class="block text-gray-700 text-sm font-bold mb-2"
          >Nazwisko</label
        >
        <input
          type="text"
          id="surname"
          placeholder="Nazwisko"
          v-model="directorData.surname"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="mb-6">
        <label
          for="Birthdate"
          class="block text-gray-700 text-sm font-bold mb-2"
          >Data Urodzenia</label
        >
        <input
          type="date"
          id="birthdate"
          placeholder="Data Urodzenia"
          v-model="directorData.birthdate"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="flex items">
        <button
          class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          type="submit"
        >
          Edytuj
        </button>
        <button
          @click="booleanManager"
          class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          type="button"
        >
          Cofnij
        </button>
      </div>
    </form>
  </div>
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
      <span class="font-medium">{{ "Błąd" }}</span>
    </div>
  </div>
</template>
