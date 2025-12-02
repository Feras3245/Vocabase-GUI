<script setup lang="ts">
import NavBar from "./components/NavBar.vue";
import Search from "./components/Search.vue";
import { Verb, Adjective, Noun, Term, Adverb } from "./types";
import VerbCard from "./components/cards/terms/VerbCard.vue";
import NounCard from "./components/cards/terms/NounCard.vue";
import AdverbCard from "./components/cards/terms/AdverbCard.vue";
import AdjectiveCard from "./components/cards/terms/AdjectiveCard.vue";
import { storeToRefs } from "pinia";
import { useSearchStore } from "./stores/SearchStore";

const { term } = storeToRefs(useSearchStore())
</script>

<template>
    <NavBar/>
    <Search/>
    <VerbCard v-if="(term instanceof Verb)" v-model="term"/>
    <NounCard v-else-if="(term instanceof Noun)" v-model="term"/>
    <AdjectiveCard v-else-if="(term instanceof Adjective)" v-model="term"/>
    <AdverbCard v-else-if="(term instanceof Adverb)" v-model="term"/>
</template>

<style>
a {
  color: var(--color-main-light);
  font-weight: 600;
}

a:hover {
  color: var(--color-main-dark);
}


a:active {
  color: var(--color-alt-dark);
}
</style>
