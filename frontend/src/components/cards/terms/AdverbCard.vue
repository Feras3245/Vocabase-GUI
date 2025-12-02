<script setup lang="ts">
import { ref } from 'vue';
import Card from '../Card.vue';
import type { Adverb } from '@/types';
import ListCell from '../cells/ListCell.vue';
const adverb = defineModel<Adverb>({required: true})
const editing = ref<boolean>(false)
</script>

<template>
<Card v-model="adverb" v-model:editing="editing" term-type="Adverb">
    <template v-slot:cells>
        <ListCell v-model="adverb.definitions" cell="Definitions" :editing="editing"/>
        <ListCell v-model="adverb.examples" cell="Examples" :editing="editing"/>
    </template>

    <template v-slot:related-terms>
        <ListCell v-model="adverb.synonyms" cell="Synonyms" :editing="editing"/>
        <ListCell v-model="adverb.antonyms" cell="Antonyms" :editing="editing"/>
    </template>
</Card>
</template>

<style lang="css" scoped>
</style>