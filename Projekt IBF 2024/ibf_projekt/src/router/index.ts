import { useAuthStore } from "@/stores/auth";
import AboutView from "@/views/AboutView.vue";
import AdminView from "@/views/AdminView.vue";
import DashboardView from "@/views/DashboardView.vue";
import DirectorsView from "@/views/DirectorsView.vue";
import LoginView from "@/views/LoginView.vue";
import MoviesView from "@/views/MoviesView.vue";
import RegisterView from "@/views/RegisterView.vue";
import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "Dashboard",
      component: DashboardView,
    },
    {
      path: "/register",
      name: "Register",
      component: RegisterView,
    },
    {
      path: "/login",
      name: "Login",
      component: LoginView,
    },
    {
      path: "/admin",
      name: "Admin",
      component: AdminView,
    },
    {
      path: "/movies",
      name: "Movies",
      component: MoviesView,
    },
    {
      path: "/directors",
      name: "Directors",
      component: DirectorsView,
    },
    {
      path: "/about",
      name: "About",
      component: AboutView,
    },
  ],
});

router.beforeEach(async (to) => {
  const publicPages = ["/login", "/register", "/"];
  const authPages = ["/login", "/register"];
  const authRequired = !publicPages.includes(to.path);
  const authNotRequired = !authPages.includes(to.path);
  const auth = useAuthStore();

  if (authRequired && !auth.user) {
    return "/login";
  } else if (!authNotRequired && auth.user) {
    const previousRoute = router.currentRoute.value;
    if (previousRoute && previousRoute.path !== to.path) {
      return previousRoute.path;
    } else {
      return "/";
    }
  }
});

export default router;
