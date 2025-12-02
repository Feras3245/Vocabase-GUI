<script setup lang="ts">
import { onMounted, ref } from 'vue';
import Button from './Button.vue';
import SettingsIcon from './icons/settings-icon.vue';
import CloseIcon from './icons/close-icon.vue';
import QuestionIcon from './icons/question-icon.vue';
import DropdownSelector from './DropdownSelector.vue';
import ReloadIcon from './icons/reload-icon.vue';
import LoadingIcon from './icons/loading-icon.vue';
import { useNotionStore } from '@/stores/NotionStore';

const settings = ref<HTMLDialogElement | null>(null);

function openSettings() {
  settings.value?.showModal();
}

function closeSettings() {
  refreshCollections.value = 0;
  refreshGroups.value = 0;
  settings.value?.close();
}
onMounted(() => {
  settings.value?.close();
  notion.refreshCollections().finally(() => {
    refreshCollections.value++;
    refreshGroups.value++;
  });
})

const notion = useNotionStore();

const refreshCollections = ref<number>(0)
const refreshGroups = ref<number>(0)
</script>

<template>
  <Button :size="2" variant="tertiary" title="view settings" @click="openSettings"><SettingsIcon/></Button>
  <Teleport to="#app">
    <dialog class="settings" closedby="none" ref="settings">
      <div>
        <div class="top">
          <h3>Settings</h3>
          <Button class="close" variant="tertiary" @click="closeSettings"><CloseIcon/></Button>
        </div>
        <div class="input-group">
          <h4><strong>Select Collection</strong>
            <span class="info-tooltip">
              <span role="tooltip">Your Vocabase page must be connected to your internal Notion integration and include at least one collection. Each collection should contain multiple groups. Avoid adding unrelated or non-Vocabase pages to this page to ensure proper functionality.</span>
              <QuestionIcon/>
            </span>
          </h4>
          <div :key="refreshCollections">
            <DropdownSelector class="selector" v-model="notion.selectedCollection" name="collection-selector" title="select collection" :items="notion.collections" @change="notion.refreshGroups(); refreshGroups++"/>
            <Button class="refresh-button" variant="tertiary" @click="notion.refreshCollections(); refreshCollections++" title="refresh collections"><ReloadIcon v-if="!notion.loadingCollections"/><LoadingIcon v-else/></Button>
          </div>
        </div>
        <div class="input-group">
          <h4><strong>Select Group</strong>
            <span class="info-tooltip">
              <span role="tooltip">Each group must include four databases titled “Verbs”, “Nouns”, “Adjectives”, and “Adverbs”. Each database must contain the specific properties outlined <a href="/database_properties.json" target="_blank">here</a>.</span>
              <QuestionIcon/>
            </span>
          </h4>
          <div :key="refreshGroups">
            <DropdownSelector class="selector" v-model="notion.selectedGroup" name="group-selector" title="select group" :items="notion.groups" @change="notion.selectDB()"/>
            <Button class="refresh-button" variant="tertiary" @click="notion.refreshGroups(); refreshGroups++" title="refresh groups"><ReloadIcon v-if="!notion.loadingGroups"/><LoadingIcon v-else/></Button>
          </div>
        </div>
        <div class="divider-line"></div>
        <Button variant="secondary" href="/notion/download">Download Anki Deck</Button>
        <div class="divider-line"></div>
        <div class="input-group">
          <div>
            <Button variant="primary" @click="closeSettings()">Close</Button>
          </div>
        </div>
      </div>
    </dialog>
  </Teleport>
</template>

<style scoped>
.button {
  padding: 10px;
  border-radius: 5px;
}

.input-group>.notion-db-control {
  display: flex;
  flex-flow: row wrap;
  align-items: stretch;
  gap: 10px;
}

.status {
  display: flex;
  flex-flow: row nowrap;
  align-items: center;
  gap: 5px;
}

.status>:deep(svg) {
  height: var(--font-size);
}

.settings {
  padding: 20px;
  border: none;
  border-radius: 10px;
  background-color: var(--color-theme-dual-10-mix-normal);
  z-index: 1000;
  border-image: none;
  overflow: visible;
}

.top {
  display: flex;
  flex-flow: row nowrap;
  align-items: flex-start;
  justify-content: space-between;
}

.top>.close {
  padding: 0;
}

.top>h3 {
  font-family: var(--font-alt);
  font-size: calc(var(--font-size) * 2);
  padding: 10px 5px;
}

.settings>div {
  display: flex;
  flex-flow: column nowrap;
  gap: 15px;
}

.settings:focus-visible {
  outline: none;
}

.settings::backdrop {
  background-color: var(--color-theme-dual-50-mix);
  opacity: 0.5;
}

.input-group {
  display: flex;
  flex-flow: column nowrap;
  gap: 10px;
}

.input-group>h4 {
  display: flex;
  flex-flow: row nowrap;
  align-items: center;
  gap: 10px;
}

.input-group>h4>strong {
  font-size: calc(var(--font-size) * 1.2);
  font-weight: 600;
  padding-left: 3px;
}

.input-group>div {
  display: flex;
  flex-flow: row nowrap;
  align-items: stretch;
  justify-content: space-between;
  gap: 10px;
}

.input-group>div>.selector {
  flex: 1 1 auto;
}

.refresh-button {
  color: var(--color-theme-dual-50-mix) !important;
  padding: 2px;
}

.info-tooltip {
  position: relative;
}

.info-tooltip:hover>span,
.info-tooltip:active>span {
  display: block;
}

.info-tooltip>svg {
  height: var(--font-size);
  color: var(--color-theme-dual-50-mix);
}

.info-tooltip>span {
  position: absolute;
  bottom: 110%;
  left: 50%;
  transform: translateX(-50%);
  padding: 15px;
  background-color: var(--color-theme-dual-normal);
  border-radius: 10px;
  min-width: 500px;
  font-size: calc(var(--font-size) * 0.9);
  line-height: 1.5;
  display: none;
}

.divider-line {
  background-color: var(--color-theme-dual-50-mix);
  height: 2px;
}
</style>
