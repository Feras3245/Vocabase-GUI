<script setup lang="ts">
import { ref } from 'vue';
import Card from '../Card.vue';
import type { Article, Noun } from '@/types';
import ListCell from '../cells/ListCell.vue';
import TextCell from '../cells/TextCell.vue';
import ArticleCell from '../cells/ArticleCell.vue';
const noun = defineModel<Noun>({required: true})

const editing = ref<boolean>(false);
const refresh = ref<number>(0);

function changeArticle(choice: Article) {
    refresh.value = 0;
    noun.value.article = choice;
    refresh.value++;
}
</script>

<template>
<Card v-model="noun" v-model:editing="editing" term-type="Noun">
    <template v-slot:text-cells>
        <ArticleCell v-model="noun.article"  :editing="editing" v-on:update:model-value="changeArticle" :key="refresh"/>
        <TextCell cell="Plural" v-model="noun.plural"  :editing="editing"/>
        
    </template>

    <template v-slot:cells>
        <ListCell v-model="noun.definitions" cell="Definitions" :editing="editing"/>
        <div class="horizontal-line"></div>
        <ListCell v-model="noun.examples" cell="Examples" :editing="editing"/>
    </template>

    <template v-slot:related-terms>
        <ListCell v-model="noun.synonyms" cell="Synonyms" :editing="editing"/>
        <ListCell v-model="noun.antonyms" cell="Antonyms" :editing="editing"/>
    </template>

</Card>
</template>

<style lang="css" scoped>
</style>