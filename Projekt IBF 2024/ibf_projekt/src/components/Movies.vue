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
  posterLocation: "",
  iddir: null,
});
const directors = ref([]);
const movies = ref([]);
const error = ref<string | null>(null);
const errorPresent = ref(false);
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
        posterLocation: movie.posterLocation,
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
  errorPresent.value = false;
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
        size: 3,
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
  errorPresent.value = true;

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

  error.value ? (errorPresent.value = true) : (errorPresent.value = false);
};
const handleClick = () => {
  errorPresent.value = false;
  addNewMovieForm.value = !addNewMovieForm;
  if (!addNewMovieForm.value || movieDataEditForm.value) {
    fetchDirectors();
  }
};

const updatePoster = async (event) => {
  const file = event.target.files[0];
  if (file) {
    try {
      const formData = new FormData();
      formData.append("file", file);
      formData.append("upload_preset", "posters");
      formData.append("folder", "movies_posters");

      const response = await axios.post(
        "https://api.cloudinary.com/v1_1/dhmdv4ouw/image/upload",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );

      const cloudinaryUrl = response.data.secure_url;

      movie.posterLocation = cloudinaryUrl;
      console.log("Image uploaded successfully:", cloudinaryUrl);
    } catch (error) {
      console.error("Error uploading image:", error);
    }
  }
};

const booleanManager = () => {
  addNewMovieForm.value = true;
  errorPresent.value = false;
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
          <th>ID filmu</th>
          <th>Plakat</th>
          <th>Tytuł</th>
          <th>Gatunek</th>
          <th>Data Premiery</th>
          <th>Reżyser</th>
          <th v-if="userRole == 'ADMIN'">Opcje</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="movie in movies" :key="movie.idmovie">
          <td>{{ movie.idmovie }}</td>
          <td>
            <img
              :src="movie.posterLocation"
              alt="Movie Poster"
              v-if="movie.posterLocation"
            />
            <p v-else>Plakat niedostępny</p>
          </td>
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
              Usuń
            </button>
          </td>
          <td v-if="userRole == 'ADMIN'">
            <button
              class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
              @click="editMovie(movie)"
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
      Dodaj film
    </button>

    <nav v-if="addNewMovieForm" aria-label="Page navigation example">
      <ul class="inline-flex -space-x-px text-sm">
        <li>
          <button
            @click.prevent="getMovies(currentPage - 1)"
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
            @click.prevent="getMovies(currentPage + 1)"
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
      @submit.prevent="addMovie()"
      v-if="!addNewMovieForm"
    >
      <div class="mb-4">
        <label for="Title" class="block text-gray-700 text-sm font-bold mb-2"
          >Tytuł</label
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
          >Gatunek</label
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
          Reżyser
        </label>
        <select
          id="director"
          v-model="movie.iddir"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
        >
          <option value="" disabled>Wybierz reżysera</option>
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
          >Data Premiery</label
        >
        <input
          type="date"
          id="premieredate"
          placeholder="Data Premiery"
          v-model="movie.premieredate"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="mb-6">
        <label for="poster" class="block text-gray-700 text-sm font-bold mb-2"
          >Plakat</label
        >
        <input
          type="file"
          id="poster"
          placeholder="Plakat"
          @change="updatePoster($event)"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="flex items">
        <button
          class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          type="submit"
        >
          Dodaj film
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

  <div v-if="!movieDataEditForm">
    <form
      class="bg-white p-8 rounded-md shadow-md"
      @submit.prevent="updateMovie"
    >
      <div class="mt-8">
        <label for="Title" class="block text-gray-700 text-sm font-bold mb-2"
          >Tytuł</label
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
          >Gatunek</label
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
          Reżyser
        </label>
        <select
          id="director"
          v-model="movie.iddir"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
        >
          <option value="" disabled>Wybierz reżysera</option>
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
          >Data premiery</label
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
          Edytuj
        </button>
        <button
          @click="movieDataEditForm = !movieDataEditForm"
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

<style>
.disabled-link {
  pointer-events: none;
}
</style>
