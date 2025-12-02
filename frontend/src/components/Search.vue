<script setup lang="ts">
import { ref } from 'vue';
import Button from './Button.vue';
import LogoIcon from './icons/logo-icon.vue';
import SearchIcon from './icons/search-icon.vue';
import TextInput from './TextInput.vue';
import { fetchResult } from '@/api';
import { Term } from '@/types';
import LoadingIcon from './icons/loading-icon.vue';
import NoResultsBanner from './NoResultsBanner.vue';
import { useSearchStore } from '@/stores/SearchStore';

const query = ref<string>('');
const search = useSearchStore();
</script>

<template>
    <div class="search-container">
        <div class="site-title">
            <LogoIcon />
            <div>
                <h1>Vocabase</h1>
                <span>Online Dictionary Scraper</span>
            </div>
        </div>
        <form autocomplete="off" id="search-form" class="search-form" method="get" action="/" @submit.lazy.prevent="search.search(query)" tabindex="0" aria-label="search" role="searchbox"> 
            <TextInput class="search-text-input" name="query" form="search-form" type="text" autocomplete="off" placeholder="Search" v-model="query"/>
            <Button variant="primary" form="search-form" type="submit">
                <SearchIcon/>Search
            </Button>
        </form>
        <LoadingIcon v-if="search.searching" class="loading-spinner"/>
        <NoResultsBanner v-if="search.noResults"/>
    </div>
</template>

<style scoped>
.loading-spinner {
    height: 80px;
    color: var(--color-main-light);
}

.search-container {
    display: flex;
    flex-flow: column nowrap;
    gap: 50px;
    margin: auto;
    box-sizing: border-box;
}


.site-title {
    user-select: none;
    display: flex;
    flex-flow: row nowrap;
    align-items: center;
    gap: 20px;
    margin: auto;
    justify-content: center;
}

.site-title>:deep(svg) {
    width: 150px;
}

.site-title>div {
    display: flex;
    flex-flow: column nowrap;
    text-align: center;
}

.site-title>div>h1 {
    font-size: calc(var(--font-size) * 4);
    font-family: var(--font-alt);
}

.site-title>div>span {
    font-size: calc(var(--font-size) * 2);
    font-family: var(--font-main);
}

.search-form {
    display: flex;
    flex-flow: row nowrap;
    gap: 20px;
    align-items: stretch;
    justify-content: center;
    height: fit-content;
}

.search-text-input {
    width: 40%;
}

@media screen and (max-width: 850px) {
    .search-text-input {
        width: 70%;
    }
}

@media screen and (max-width: 640px) {
    .site-title {
        flex-flow: column nowrap;
    }

    .site-title>div>h1 {
        font-size: calc(var(--font-size) * 3.5);
    }

    .site-title>div>span {
        font-size: calc(var(--font-size) * 1.5);
    }

    .search-form {
        display: flex;
        flex-flow: column nowrap;
        gap: 20px;
        align-items: center;
        justify-content: center;
        height: fit-content;
    }

    .search-text-input {
        width: unset;
        margin-left: 20px;
        margin-right: 20px;
        align-self: stretch;
    }

}
</style>
