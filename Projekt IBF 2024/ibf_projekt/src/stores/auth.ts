import router from "@/router";
import { defineStore } from "pinia";

export const useAuthStore = defineStore({
  id: "auth",
  state: () => ({
    id: localStorage.getItem("id") ? localStorage.getItem("id")! : null,
    user: localStorage.getItem("user")
      ? JSON.parse(localStorage.getItem("user")!)
      : null,
    authority: localStorage.getItem("authority")
      ? JSON.parse(localStorage.getItem("authority")!)
      : null,
    token: localStorage.getItem("token")
      ? JSON.parse(localStorage.getItem("token")!)
      : null,
    returnUrl: "/",
  }),
  actions: {
    async login(username: string, password: string) {
      console.log(
        "In login function for user: ",
        username,
        " and password: ",
        password
      );

      const response = await fetch("http://localhost:8080/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
      });

      if (response.ok) {
        const token = await response.text();

        let nazwa = JSON.stringify(username).slice(1, -1);
        const authorityResponse = await fetch(
          "http://localhost:8080/authority/" + nazwa,
          {
            method: "GET",
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (authorityResponse.ok) {
          const authorities = await authorityResponse.json();
          const authority = authorities.authority;
          console.log(authority);
          console.log(authorities.user.id);

          localStorage.setItem("user", JSON.stringify(username));
          localStorage.setItem("id", authorities.user.id);
          localStorage.setItem("authority", JSON.stringify(authority));
          localStorage.setItem("token", JSON.stringify(token));
          this.user = username;
          this.authority = authority;
          this.token = token;

          const isTokenExpired = () =>
            Date.now() >= JSON.parse(atob(token.split(".")[1])).exp * 1000;
          if (!isTokenExpired()) {
            router.push(this.returnUrl || "/");
            window.location.reload();
          } else {
            this.logout();
          }
        } else {
          throw new Error("Failed to fetch authority");
        }
      } else {
        throw new Error("Invalid credentials");
      }
    },
    logout() {
      this.user = "";
      this.id = "";
      this.authority = "";
      this.token = "";
      localStorage.removeItem("user");
      localStorage.removeItem("id");
      localStorage.removeItem("authority");
      localStorage.removeItem("token");
      localStorage.removeItem("cachedImages");
      router.push("/login");
      window.location.reload();
    },
    isTokenExpired() {
      if (this.token != null) {
        const expired = () =>
          Date.now() >= JSON.parse(atob(this.token.split(".")[1])).exp * 1000;
        console.log("Token expired? ", expired());
        if (expired()) {
          this.logout();
        }
      }
    },
  },
});
