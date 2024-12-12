<script setup lang="ts">
import Footer from "@/components/Footer.vue";
import Header from "@/components/Header.vue";
import Dashboard from "@/components/Dashboard.vue";
</script>

<template>
  <!-- Wrapper -->
  <div id="wrapper">
    <Header> </Header>

    <!-- Main -->
    <div id="main">
      <!-- Introduction -->
      <section id="intro" class="main">
        <div class="spotlight">
          <div class="content">
            <header class="major">
              <h2>O stronie</h2>
            </header>
            <p>
              Internetowa Baza Filmów to wirtualne sanktuarium dla miłośników
              kina z całego świata. Ta dynamiczna platforma gromadzi nie tylko
              najnowsze premiery, ale również klasyczne dzieła filmowe, tworząc
              bogatą kolekcję, która zaspokoi gusta każdego kinomana. Na stronie
              Internetowej Bazy Filmów użytkownicy mają dostęp do obszernej bazy
              danych zawierającej informacje o filmach i reżyserach. Niezależnie
              od tego, czy ktoś jest fanem kina akcji, dramatu, komedii czy
              horroru, na Filmowej Przystani znajdzie coś dla siebie. Dzięki
              regularnie aktualizowanej zawartości użytkownicy zawsze będą na
              bieżąco z najnowszymi filmami. Internetowa Baza Filmów to nie
              tylko strona internetowa, to miejsce, które inspiruje do
              odkrywania nowych filmowych ścieżek.
            </p>
          </div>
          <span class="image">
            <Dashboard></Dashboard>
          </span>
        </div>
      </section>

      <!-- Get Started -->
      <section id="cta" class="main special">
        <header class="major">
          <h2>IBF</h2>
          <p>Internetowa Baza Filmów</p>
          <p>To koniec strony :)</p>
        </header>
        <footer class="major">
          <ul class="actions special">
            <li><a href="#header" class="button">Opis</a></li>
          </ul>
        </footer>
      </section>
    </div>

    <Footer> </Footer>
  </div>
</template>

<style></style>
