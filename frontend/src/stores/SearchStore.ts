import { fetchCollections, fetchGroups, fetchResult, setDB } from '@/api';
import type { Term } from '@/types';
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useSearchStore = defineStore('search', {
  state: () => {
    const term = ref<Term|null>(null);
    const noResults = ref<boolean>(false);
    const searching = ref<boolean>(false);

    const search = async (query:string) => {
      noResults.value = false;
      const isEmptyString = (str: string) => /^\s*$/.test(str);
      if (!isEmptyString(query)) {
        searching.value = true;
        term.value = null;
        query = query.trim();
        fetchResult(query).then((data:Term|null) => {
          if (data === null) {
            noResults.value = true;
          } else {
            term.value = data;
          }
        }).finally(() => {
          searching.value = false;
        })
      }
    };

    return {term:term, noResults:noResults, searching:searching, search:search};
  }
});