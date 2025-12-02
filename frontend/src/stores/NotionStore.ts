import { fetchCollections, fetchGroups, setDB } from '@/api';
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useNotionStore = defineStore('notion', {
  state: () => {
    const collections = ref<string[]>([]);
    const groups = ref<string[]>([]);
    const selectedCollection = ref<number>(0);
    const selectedGroup = ref<number>(0);
    const loadingCollections = ref<boolean>(false);
    const loadingGroups = ref<boolean>(false);

    const selectDB = async () => {
        setDB(collections.value[selectedCollection.value], groups.value[selectedGroup.value]);
    }

    const refreshGroups = async () => {
        if (!(loadingCollections.value || loadingGroups.value)) {
            loadingGroups.value = true;
            groups.value = [];
            selectedGroup.value = 0;
            fetchGroups(collections.value[selectedCollection.value]).then((data:string[]) => {
                groups.value = data;
            }).finally(() => {
                loadingGroups.value = false;
                selectedGroup.value = groups.value.length - 1;
                selectDB();
            });
        }
    }

    const refreshCollections = async () => {
        if (!loadingCollections.value) {
            loadingCollections.value = true;
            groups.value = [];
            selectedCollection.value = 0;
            fetchCollections().then((data:string[]) => {
                collections.value = data;
            }).finally(() => {
                loadingCollections.value = false;
                selectedCollection.value = collections.value.length - 1;
                refreshGroups();
            });
        }
    }

    return { collections:collections, 
             groups:groups, 
             selectedCollection:selectedCollection, 
             selectedGroup:selectedGroup,
             loadingCollections:loadingCollections,
             loadingGroups:loadingGroups,
             refreshCollections:refreshCollections,
             refreshGroups:refreshGroups,
             selectDB:selectDB
            };
  }
});