<script setup lang="ts">
import type { Tags } from '@/types';
import { ref } from 'vue';
import Button from '@/components/Button.vue';
import MinusIcon from '@/components/icons/minus-icon.vue';
import PlusIcon from '@/components/icons/plus-icon.vue';

const props = defineProps({
    editing: {type: Boolean, required: true},
})
const content = defineModel<Tags>({required: true});

const refresh = ref<number>(0)

function toggleTag(tag: String) {
    if(content.value && props.editing){
        switch (tag) {
            case "REGULAR":
                content.value.regular = (!content.value.regular)
                break;
            case "DATIVE":
                content.value.dative = (!content.value.dative)
                break;
            case "ACCUSATIVE":
                content.value.accusative = (!content.value.accusative)
                break;
            case "GENITIVE":
                content.value.genitive = (!content.value.genitive)
                break;
            case "SEPARABLE":
                content.value.separable = (!content.value.separable)
                break;
            case "PPII_WITH_SEIN":
                content.value.ppiiWithSein = (!content.value.ppiiWithSein)
                break;
            case "REFLEXIVE":
                content.value.reflexive = (!content.value.reflexive)
                break;

        }
        refresh.value++;
    }
}
</script>
<template>
    <div v-bind="$attrs" class="tags-cell" :key="refresh">
        <span class="cell-title">Tags</span>
        <div class="cell-content">
            <p :class="{ edit: editing }" v-if="content.regular || editing" :data-selected="content.regular" @click="toggleTag('REGULAR')">
                Regular
                <MinusIcon v-if="content.regular && editing" style="color: var(--color-theme-light);" />
                <PlusIcon v-if="!content.regular" />
            </p>
            <p :class="{ edit: editing }" v-if="!content.regular || editing" :data-selected="!content.regular" @click="toggleTag('REGULAR')">
                Irregular
                <MinusIcon v-if="!content.regular && editing" style="color: var(--color-theme-light);" />
                <PlusIcon v-if="content.regular" />
            </p>
            <p :class="{ edit: editing }" v-if="content.dative || editing" :data-selected="content.dative" @click="toggleTag('DATIVE')">
                Dative
                <MinusIcon v-if="content.dative && editing" style="color: var(--color-theme-light);" />
                <PlusIcon v-if="!content.dative" />
            </p>
            <p :class="{ edit: editing }" v-if="content.genitive || editing" :data-selected="content.genitive" @click="toggleTag('GENITIVE')">
                Genitive
                <MinusIcon v-if="content.genitive && editing" style="color: var(--color-theme-light);" />
                <PlusIcon v-if="!content.genitive" />
            </p>
            <p :class="{ edit: editing }" v-if="content.accusative || editing" :data-selected="content.accusative" @click="toggleTag('ACCUSATIVE')">
                Accusative
                <MinusIcon v-if="content.accusative && editing" style="color: var(--color-theme-light);" />
                <PlusIcon v-if="!content.accusative" />
            </p>
            <p :class="{ edit: editing }" v-if="content.separable || editing" :data-selected="content.separable" @click="toggleTag('SEPARABLE')">
                Separable
                <MinusIcon v-if="content.separable && editing" style="color: var(--color-theme-light);" />
                <PlusIcon v-if="!content.separable" />
            </p>
            <p :class="{ edit: editing }" v-if="!content.separable || editing" :data-selected="!content.separable" @click="toggleTag('SEPARABLE')">
                Inseparable
                <MinusIcon v-if="!content.separable && editing" style="color: var(--color-theme-light);" />
                <PlusIcon v-if="content.separable" />
            </p>
            <p :class="{ edit: editing }" v-if="content.ppiiWithSein || editing" :data-selected="content.ppiiWithSein" @click="toggleTag('PPII_WITH_SEIN')">
                PPII w/ Sein
                <MinusIcon v-if="content.ppiiWithSein && editing" style="color: var(--color-theme-light);" />
                <PlusIcon v-if="!content.ppiiWithSein" />
            </p>
            <p :class="{ edit: editing }" v-if="content.reflexive || editing" :data-selected="content.reflexive"  @click="toggleTag('REFLEXIVE')">
                Reflexive
                <MinusIcon v-if="content.reflexive && editing" style="color: var(--color-theme-light);" />
                <PlusIcon v-if="!content.reflexive" />
            </p>
        </div>
    </div>
</template>

<style scoped>

.tags-cell {
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

.cell-content>p {
    padding: 7px;
    border-radius: 5px;
    white-space: nowrap;
    text-align: center;
    background-color: var(--color-theme-dual-normal);
    gap: 5px;
    border: 1px solid var(--color-theme-dual-50-mix);
    gap: 5px;
    display: flex;
    flex-flow: row nowrap;
    align-items: center;
    user-select: none;
    cursor: pointer;
}

.cell-content>p:deep(svg) {
    height: var(--font-size);
}

.cell-content>p.edit {
    background-color: var(--color-theme-dual-normal);
    border: 1px solid var(--color-main-light);
}

.cell-content>p[contenteditable="true"]:focus {
    border: 1px solid var(--color-main-light);
}

.cell-content>p.edit[data-selected="true"] {
    background-color: var(--color-alt-dark);
    color: var(--color-theme-light);
}
</style>
