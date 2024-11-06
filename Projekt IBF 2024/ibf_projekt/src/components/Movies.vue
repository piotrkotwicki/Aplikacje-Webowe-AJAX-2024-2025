<script setup lang="ts">
import { useAuthStore } from '@/stores/auth';
import axios, { AxiosError } from 'axios';
import { onMounted, reactive, ref } from 'vue';

const auth = useAuthStore();
const token = auth.token;
const role = localStorage.getItem('authority')?localStorage.getItem('authority')?.slice(1,-1): null;
console.log(role);
const add= ref(true)
const movie = reactive({
    idmovie: null,
    title: '',
    genre: '',
    premieredate: '',
    iduser: localStorage.getItem('id'),
    iddirector: localStorage.getItem('id'),
    iddir: ref(0),

});
const movies = ref([]);
const error = ref<string | null>(null);
const currentPage = ref(0);
const PAGE_SIZE = ref(0);
const PAGE_COUNT = ref(0);
let countRequested = false;

const deleteMovie = async (id: number) => {
  if (confirm("Are you sure you want to delete this record?")) {
    try {
      await axios.delete(`http://localhost:8080/movies/${id}`, {
      headers: {
            Authorization: `Bearer ${token}` // Ensure to include only the token without "Bearer"
        }
    });
      console.log('User deleted successfully');
      window.location.reload(); // Refresh users after deletion
    } catch (error) {
      console.error('Error deleting user:', error);
      handleError(error as AxiosError);
    }
  }
};
const addMovie = async (id: number) => {
  
  try {
    const response = await axios.post(`http://localhost:8080/movies/${id}`, movie, {
    headers: {
      Authorization: `Bearer ${token}`
    }


  });
  if(response.status==200){
    console.log('Movie added');
    window.location.reload(); // Refresh users after deletion
  }else{
    throw new Error('Movie not added');
  }
   
  } catch (error) {
    handleError(error as AxiosError);
  }
};
const getMoviesCount = async () => {
    try {
        const response = await axios.get("http://localhost:8080/movies/count", {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        
        PAGE_SIZE.value = response.data;
        countRequested = true;
        console.log("Rekordów w tabeli jest: ", PAGE_SIZE.value);

    } catch (error) {
        handleError(error as AxiosError);
    }
};

const getMovies = async (page: number = 0) => {
    try {
        if(!countRequested) {
            await getMoviesCount();
        }

        const response = await axios.get("http://localhost:8080/movies", {
            params: {
                page: page
            },
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        
        PAGE_COUNT.value = movies.value.length;
        console.log("Rekordów na stronie jest: ", response.data.length);
        movies.value = response.data;
        currentPage.value = page;
        console.log("Aktualana strona: ", currentPage.value);
    } catch (error) {
        handleError(error as AxiosError);
    }
};

interface CustomAxiosError extends AxiosError {
  value?: string;
}

const handleError = (error: CustomAxiosError) => {
  if (error.response && error.response.data && typeof error.response.data === 'object') {
    const responseData: any = error.response.data;
    error.value = 'Failed to fetch meals: ' + responseData.message;
  } else if (error.request) {
    error.value = 'No response from server';
  } else {
    error.value = 'Error making request: ' + error.message;
  }
};
const handleClick = () => {
    add.value = !add;
}

onMounted(() => {
  getMovies();
});

</script>


<template>
  <div>
    <table v-if="add">
      <thead>
        <tr>
          <th>Movie ID</th>
          <th>Title</th>
          <th>Genre</th>
          <th>Premiere Date</th>
          <th>Reżyser</th>
          <th v-if="role=='ADMIN'">Options</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="movie in movies" :key="movie.idmovie">
          <td>{{ movie.idmovie }}</td>
          <td>{{ movie.title }}</td>
          <td>{{ movie.genre }}</td>
          <td>{{ new Date(movie.premieredate).toLocaleDateString("pl-PL") }}</td>
          <td>{{ movie.directorDetails[0] }}  {{ movie.directorDetails[1] }}</td>
          <td v-if="role=='ADMIN'"><button
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline" @click="deleteMovie(movie.idmovie)"
        type="button">
        Delete
      </button></td>
        </tr>
      </tbody>
    </table>
    <div v-if="error" class="error-message">{{ error }}</div>

    <button v-if="role=='ADMIN'" @click="handleClick()"
      class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline" 
      type="button">
      Add
  </button>

  <nav aria-label="Page navigation example">
    <ul class="inline-flex -space-x-px text-sm">
      <li>
        <button @click.prevent="getMovies(currentPage - 1)" :disabled="currentPage==0" href="#" class="flex items-center justify-center px-3 h-8 ms-0 leading-tight text-gray-500 bg-white border border-e-0 border-gray-300 rounded-s-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">Previous</button>
      </li>
      <li>
        <button aria-current="page" class="flex items-center justify-center px-3 h-8 text-blue-600 border border-gray-300 bg-blue-50 hover:bg-blue-100 hover:text-blue-700 dark:border-gray-700 dark:bg-gray-700 dark:text-white">{{ currentPage + 1 }}</button>
      </li>
      <li>
        <button :disabled="PAGE_SIZE - ((currentPage + 1) * PAGE_COUNT) < 1" @click.prevent="getMovies(currentPage + 1)" class="flex items-center justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 rounded-e-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">Next</button>
      </li>
    </ul>
  </nav>

<form class="bg-white p-8 rounded-md shadow-md" @submit.prevent="addMovie(movie.iddir)" v-if = "!add">
    <div class="mb-4">
      <label for="Title" class="block text-gray-700 text-sm font-bold mb-2">Tytuł</label>
      <input type="text" id="title" placeholder="Tytuł" v-model="movie.title"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
    </div>
    <div class="mb-4">
      <label for="id_director" class="block text-gray-700 text-sm font-bold mb-2">id director</label>
      <input type="number" min="1" id="id_director" placeholder="1" v-model="movie.iddir"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
    </div>
    <div class="mb-6">
      <label for="Genre" class="block text-gray-700 text-sm font-bold mb-2">Gatunek</label>
      <input type="text" id="genre" placeholder="Gatunek" v-model="movie.genre"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline">
    </div>
    <div class="mb-6">
      <label for="Birthdate" class="block text-gray-700 text-sm font-bold mb-2">Data Premiery</label>
      <input type="date" id="premieredate" placeholder="Data Premiery" v-model="movie.premieredate"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline">
    </div>
    <div class="flex items">

      <button
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        type="submit">
        Dodaj Film
      </button>
      <button @click="add=!add"
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        type="button">
        Cofnij
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