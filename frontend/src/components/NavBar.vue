<script setup lang="ts">
import SwitchTheme from './SwitchTheme.vue';
import Button from './Button.vue';
import NotionIcon from './icons/notion-icon.vue';
import ViewSettings from './ViewSettings.vue';
import { ref } from 'vue';
import SuccessIcon from './icons/success-icon.vue';
import FailureIcon from './icons/failure-icon.vue';
import LoadingIcon from './icons/loading-icon.vue';
import { storeToRefs } from 'pinia';
import { useSearchStore } from '@/stores/SearchStore';
import { saveToNotion } from '@/api';

const status = ref<'SAVING'|'SUCCESS'|'FAILURE'|'IDLE'>('IDLE')

const { term } = storeToRefs(useSearchStore())
function saveTerm() {
    if (!(term.value === null)) {
        status.value = 'SAVING';
        saveToNotion(term.value).then((success:boolean) => {
            status.value = (success) ? 'SUCCESS' : 'FAILURE';
            if (success) {
                term.value = null;
            }
            setTimeout(() => {
                status.value = 'IDLE'
            }, 5000);
        })
    }
}
</script>

<template>
    <nav>
        <ViewSettings/>
        <SwitchTheme/>
        <form id="notion" action="/" method="post" @submit.lazy.prevent="saveTerm">
            <span class="status" v-if="status !== 'IDLE'">
                <strong v-if="status === 'SAVING'" class="saving"><LoadingIcon/>Saving...</strong>
                <strong v-if="status === 'SUCCESS'" class="success"><SuccessIcon/>Saved!</strong>
                <strong v-if="status === 'FAILURE'" class="failure"><FailureIcon/>Saving Falied!</strong>
            </span>
            <Button form="notion" variant="primary" type="submit"><NotionIcon/>Send To Notion</Button>
        </form>
    </nav>
</template>

<style lang="css" scoped>

nav {
    box-sizing: border-box;
    display: flex;
    flex-flow: row nowrap;
    padding: 10px 20px;
    background-color: var(--color-theme-dual-normal);
}

nav>form {
    margin-left: auto;
    display: flex;
    flex-flow: row nowrap;
    align-items: center;
    gap: 10px;
}

nav>form>:deep(.button) {
    padding: 10px 20px;
    font-weight: 600;
}


.status>strong {
    display: flex;
    flex-flow: row nowrap;
    gap: 5px;
    font-size: calc(var(--font-size) * 1.2);
    align-items: center;
}

.status:deep(svg) {
    height: calc(var(--font-size) * 1.5);
}

.success {
    color: #12ce18;
}

.failure {
    color: #f15b5b;
}

.saving {
    color: var(--color-theme-dual-50-mix);
}
</style>