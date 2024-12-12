<script setup lang="ts">
import { useAuthStore } from "@/stores/auth";
import axios, { AxiosError } from "axios";
import { onMounted, ref } from "vue";

const auth = useAuthStore();
const token = auth.token;
const usersData = ref([]);
const error = ref<string | null>(null);
const currentPage = ref(0);
const SINGLE_PAGE_SIZE = ref(0);
const CURRENT_PAGE_NUMBER = ref(0);
const idOfUserBeingEdited = ref<number | null>(null);
const usernameBeingEdited = ref({
  username: "",
});
const newUsername = ref<string>("");
let countRequested = false;
const editing = ref(true);

const deleteUser = async (id: number) => {
  if (confirm("Are you sure you want to delete this record?")) {
    try {
      await axios.delete(`http://localhost:8080/authority/${id}`, {
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

const getUsersCount = async () => {
  try {
    const response = await axios.get("http://localhost:8080/authority/count", {
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

const getUsers = async (page: number = 0) => {
  try {
    if (!countRequested) {
      await getUsersCount();
    }

    const response = await axios.get("http://localhost:8080/authority", {
      params: {
        page: page,
      },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    CURRENT_PAGE_NUMBER.value = usersData.value.length;
    usersData.value = response.data;
    currentPage.value = page;
  } catch (error) {
    handleError(error as AxiosError);
  }
};

interface CustomAxiosError extends AxiosError {
  value?: string;
}

const editUser = (userId: number) => {
  idOfUserBeingEdited.value = userId;
  editing.value = !editUser;
};

const updateUser = async () => {
  newUsername.value = usernameBeingEdited.value.username;
  if (idOfUserBeingEdited.value !== null) {
    try {
      await axios.put(
        `http://localhost:8080/users/${idOfUserBeingEdited.value}`,
        {
          newUsername: newUsername.value,
          newAuthority: "USER",
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
  }
};

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

const goBack = () => {
  window.location.reload();
};

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
        <tr v-for="user in usersData" :key="user.user.id">
          <td>{{ user.user.id }}</td>
          <td>{{ user.user.username }}</td>
          <td>{{ user.authority }}</td>
          <td>
            <button
              class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
              @click="deleteUser(user.user.id)"
              type="button"
            >
              Usuń
            </button>
          </td>
          <td>
            <button
              class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
              @click="editUser(user.user.id)"
              type="button"
            >
              Edytuj
            </button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="error" class="error-message">{{ error }}</div>

    <nav v-if="usernameBeingEdited" aria-label="Page navigation example">
      <ul class="inline-flex -space-x-px text-sm">
        <li>
          <button
            @click.prevent="getUsers(currentPage - 1)"
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
            @click.prevent="getUsers(currentPage + 1)"
            class="flex items-center justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 rounded-e-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
          >
            Dalej
          </button>
        </li>
      </ul>
    </nav>
  </div>

  <form
    class="bg-white p-8 rounded-md shadow-md przerwa"
    @submit.prevent="updateUser()"
    v-if="!editing"
  >
    <div class="mb-4">
      <label for="username" class="block text-gray-700 text-sm font-bold mb-2"
        >Nazwa Użytkownika</label
      >
      <input
        type="text"
        id="username"
        placeholder="Username"
        v-model="usernameBeingEdited.username"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
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
        @click="goBack()"
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        type="button"
      >
        Cofnij
      </button>
    </div>
  </form>
</template>
