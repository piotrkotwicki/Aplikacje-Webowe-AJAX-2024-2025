<script setup lang="ts">
import { useAuthStore } from '@/stores/auth';
import axios, { AxiosError } from 'axios';
import { computed, onMounted, ref } from 'vue';

const auth = useAuthStore();
const token = auth.token;
const users = ref([]);
const error = ref<string | null>(null);
const currentPage = ref(0);
const PAGE_SIZE = ref(0);
const PAGE_COUNT = ref(0);
const editingUserId = ref<number | null>(null);
const editingUsername = ref({
 username: ''
});
const newUsername = ref<string>('');
const TOTAL_PAGES = computed(() => Math.ceil(PAGE_SIZE.value / PAGE_COUNT.value));
let countRequested = false;
const editing = ref(true)

const deleteUser = async (id: number) => {
  if (confirm("Are you sure you want to delete this record?")) {
    try {
      await axios.delete(`http://localhost:8080/authority/${id}`, {
      headers: {
            Authorization: `Bearer ${token}`
        }
    });
      console.log('User deleted successfully');
      window.location.reload();
    } catch (error) {
      console.error('Error deleting user:', error);
      handleError(error as AxiosError);
    }
  }
};

const getUsersCount = async () => {
    try {        
        const response = await axios.get("http://localhost:8080/authority/count", {
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

const getUsers = async (page: number = 0) => {
    try {
        
        if(!countRequested) {
            await getUsersCount();
        }

        const response = await axios.get("http://localhost:8080/authority", {
            params: {
                page: page
            },
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        
        PAGE_COUNT.value = response.data.length;
        users.value = response.data;
        currentPage.value = page;
        console.log("Rekordów na stronie: ", PAGE_COUNT.value);
        console.log("Rekordów w bazie: ", PAGE_SIZE.value);
        console.log("Aktualna strona: ", currentPage.value);
    } catch (error) {
        handleError(error as AxiosError);
    }
};

interface CustomAxiosError extends AxiosError {
  value?: string;
}

const editUser = (userId: number) => {
 editingUserId.value = userId;
 editing.value = !editUser;
};

const updateUser = async () => {
 newUsername.value = editingUsername.value.username;
 console.log(newUsername.value);
 if (editingUserId.value !== null) {
    try {
      const response = await axios.put(`http://localhost:8080/users/${editingUserId.value}`, {
        newUsername: newUsername.value,
        newAuthority: "USER" // Assuming you want to keep this static or fetch it as needed
      }, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      console.log('User updated successfully');
      window.location.reload();
    } catch (error) {
      console.error('Error updating user:', error);
      handleError(error as AxiosError);
    }
 }
};

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

const goBack = () => {
  window.location.reload();
}

onMounted(() => {
  getUsers();
});

</script>


<template>
  <div>
    <table v-if="editing">
      <thead>
        <tr>
          <th>User ID</th>
          <th>Login</th>
          <th>Role</th>
          <th>Options</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.user.id">
          <td>{{ user.user.id }}</td>
          <td>{{ user.user.username }}</td>
          <td>{{ user.authority }}</td>
          <td><button
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline" @click="deleteUser(user.user.id)"
        type="button">
        Delete
      </button></td>
      <td><button
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline" @click="editUser(user.user.id)"
        type="button">
        Edit
      </button></td>
        </tr>
      </tbody>
    </table>
    <div v-if="error" class="error-message">{{ error }}</div>

    <nav v-if="editing" aria-label="Page navigation example">
    <ul class="inline-flex -space-x-px text-sm">
      <li>
        <button @click.prevent="getUsers(currentPage - 1)" :disabled="currentPage==0" href="#" class="flex items-center justify-center px-3 h-8 ms-0 leading-tight text-gray-500 bg-white border border-e-0 border-gray-300 rounded-s-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">Previous</button>
      </li>
      <li>
        <button aria-current="page" class="flex items-center justify-center px-3 h-8 text-blue-600 border border-gray-300 bg-blue-50 hover:bg-blue-100 hover:text-blue-700 dark:border-gray-700 dark:bg-gray-700 dark:text-white">{{ currentPage + 1 }}</button>
      </li>
      <li>
        <button @click.prevent="getUsers(currentPage + 1)" :disabled="currentPage >= TOTAL_PAGES - 1" class="flex items-center justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 rounded-e-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">Next</button>
      </li>
    </ul>
  </nav>
  </div>

  <form class="bg-white p-8 rounded-md shadow-md przerwa" @submit.prevent="updateUser()" v-if="!editing">
    <div class="mb-4">
      <label for="username" class="block text-gray-700 text-sm font-bold mb-2">New Username</label>
      <input type="text" id="username" placeholder="Username" v-model="editingUsername.username"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
    </div>
    
    <div class="flex items">
      <button
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        type="submit">
        Edytuj
      </button>
      <button @click="goBack()"
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        type="button">
        Confij
      </button>
    </div>
  </form>

</template>