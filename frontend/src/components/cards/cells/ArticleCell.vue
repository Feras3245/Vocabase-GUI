<script setup lang="ts">
import type { Article } from '@/types';

const props = defineProps({
    editing: {type: Boolean, required: true},
})
const content = defineModel<Article>({required: true});
</script>
<template>
    <div v-bind="$attrs" class="article-cell">
        <span class="cell-title">Article</span>
        <div class="cell-content">
            <p :class="{ edit: editing }" v-if="content === 'DER' || editing" :data-selected="content === 'DER'" @click="$emit('update:modelValue', 'DER')">Der</p>
            <p :class="{ edit: editing }" v-if="content === 'DIE' || editing" :data-selected="content === 'DIE'" @click="$emit('update:modelValue', 'DIE')">Die</p>
            <p :class="{ edit: editing }" v-if="content === 'DAS' || editing" :data-selected="content === 'DAS'" @click="$emit('update:modelValue', 'DAS')">Das</p>
            <p :class="{ edit: editing }" v-if="content === 'DER_DIE' || editing" :data-selected="content === 'DER_DIE'" @click="$emit('update:modelValue', 'DER_DIE')">Der/Die</p>
            <p :class="{ edit: editing }" v-if="content === 'DER_DAS' || editing" :data-selected="content === 'DER_DAS'" @click="$emit('update:modelValue', 'DER_DAS')">Der/Das</p>
            <p :class="{ edit: editing }" v-if="content === 'DAS_DIE' || editing" :data-selected="content === 'DAS_DIE'" @click="$emit('update:modelValue', 'DAS_DIE')">Das/Die</p>
        </div>
    </div>
</template>

<style scoped>

.article-cell {
    display: flex;
    flex-flow: column wrap;
    padding: 10px;
    gap: 10px;
    align-items: flex-start;
}

.cell-title {
    font-weight: 700;
    font-size: calc(var(--font-size) * 1.1);
}

.cell-content {
    display: flex;
    flex-flow: row wrap;
    justify-content: flex-start;
    gap: 5px;
}

.cell-content>p {
    padding: 7px;
    border-radius: 5px;
    white-space: nowrap;
    text-align: center;
    background-color: var(--color-theme-dual-normal);
    gap: 5px;
    border: 1px solid var(--color-theme-dual-50-mix);
    gap: 5px;
    display: flex;
    flex-flow: row nowrap;
    align-items: center;
    user-select: none;
    cursor: pointer;
}

.cell-content>p.edit {
    background-color: var(--color-theme-dual-normal);
    border: 1px solid var(--color-main-light);
}

.cell-content>p[contenteditable="true"]:focus {
    border: 1px solid var(--color-main-light);
}

:deep(.button) {
    padding: 0;
}

.cell-content>p.edit[data-selected="true"] {
    background-color: var(--color-alt-dark);
    color: var(--color-theme-light);
}
</style>
