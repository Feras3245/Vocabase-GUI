import { Adjective, Adverb, Noun, Verb, type Term } from "@/types";

export async function fetchCollections() : Promise<string[]> {
    const response = await fetch('notion/collections');
    if (!response.ok) throw new Error('Failed to fetch collections');
    return response.json();
}


export async function fetchGroups(collection:string) : Promise<string[]> {
    const response = await fetch('notion/groups/' + collection);
    if (!response.ok) throw new Error('Failed to fetch collections');
    return response.json();
}

export async function setDB(collection:string, group: string) : Promise<void> {
    const response = await fetch("/notion/db", {
        headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({collection: collection, group: group})
    });
    if (!response.ok) throw new Error('Failed to select DB group');
}

export async function fetchResult(query:string) : Promise<Term|null> {
    try {
      const response = await fetch(`/search?query=${encodeURIComponent(query)}`);
      if (!response.ok) {
        throw new Error(`ERROR ${response.status}: unable to retrieve search results`);
      }
        const data = await response.json();
        if (!(data === undefined || data === null)) {
            switch (data.type) {
                case 'VERB':
                    const temp_verb = data as Verb;
                    return new Verb(temp_verb.term, temp_verb.niveau, temp_verb.definitions, temp_verb.examples, 
                        temp_verb.synonyms, temp_verb.antonyms, temp_verb.ppii, temp_verb.preterite, temp_verb.prepositions, temp_verb.tags)
                case 'NOUN':
                    const temp_noun = data as Noun;
                    return new Noun(temp_noun.term, temp_noun.niveau, temp_noun.definitions, temp_noun.examples, 
                        temp_noun.synonyms, temp_noun.antonyms, temp_noun.article, temp_noun.plural);
                case 'ADJECTIVE':
                    const temp_adjective = data as Adjective;
                    return new Adjective(temp_adjective.term, temp_adjective.niveau, temp_adjective.definitions, temp_adjective.examples, 
                        temp_adjective.synonyms, temp_adjective.antonyms, temp_adjective.comparative, temp_adjective.superlative);
                case 'ADVERB':
                    const temp_adverb = data as Adverb;
                    return new Adverb(temp_adverb.term, temp_adverb.niveau, temp_adverb.definitions, temp_adverb.examples, 
                        temp_adverb.synonyms, temp_adverb.antonyms)
                default:
                    return null;
            }
        } else {
            return null;
        }
    } catch (error) {
      return null;
    }
  }

  export async function saveToNotion(term:Term) : Promise<boolean> {
    try {
        const response = await fetch("/notion/save", {
            headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
            },
            method: "POST",
            body: JSON.stringify(term)
        });

        const data: {success:boolean} = await response.json();
        return data.success;
    } catch (error) {
        console.log(error)
        return false;
    }
  }