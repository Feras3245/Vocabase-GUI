<script setup lang="ts">
import { useThemeStore } from '@/stores/ThemeStore';
import { onMounted } from 'vue';
import DarkThemeIcon from './icons/dark-theme-icon.vue';
import LightThemeIcon from './icons/light-theme-icon.vue';
import Button from './Button.vue';

const themeStore = useThemeStore()

function switchTheme(){
    themeStore.toggle();
    document.body.style.colorScheme = themeStore.$state.theme;
}

// set color-scheme css property for document body to theme on app mount
onMounted(() => {
  document.body.style.colorScheme = themeStore.$state.theme;
})
</script>

<template>
<Button @click="switchTheme" :size="2" variant="tertiary" title="change theme">
  <DarkThemeIcon v-if="themeStore.$state.theme === 'light'"/>
  <LightThemeIcon v-else/>
</Button>
<br>
</template>

<style scoped>
.button {
  padding: 10px;
  border-radius: 5px;
}
</style>
