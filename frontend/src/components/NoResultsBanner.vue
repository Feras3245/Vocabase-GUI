<script setup lang="ts">
import EmojiIcon from './icons/emoji-icon.vue';
</script>

<template>
    <div class="no-results-banner">
        <EmojiIcon/>
        <h2>Sorry! No terms were scraped :(</h2>
        <p>The scraper was unable to find any related terms. Please try again</p>
    </div>
</template>

<style scoped>
:deep(svg){
    height: 100px;
    color: var(--color-main-light);
}

.no-results-banner {
    text-align: center;
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 30px;
    gap: 20px;
}

.no-results-banner > h2 {
    font-family: var(--font-alt);
    font-size: calc(var(--font-size) * 1.5);
}

.no-results-banner > p {
    font-size: calc(var(--font-size) * 1.2);
}
</style>
