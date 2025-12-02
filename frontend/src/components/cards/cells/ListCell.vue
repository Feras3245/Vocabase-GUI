<script setup lang="ts">
import { onMounted, ref } from 'vue';
import Button from '@/components/Button.vue';
import MinusIcon from '@/components/icons/minus-icon.vue';
import PlusIcon from '@/components/icons/plus-icon.vue';

const props = defineProps({
    editing: { type: Boolean, required: true },
    cell: { type: String, required: true }
})
const content = defineModel<Array<string>>({ required: true });

function remove(index: number) {
    if (content.value) {
        content.value.splice(index, 1);
        render.value++;
    }
}

function add() {
    if (content.value) {
        content.value.push("New_Entry")
        render.value++
    }
}

const render = ref(0);
</script>

<template>
    <div v-bind="$attrs" class="list-cell" :key="render">
        <span class="cell-title">{{ cell }}</span>
        <div class="cell-content">
            <span v-for="(item, index) in content" :class="{ edit: editing }" :key="index">
                <textarea v-model="content[index]" :class="{edit: editing}" :disabled="!editing"></textarea>
                <Button v-if="editing" variant="tertiary" @click="remove(index)">
                    <MinusIcon/>
                </Button>
            </span>
            <Button v-if="editing" variant="tertiary" @click="add()">
                <PlusIcon />
            </Button>
        </div>
    </div>
</template>

<style scoped>
textarea {
    field-sizing: content;
    resize: none;
    padding: 0;
    box-sizing: border-box;
    font-family: var(--font-main);
    font-size: var(--font-size);
    background: transparent;
    border: none;
    color: var(--color-theme-dual-reverse);
}

.list-cell {
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

.cell-content>span {
    padding: 7px;
    border-radius: 5px;
    text-align: center;
    background-color: var(--color-theme-dual-normal);
    gap: 5px;
    border: 1px solid var(--color-theme-dual-50-mix);
    gap: 5px;
    display: flex;
    flex-flow: row nowrap;
    align-items: center;
}

.cell-content>span.edit {
    background-color: var(--color-theme-dual-normal);
    border: 1px solid var(--color-main-light);
}

.cell-content>span>input {
    field-sizing: content;
    border: none;
    outline: none;
    padding: 0;
    line-height: 0;
    box-sizing: border-box;
    background-color: inherit;
    color: var(--color-theme-dual-reverse);
}

.cell-content>span>input[disabled="true"] {
    color: var(--color-theme-dual-reverse);
}

.cell-content>span>input[disabled="true"]:focus {
    border: 1px solid var(--color-main-light);
}

:deep(.button) {
    padding: 0;
}
</style>
