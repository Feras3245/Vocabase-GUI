<script setup lang="ts">
import type { PropType } from 'vue';
const props = defineProps({
  variant: {
    type: String as PropType<'primary' | 'secondary' | 'tertiary'>,
    required: false,
    validator: (value: string) => ['primary', 'secondary', 'tertiary'].includes(value),
    default: 'primary'
  },
  size: {
    type: Number,
    default: 1
  },
  form: {
    type: String,
    required: false
  }
});
</script>

<template>
  <a v-if="(typeof props.form === 'undefined')" v-bind="$attrs" :class="`button ${variant}`">
    <slot>Link Button</slot>
  </a>

  <button v-else v-bind="$attrs" :form="form" :class="`button ${variant}`">
    <slot>Form Button</slot>
  </button>
</template>

<style scoped>
.button {
  --size: calc(var(--font-size) * v-bind(size));

  all: unset;
  padding: 10px 20px;
  border-radius: 5px;
  user-select: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-weight: 300;
  font-size: var(--size);
  transition: all 0.3s ease-in-out;

  &:deep(svg) {
		height: var(--size);

		/* Add subtle transition for icons */
		transition: transform 200ms ease;
	}
}

a.button.primary[disabled] {
  cursor: unset;
  background-color: var(--color-theme-dual-50-mix);
}

a.button.primary[disabled]:hover {
  cursor: unset;
  background-color: var(--color-theme-dual-50-mix);
  transform: none;
}

a.button.secondary[disabled] {
  cursor: unset;
  box-shadow:inset 0px 0px 0px 1px var(--color-theme-dual-50-mix);
  color: var(--color-theme-dual-50-mix);
}

a.button.secondary[disabled]:hover {
  cursor: unset;
  background-color: unset;
  transform: none;
}


a.button.tertiary[disabled] {
  cursor: unset;
  color: var(--color-theme-dual-50-mix);
}

a.button.tertiary[disabled]:hover {
  cursor: unset;
  transform: none;
}

.button.primary {
  background: var(--color-main-light);
  color: var(--color-theme-light);
}

.button.primary:hover {
  background: var(--color-main-dark);
  transform: translateY(-2px);
}

.button.primary:active {
  background: var(--color-alt-light);
  transform: translateY(0);
}

.button.secondary {
  box-shadow:inset 0px 0px 0px 1px var(--color-main-light);
  color: var(--color-main-light);
}

.button.secondary:hover {
  background: var(--color-main-dark);
  color: var(--color-theme-light);
  box-shadow: none;
  transform: translateY(-2px);
}

.button.secondary:active {
  background: var(--color-alt-light);
  transform: translateY(0);
}

.button.tertiary {
  color: var(--color-main-light);
}

.button.tertiary:hover {
  color: var(--color-main-dark);
}

.button.tertiary:active {
  color: var(var(--color-alt-light));
}
</style>
