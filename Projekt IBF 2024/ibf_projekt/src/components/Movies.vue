<script setup lang="ts">
import { useAuthStore } from "@/stores/auth";
import axios, { AxiosError } from "axios";
import { onMounted, reactive, ref } from "vue";

const auth = useAuthStore();
const token = auth.token;
const userUsername = localStorage.getItem("user")
  ? localStorage.getItem("user").slice(1, -1)
  : null;
const userRole = localStorage.getItem("authority")
  ? localStorage.getItem("authority")?.slice(1, -1)
  : null;
const addNewMovieForm = ref(true);
const movie = reactive({
  idmovie: null,
  title: "",
  genre: "",
  premieredate: "",
  username: "",
  directorName: "",
  iddir: null,
});
const directors = ref([]);
const movies = ref([]);
const error = ref<string | null>(null);
const currentPage = ref(0);
const SINGLE_PAGE_SIZE = ref(0);
const CURRENT_PAGE_NUMBER = ref(0);
let countRequested = false;
const movieDataEditForm = ref(true);
let movieId = 0;

const fetchDirectors = async () => {
  try {
    const response = await axios.get("http://localhost:8080/directors/all", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    directors.value = response.data;
  } catch (error) {}
};

const deleteMovie = async (id: number) => {
  if (confirm("Are you sure you want to delete this record?")) {
    try {
      await axios.delete(`http://localhost:8080/movies/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      window.location.reload();
    } catch (error) {
      handleError(error as AxiosError);
    }
  }
};

const addMovie = async () => {
  try {
    const response = await axios.post(
      `http://localhost:8080/movies`,
      {
        title: movie.title,
        genre: movie.genre,
        premieredate: movie.premieredate,
        iddir: movie.iddir,
        username: userUsername,
      },
      {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      }
    );

    if (response.status == 200) {
      window.location.reload();
    } else {
      throw new Error("Movie not added");
    }
  } catch (error) {
    handleError(error as AxiosError);
  }
};

const editMovie = async (movieToEdit: any) => {
  if (directors.value.length === 0) {
    await fetchDirectors();
  }
  movieDataEditForm.value = false;
  movie.title = movieToEdit.title;
  movie.genre = movieToEdit.genre;
  movie.premieredate = movieToEdit.premieredate.split("T")[0];
  movie.directorName = movieToEdit.directorName;
  movie.iddir = movieToEdit.iddir;
  movie.username = movieToEdit.username;
  movieId = movieToEdit.idmovie;
};

const updateMovie = async () => {
  try {
    console.log("REŻ:", movie.directorName);
    const response = await axios.put(
      `http://localhost:8080/movies/${movieId}`,
      {
        title: movie.title,
        genre: movie.genre,
        premieredate: movie.premieredate,
        iddir: movie.iddir,
        username: userUsername,
      },
      {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      }
    );

    if (response.status == 200) {
      window.location.reload();
    } else {
      throw new Error("Movie not updated");
    }
  } catch (error) {
    handleError(error as AxiosError);
  }
};

const getMoviesCount = async () => {
  try {
    const response = await axios.get("http://localhost:8080/movies/count", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    SINGLE_PAGE_SIZE.value = response.data;
    countRequested = true;
  } catch (error) {
    handleError(error as AxiosError);
  }
};

const getMovies = async (page: number = 0) => {
  try {
    if (!countRequested) {
      await getMoviesCount();
    }

    const response = await axios.get("http://localhost:8080/movies", {
      params: {
        page: page,
      },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    CURRENT_PAGE_NUMBER.value = movies.value.length;
    movies.value = response.data;
    currentPage.value = page;
  } catch (error) {
    handleError(error as AxiosError);
  }
};

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
    error.value = "Failed to fetch meals: " + responseData.message;
  } else if (error.request) {
    error.value = "No response from server";
  } else {
    error.value = "Error making request: " + error.message;
  }
};
const handleClick = () => {
  addNewMovieForm.value = !addNewMovieForm;
  if (!addNewMovieForm.value || movieDataEditForm.value) {
    fetchDirectors();
  }
};

onMounted(() => {
  getMovies();
});
</script>

<template>
  <div v-if="movieDataEditForm">
    <table v-if="addNewMovieForm">
      <thead>
        <tr>
          <th>Movie ID</th>
          <th>Title</th>
          <th>Genre</th>
          <th>Premiere Date</th>
          <th>Reżyser</th>
          <th v-if="userRole == 'ADMIN'">Options</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="movie in movies" :key="movie.idmovie">
          <td>{{ movie.idmovie }}</td>
          <td>{{ movie.title }}</td>
          <td>{{ movie.genre }}</td>
          <td>
            {{ new Date(movie.premieredate).toLocaleDateString("pl-PL") }}
          </td>
          <td>{{ movie.directorName }}</td>
          <td v-if="userRole == 'ADMIN'">
            <button
              class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
              @click="deleteMovie(movie.idmovie)"
              type="button"
            >
              Delete
            </button>
          </td>
          <td v-if="userRole == 'ADMIN'">
            <button
              class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
              @click="editMovie(movie)"
              type="button"
            >
              Edit
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
      Add
    </button>

    <nav v-if="addNewMovieForm" aria-label="Page navigation example">
      <ul class="inline-flex -space-x-px text-sm">
        <li>
          <button
            @click.prevent="getMovies(currentPage - 1)"
            :disabled="currentPage == 0"
            href="#"
            class="flex items-center justify-center px-3 h-8 ms-0 leading-tight text-gray-500 bg-white border border-e-0 border-gray-300 rounded-s-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
          >
            Previous
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
            @click.prevent="getMovies(currentPage + 1)"
            class="flex items-center justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 rounded-e-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
          >
            Next
          </button>
        </li>
      </ul>
    </nav>

    <form
      class="bg-white p-8 rounded-md shadow-md"
      @submit.prevent="addMovie()"
      v-if="!addNewMovieForm"
    >
      <div class="mb-4">
        <label for="Title" class="block text-gray-700 text-sm font-bold mb-2"
          >Title</label
        >
        <input
          type="text"
          id="title"
          placeholder="Tytuł"
          v-model="movie.title"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="mb-6">
        <label for="Genre" class="block text-gray-700 text-sm font-bold mb-2"
          >Genre</label
        >
        <input
          type="text"
          id="genre"
          placeholder="Gatunek"
          v-model="movie.genre"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="mb-6">
        <label
          for="Director"
          class="block text-gray-700 text-sm font-bold mb-2"
        >
          Director
        </label>
        <select
          id="director"
          v-model="movie.iddir"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
        >
          <option value="" disabled>Select a director</option>
          <option
            v-for="director in directors"
            :key="director.iddirector"
            :value="director.iddirector"
          >
            {{ director.name }} {{ director.surname }}
          </option>
        </select>
      </div>
      <div class="mb-6">
        <label
          for="Birthdate"
          class="block text-gray-700 text-sm font-bold mb-2"
          >Premiere Date</label
        >
        <input
          type="date"
          id="premieredate"
          placeholder="Data Premiery"
          v-model="movie.premieredate"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="flex items">
        <button
          class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          type="submit"
        >
          Add Movie
        </button>
        <button
          @click="addNewMovieForm = !addNewMovieForm"
          class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          type="button"
        >
          Cofnij
        </button>
      </div>
    </form>
  </div>

  <div v-if="!movieDataEditForm">
    <form
      class="bg-white p-8 rounded-md shadow-md"
      @submit.prevent="updateMovie"
    >
      <div class="mt-8">
        <label for="Title" class="block text-gray-700 text-sm font-bold mb-2"
          >Title</label
        >
        <input
          type="text"
          id="title"
          placeholder="Tytuł"
          v-model="movie.title"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="mb-6">
        <label for="Genre" class="block text-gray-700 text-sm font-bold mb-2"
          >Genre</label
        >
        <input
          type="text"
          id="genre"
          placeholder="Gatunek"
          v-model="movie.genre"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="mb-6">
        <label
          for="Director"
          class="block text-gray-700 text-sm font-bold mb-2"
        >
          Director
        </label>
        <select
          id="director"
          v-model="movie.iddir"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
        >
          <option value="" disabled>Select a director</option>
          <option
            v-for="director in directors"
            :key="director.iddirector"
            :value="director.iddirector"
          >
            {{ director.name }} {{ director.surname }}
          </option>
        </select>
      </div>
      <div class="mb-6">
        <label
          for="Premierdate"
          class="block text-gray-700 text-sm font-bold mb-2"
          >Premiere Date</label
        >
        <input
          type="date"
          id="premierdate"
          placeholder="Data premiery"
          v-model="movie.premieredate"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="flex items">
        <button
          class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          type="submit"
        >
          Edit
        </button>
        <button
          @click="movieDataEditForm = !movieDataEditForm"
          class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          type="button"
        >
          Go Back
        </button>
      </div>
    </form>
  </div>
</template>

<style>
.disabled-link {
  pointer-events: none;
}
</style>
