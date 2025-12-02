<script setup lang="ts">
import FailureIcon from './icons/failure-icon.vue';
import SuccessIcon from './icons/success-icon.vue';

const props = defineProps({
    success: {type:Boolean, required: true}
});
</script>

<template>
    <div class="results-banner">
        <SuccessIcon class="success" v-if="success"/>
        <FailureIcon class="failure" v-else/>
        <h2 v-if="success">Success!</h2>
        <h2 v-else>Failure!</h2>
        <p v-if="success">Vocabulary terms were successfully<br>inserted into Notion Database!</p>
        <p v-else>Something went wrong<br>Please try again...</p>
    </div>
</template>

<style scoped>
:deep(svg){
    height: 100px;
    color: var(--color-main-light);
}

.results-banner {
    text-align: center;
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 30px;
    gap: 20px;
}

.results-banner > h2 {
    font-family: var(--font-alt);
    font-size: calc(var(--font-size) * 1.5);
}

.results-banner > p {
    line-height: 1.3;
    font-size: calc(var(--font-size) * 1.2);
}

.results-banner > p > a {
  color: var(--color-main-light);
  font-weight: 600;
}

.results-banner > p > a:hover {
  color: var(--color-main-dark);
}

.results-banner > p > a:active {
  color: var(--color-alt-dark);
}

.success {
    color: #12ce18;
}

.failure {
    color: #f15b5b;
}
</style>
