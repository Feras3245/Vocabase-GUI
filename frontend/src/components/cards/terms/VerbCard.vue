<script setup lang="ts">
import { onMounted, ref } from 'vue';
import Card from '../Card.vue';
import type { Verb } from '@/types';
import ListCell from '../cells/ListCell.vue';
import TagsCell from '../cells/TagsCell.vue';
import TextCell from '../cells/TextCell.vue';
const verb = defineModel<Verb>({required: true})
const editing = ref<boolean>(false)
</script>

<template>
<Card v-model="verb" v-model:editing="editing" term-type="Verb">
    <template v-slot:text-cells>
        <TextCell cell="PPII" v-model="verb.ppii"  :editing="editing"/>
        <TextCell cell="Preterite" v-model="verb.preterite"  :editing="editing"/>
    </template>

    <template v-slot:cells>
        <ListCell v-model="verb.definitions" cell="Definitions" :editing="editing"/>
        <TagsCell v-model="verb.tags" :editing="editing"/>
        <ListCell v-model="verb.prepositions" cell="Prepositions" :editing="editing"/>
        <ListCell v-model="verb.examples" cell="Examples" :editing="editing"/>
    </template>

    <template v-slot:related-terms>
        <ListCell v-model="verb.synonyms" cell="Synonyms" :editing="editing"/>
        <ListCell v-model="verb.antonyms" cell="Antonyms" :editing="editing"/>
    </template>
</Card>
</template>

<style lang="css" scoped>
</style>