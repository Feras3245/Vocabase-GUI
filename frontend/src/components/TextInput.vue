<script setup lang="ts">
import { ref } from 'vue';

const props = defineProps({
    name: {type:String, required: true},
    labelText: {type:String, required: false},
});

const value = defineModel<String>();
</script>

<template>
<label :for="name" class="text-input">
    <span v-if="(typeof labelText) !== 'undefined'">{{ labelText }}</span>
    <input v-model="value" v-bind="$attrs" :title="labelText" :name="name" :id="name"/>
</label>
</template>

<style scoped lang="css">
.text-input {
    display: flex;
    flex-flow: column nowrap;
    align-items: stretch;
    gap: 10px;
}

.text-input > span {
    font-weight: 600;
}

.text-input > input {
    box-sizing: border-box;
    padding: 0.7rem 0.5rem;
    border: 1px solid var(--color-theme-dual-50-mix);
    border-radius: 5px;
    background-color: var(--color-theme-dual-normal);
}

.text-input > input::placeholder {
    color: var(--color-theme-dual-50-mix);
    font-weight: 500;
}

.text-input > input:focus {
    border-color: var(--color-main-light);
    outline: none;
}
</style>