<script setup lang="ts">
import Button from "@/components/Button.vue"
import { ref } from "vue";
import type { Term } from "@/types";

const props = defineProps({
    termType: {type: String, required: true},
})

const term = defineModel<Term>({required: true})
const editing = defineModel<boolean>('editing', {required: true})
const reset = ref<Term>(term.value.copy())

function editMode() {
    editing.value = true;
    refresh.value = 0;
}

function saveChanges() {
    editing.value = false;
    reset.value = term.value.copy();
    refresh.value++;
}

function cancelEdit() {
    editing.value = false;
    term.value = reset.value.copy();
    refresh.value++;
}

const refresh = ref<number>(0);
</script>

<template>
    <div class="verb-card">
        <div class="card-head">
            <h3>{{ termType }}</h3>
            <div>
                <Button variant="primary" @click="editMode" v-if="!editing">Edit</Button>
                <Button variant="primary" @click="saveChanges" v-if="editing">Save</Button>
                <Button variant="secondary" @click="cancelEdit" v-if="editing">cancel</Button>
            </div>
        </div>
        <div class="card-body">
            <span class="niveau">{{ term.niveau }}</span>
            <h4>{{ term.term }}</h4>
            <div class="horizontal-line"></div>
            <div class="cells" :key="refresh">
                <div class="text-cells">
                    <slot name="text-cells"></slot>
                </div>
                <div class="horizontal-line"></div>
                <slot name="cells" :editing="editing"></slot>
                <div class="horizontal-line"></div>
                <div class="related-terms">
                    <slot name="related-terms" :editing="editing"></slot>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>

.niveau {
    position: absolute;
    font-size: calc(var(--font-size) * 2);
    font-weight: 800;
    color: var(--color-main-light);
}

.verb-card {
    padding: 15px;
    margin: auto;
    display: flex;
    flex-flow: column nowrap;
    gap: 10px;
    min-width: 320px;
    /* width: min-content; */
    max-width: 700px;
    margin-top: 30px;
}

.card-head {
    display: flex;
    flex-flow: row nowrap;
    align-items: flex-end;
    justify-content: space-between;
}

.card-head>h3 {
    box-sizing: border-box;
    font-size: calc(var(--font-size) * 2);
    font-family: var(--font-alt);
}

.card-head>div {
    display: flex;
    flex-flow: row nowrap;
    gap: 10px;
    align-items: center;
}

.card-body {
    background-color: var(--color-theme-dual-5-mix-normal);
    border-radius: 10px;
    padding: 20px;
    display: flex;
    flex-flow: column nowrap;
    gap: 10px;
    border: 1px solid var(--color-theme-dual-50-mix);
}

.card-body>h4 {
    text-align: center;
    font-size: calc(var(--font-size) * 2);
    font-weight: 900;
    padding: 20px;
}

.cells {
    display: flex;
    flex-flow: column nowrap;
    justify-content: flex-start;
}

.text-cells {
    display: flex;
    flex-flow: row nowrap;
    justify-content: space-between;
    align-items: stretch;
}
.related-terms {
    display: flex;
    flex-flow: row nowrap;
    justify-content: space-around;
}

.related-terms>* {
    width: 50%;
}


</style>
