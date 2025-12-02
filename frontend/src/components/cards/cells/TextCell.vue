<script setup lang="ts">
import { ref, useTemplateRef } from 'vue';


const props = defineProps({
    editing: {type: Boolean, required: true},
    cell: {type: String, required: true}
})

const content = defineModel<String>({required: true});

const cellValue = ref<HTMLParagraphElement>();

function update() {
    if (typeof cellValue.value !== 'undefined') {
        content.value = cellValue.value?.innerText;
    }
}
</script>

<template>
    <div v-bind="$attrs" class="text-cell">
        <span class="cell-title">{{ cell }}</span>
        <div class="cell-content">
            <input v-model="content" :class="{edit: editing}" :disabled="!editing"></input>
        </div>
    </div>
</template>

<style scoped>
.text-cell {
    display: flex;
    flex-flow: column nowrap;
    padding: 10px;
    gap: 10px;
    align-items: flex-start;
}

.cell-title {
    font-weight: 700;
    font-size: calc(var(--font-size) * 1.1);
}

.cell-content>input {
    field-sizing: content;
    padding: 7px;
    line-height: 0;
    border-radius: 5px;
    white-space: nowrap;
    text-align: center;
    background-color: var(--color-theme-dual-normal);
    gap: 5px;
    border: 1px solid var(--color-theme-dual-50-mix);
    color: var(--color-theme-dual-reverse);
}

.cell-content>input[disabled="true"] {
    color: var(--color-theme-dual-reverse);
}

.cell-content>input.edit {
    background-color: var(--color-theme-dual-normal);
    border: 1px solid var(--color-main-light);
}

.cell-content>input[disabled="false"]:focus {
    border: 1px solid var(--color-main-light);
}
</style>
