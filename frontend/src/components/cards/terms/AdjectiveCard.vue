<script setup lang="ts">
import { ref } from 'vue';
import Card from '../Card.vue';
import type { Adjective } from '@/types';
import ListCell from '../cells/ListCell.vue';
import TextCell from '../cells/TextCell.vue';
const adjective = defineModel<Adjective>({required: true})
const editing = ref<boolean>(false)
</script>

<template>
<Card v-model="adjective" v-model:editing="editing" term-type="Adjective">
    <template v-slot:text-cells>
        <TextCell cell="Comparative" v-model="adjective.comparative"  :editing="editing"/>
        <TextCell cell="Superlative" v-model="adjective.superlative"  :editing="editing"/>
    </template>

    <template v-slot:cells>
        <ListCell v-model="adjective.definitions" cell="Definitions" :editing="editing"/>
        <ListCell v-model="adjective.examples" cell="Examples" :editing="editing"/>
    </template>

    <template v-slot:related-terms>
        <ListCell v-model="adjective.synonyms" cell="Synonyms" :editing="editing"/>
        <ListCell v-model="adjective.antonyms" cell="Antonyms" :editing="editing"/>
    </template>
</Card>
</template>

<style lang="css" scoped>
</style>