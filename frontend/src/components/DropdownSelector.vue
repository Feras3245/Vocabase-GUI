<script setup lang="ts">
import { ref } from 'vue';

const props = defineProps({
    name: {type: String, required: true},
    items: {type: Array<String>, required: true,
        validator: (value:Array<String>) => (value.length > 0)
    },
});

const selected = defineModel<number>({required:true})
</script>

<template>
  <select :name="name" v-bind="$attrs" :id="name" v-model="selected">
    <option v-for="(item, index) in items" :value="index">{{ item }}</option>
  </select>
</template>

<style scoped lang="css">

select {
  appearance: base-select;
  border-radius: 5px;
  padding: 10px;
  border: 1px solid var(--color-theme-dual-50-mix);
  position: relative;
  outline: none;
}

select:hover {
  background-color: var(--color-theme-dual-normal);
  color: var(--color-main-light);
}

select::picker-icon {
  color: var(--color-theme-dual-50-mix);
  transition: 0.4s rotate;
}

select:hover::picker-icon{
  color: var(--color-main-light);
}
select:open {
  border: 1px solid var(--color-main-light);
}
select:open::picker-icon {
  color: var(--color-main-light);
  rotate: 180deg;
}

select::picker(select) {
  appearance: base-select;
  border-radius: 5px;
  border: 1px solid var(--color-theme-dual-50-mix);
 }

 select>option::checkmark {
  display: none;
 }

 select:open>option:hover {
  background-color: var(--color-main-light-10-opacity);
 }

 .arrow {
  width: 1rem;
 }
</style>