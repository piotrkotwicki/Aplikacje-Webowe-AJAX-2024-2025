<script setup lang="ts">
import { useAuthStore } from '@/stores/auth';
import axios, { AxiosError } from 'axios';
import { onMounted, reactive, ref } from 'vue';

const auth = useAuthStore();
const token = auth.token;
const role = localStorage.getItem('authority')?localStorage.getItem('authority')?.slice(1,-1): null;
console.log(role);
const add= ref(true)
const director = reactive({
    iddirector: null,
    name: '',
    surname: '',
    birthdate: '',
    // iduser: localStorage.getItem('id'),
});
const directors = ref([]);
const error = ref<string | null>(null);
const currentPage = ref(0);
const PAGE_SIZE = ref(0);
const PAGE_COUNT = ref(0);
let countRequested = false;
const editing = ref(true)
let iddirectora = 0;

const deleteDirector = async (id: number) => {
  if (confirm("Are you sure you want to delete this record?")) {
    try {
      await axios.delete(`http://localhost:8080/directors/${id}`, {
      headers: {
            Authorization: `Bearer ${token}` // Ensure to include only the token without "Bearer"
        }
    });
      console.log('User deleted successfully');
      location.reload(); // Refresh users after deletion
    } catch (error) {
      console.error('Error deleting user:', error);
      handleError(error as AxiosError);
    }
  }
};
const addDirector = async () => {
  
    try {
      const response = await axios.post(`http://localhost:8080/directors`, director, {
      headers: {
        Authorization: `Bearer ${token}`
      }


    });
    if(response.status==201){
      console.log('Director added');
      location.reload(); // Refresh users after deletion
    }else{
      throw new Error('Director not added');
    }
     
    } catch (error) {
      handleError(error as AxiosError);
    }
  };
const getDirectorsCount = async () => {
  console.log(localStorage.getItem('id'));
  auth.isTokenExpired();
    try {
        const response = await axios.get("http://localhost:8080/directors/count", {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        PAGE_SIZE.value = response.data;
        countRequested = true;

    } catch (error) {
        handleError(error as AxiosError);
    }
};

const getDirectors = async (page: number = 0) => {
    try {
        
        if(!countRequested) {
            await getDirectorsCount();
        }

        const response = await axios.get("http://localhost:8080/directors", {
            params: {
                page: page
            },
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        
        PAGE_COUNT.value = directors.value.length;
        directors.value = response.data;
        currentPage.value = page;
    } catch (error) {
        handleError(error as AxiosError);
    }
};

const editDirector = async (iddirector: number) => {
  editing.value = !editing.value;
  console.log("ID directora: ",iddirector);
  iddirectora = iddirector;
};

const updateDirector = async () => {
  editing.value = !editing.value;
    try {
      const response = await axios.put(`http://localhost:8080/directors`, {
        birthdate: director.birthdate,
        iddirector: iddirectora,
        name: director.name,
        surname: director.surname
      }, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      console.log('Director updated successfully');
      window.location.reload();
    } catch (error) {
      console.error('Error updating director:', error);
      handleError(error as AxiosError);
    }
};

interface CustomAxiosError extends AxiosError {
  value?: string;
}

const handleError = (error: CustomAxiosError) => {
  if (error.response && error.response.data && typeof error.response.data === 'object') {
    const responseData: any = error.response.data;
    error.value = 'Failed to fetch directors: ' + responseData.message;
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
  getDirectors();
});

</script>

<template>
  <div v-if="editing">
    <table v-if="add">
      <thead>
        <tr>
          <th>Director ID</th>
          <th>Name</th>
          <th>Surname</th>
          <th>Birthdate</th>
          <th v-if="role=='ADMIN'">Options</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="director in directors" :key="director.iddirector">
          <td>{{ director.iddirector }}</td>
          <td>{{ director.name }}</td>
          <td>{{ director.surname }}</td>
          <td>{{ new Date(director.birthdate).toLocaleDateString("pl-PL") }}</td>
          <td v-if="role=='ADMIN'"><button
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline" @click="deleteDirector(director.iddirector)"
        type="button">
        Delete
      </button></td>
      <td v-if="role=='ADMIN'"><button
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline" @click="editDirector(director.iddirector)"
        type="button">
        Edit
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
        <button @click.prevent="getDirectors(currentPage - 1)" :disabled="currentPage==0" href="#" class="flex items-center justify-center px-3 h-8 ms-0 leading-tight text-gray-500 bg-white border border-e-0 border-gray-300 rounded-s-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">Previous</button>
      </li>
      <li>
        <button aria-current="page" class="flex items-center justify-center px-3 h-8 text-blue-600 border border-gray-300 bg-blue-50 hover:bg-blue-100 hover:text-blue-700 dark:border-gray-700 dark:bg-gray-700 dark:text-white">{{ currentPage + 1 }}</button>
      </li>
      <li>
        <button :disabled="PAGE_SIZE - ((currentPage + 1) * PAGE_COUNT) < 1" @click.prevent="getDirectors(currentPage + 1)" class="flex items-center justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 rounded-e-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">Next</button>
      </li>
    </ul>
  </nav>

<form class="bg-white p-8 rounded-md shadow-md" @submit.prevent="addDirector" v-if = "!add">
    <div class="mb-4">
      <label for="Name" class="block text-gray-700 text-sm font-bold mb-2">Imie</label>
      <input type="text" id="name" placeholder="Imie" v-model="director.name"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
    </div>
    <div class="mb-6">
      <label for="Surname" class="block text-gray-700 text-sm font-bold mb-2">Nazwisko</label>
      <input type="text" id="surname" placeholder="Nazwisko" v-model="director.surname"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline">
    </div>
    <div class="mb-6">
      <label for="Birthdate" class="block text-gray-700 text-sm font-bold mb-2">Data Urodzenia</label>
      <input type="date" id="birthdate" placeholder="Data Urodzenia" v-model="director.birthdate"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline">
    </div>
    <div class="flex items">
      <button
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        type="submit">
        Dodaj Reżysera
      </button>
      <button @click="add=!add"
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        type="button">
        Cofnij
      </button>
    </div>
  </form>
  </div>
  <div v-if="!editing">
    <form class="bg-white p-8 rounded-md shadow-md" @submit.prevent="updateDirector">
    <div class="mb-4">
      <label for="Name" class="block text-gray-700 text-sm font-bold mb-2">Imie</label>
      <input type="text" id="name" placeholder="Imie" v-model="director.name"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
    </div>
    <div class="mb-6">
      <label for="Surname" class="block text-gray-700 text-sm font-bold mb-2">Nazwisko</label>
      <input type="text" id="surname" placeholder="Nazwisko" v-model="director.surname"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline">
    </div>
    <div class="mb-6">
      <label for="Birthdate" class="block text-gray-700 text-sm font-bold mb-2">Data Urodzenia</label>
      <input type="date" id="birthdate" placeholder="Data Urodzenia" v-model="director.birthdate"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline">
    </div>
    <div class="flex items">
      <button
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        type="submit">
        Edytuj
      </button>
      <button @click="editing=!editing"
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        type="button">
        Cofnij
      </button>
    </div>
  </form>
  </div>
</template>