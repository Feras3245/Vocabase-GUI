<script setup lang="ts">
import { ref } from 'vue';
import CloseIcon from './icons/close-icon.vue';
import ArrowDownIcon from './icons/arrow-down-icon.vue';

const props = defineProps({
    name: {type: String, required: true},
    labelText: { type: String, required: false },
    placeholder: { type: String, required: false },
    items: {type: Array<String>, required: true,
        validator: (value:Array<String>) => (value.length > 0)
    },
    form: {type: String, required: false}
})
const showDropdown = ref<boolean>(false);

const unselectedItems = ref<Array<String>>(props.items);
const selectedItems = ref<Array<String>>([]);

function selectItem(index:number){
    const item = unselectedItems.value.splice(index, 1)
    selectedItems.value.push(item[0])
}

function deselectItem(index:number){
    const item = selectedItems.value.splice(index, 1)
    unselectedItems.value.push(item[0])
}
</script>

<template>
<div class="dropdown-multi-select" tabindex="-1" @focusin="showDropdown = true" @focusout="showDropdown = false">
    <span>{{ labelText }}</span>
    <div class="dropdown-selection">
        <p v-if="selectedItems.length === 0">{{ placeholder }}</p>
        <ul class="dropdown-selected">
            <li v-for="(item, index) in selectedItems" :key="index" class="dropdown-item selected"><CloseIcon @click="deselectItem(index)"/>{{item}}</li>
        </ul>
        <ArrowDownIcon />
    </div>
        <Transition name="dropdown">
            <div v-show="(showDropdown && (unselectedItems.length > 0))" class="dropdown-list">
                <ul class="dropdown-unselected">
                    <li v-for="(item, index) in unselectedItems" @click="selectItem(index)" :key="index" class="dropdown-item unselected">{{item}}</li>
                </ul>
            </div>
        </Transition>
    <input type="hidden" :id="name" :name="name" :form="form" :value="selectedItems"/>
</div>
</template>

<style scoped lang="css">
.dropdown-multi-select {
    display: flex;
    flex-flow: column nowrap;
    align-items: stretch;
    gap: 10px;
    position: relative;
}

.dropdown-multi-select>span {
    font-weight: 600;
}

.dropdown-selection {
    display: flex;
    flex-flow: row nowrap;
    padding: 0.7rem 0.5rem;
    border: 1px solid var(--color-theme-dual-50-mix);
    border-radius: 5px;
    background-color: var(--color-theme-dual-normal);
    justify-content: flex-start;
    align-items: center;
}

.dropdown-selection>p {
    color: var(--color-theme-dual-50-mix);
    user-select: none;
    cursor: unset;
    font-weight: 500;
}

.dropdown-selection>:deep(svg) {
    margin-left: auto;
    height: 0.7rem;
    transition: transform 0.3s ease;
}

.dropdown-multi-select:focus>.dropdown-selection {
    border-color: var(--color-main-light);
    outline: none;
    
    &>:deep(svg) {
        color: var(--color-main-light);
        transform: rotate(180deg);
    }
}

.dropdown-list {
    position: absolute;
    top: 100%;
    width: 100%;
    padding-top: 10px;
}

.dropdown-list>.dropdown-unselected{
    display: flex;
    flex-flow: column nowrap;
    gap: 10px;
    padding: 10px;
    border-radius: 5px;
    background-color: var(--color-theme-dual-5-mix-normal);
}

.dropdown-selection>.dropdown-selected {
    display: flex;
    flex-flow: row wrap;
    gap: 10px;
}

.dropdown-item {
    padding: 10px;
    border-radius: 5px;
    background-color: var(--color-theme-dual-normal);
    user-select: none;
    cursor: pointer;
    display: flex;
    flex-flow: row nowrap;
    gap: 10px;
    align-items: center;
}

.dropdown-item>:deep(svg){
    height: 0.7rem;
    cursor: pointer;
}

.dropdown-item.selected {
    cursor: unset;
    background-color: var(--color-main-light);
    color: var(--color-theme-light);
}

.dropdown-item.selected:hover {
    cursor: unset;
}

.dropdown-item.unselected:hover {
    background-color: var(--color-main-dark);
}

.dropdown-enter-active {
  transition: all 0.5s ease;
  transform-origin: top center;
}

.dropdown-enter-from {
  opacity: 0;
  transform: translateY(-10px) scaleY(0.95);
}
</style>