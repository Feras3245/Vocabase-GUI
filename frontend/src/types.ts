export class Term {
    term: string;
    readonly type:WordType;
    niveau: Niveau;
    definitions: string[];
    examples: string[];
    synonyms: string[];
    antonyms: string[];
    constructor(term:string, type:WordType, niveau:Niveau, definitions:string[], examples:string[], 
        synonyms:string[], antonyms:string[]) {
            this.term=term;
            this.type=type;
            this.niveau=niveau;
            this.definitions=definitions;
            this.examples=examples;
            this.synonyms=synonyms;
            this.antonyms=antonyms;
    }

    copy() : Term {
        return new Term(
            this.term, 
            this.type, 
            this.niveau, 
            Array.from(this.definitions), 
            Array.from(this.examples),
            Array.from(this.synonyms),
            Array.from(this.antonyms)
        );
    }
}

export class Verb extends Term{
    ppii: string;
    preterite: string;
    prepositions: string[];
    tags: Tags;
    constructor(term:string, niveau:Niveau, definitions:string[], examples:string[], 
        synonyms:string[], antonyms:string[], ppii:string, preterite:string, prepositions:string[], tags:Tags) {
            super(term, 'VERB', niveau, definitions, examples, synonyms, antonyms);
            this.ppii=ppii;
            this.preterite=preterite;
            this.prepositions=prepositions;
            this.tags=tags;
    }

    copy() : Verb {
        const temp = super.copy();
        return new Verb(
            temp.term, 
            temp.niveau, 
            temp.definitions, 
            temp.examples,
            temp.synonyms,
            temp.antonyms,
            this.ppii,
            this.preterite,
            Array.from(this.prepositions),
            {
                regular: this.tags.regular,
                separable: this.tags.separable,
                accusative: this.tags.accusative,
                dative: this.tags.dative,
                genitive: this.tags.genitive,
                reflexive: this.tags.reflexive,
                ppiiWithSein: this.tags.ppiiWithSein
            }
        );
    }
};

export class Noun extends Term {
    article: Article;
    plural: string;
    constructor(term:string, niveau:Niveau, definitions:string[], examples:string[], 
        synonyms:string[], antonyms:string[], article:Article, plural:string) {
            super(term, 'NOUN', niveau, definitions, examples, synonyms, antonyms);
            this.article=article;
            this.plural=plural;
    }

    copy() : Noun {
        const temp = super.copy();
        return new Noun(
            temp.term, 
            temp.niveau, 
            temp.definitions, 
            temp.examples,
            temp.synonyms,
            temp.antonyms,
            this.article,
            this.plural
        );
    }
};

export class Adjective extends Term {
    comparative: string;
    superlative: string;

    constructor(term:string, niveau:Niveau, definitions:string[], examples:string[], 
        synonyms:string[], antonyms:string[], comparative:string, superlative:string) {
            super(term, 'ADJECTIVE', niveau, definitions, examples, synonyms, antonyms);
            this.comparative=comparative;
            this.superlative=superlative;
    }

    copy() : Adjective {
        const temp = super.copy();
        return new Adjective(
            temp.term, 
            temp.niveau, 
            temp.definitions, 
            temp.examples,
            temp.synonyms,
            temp.antonyms,
            this.comparative,
            this.superlative
        );
    }
};


export class Adverb extends Term {
    constructor(term:string, niveau:Niveau, definitions:string[], examples:string[], 
        synonyms:string[], antonyms:string[]) {
            super(term, 'ADVERB', niveau, definitions, examples, synonyms, antonyms);
    }

    copy() : Adverb {
        const temp = super.copy();
        return new Adverb(
            temp.term, 
            temp.niveau, 
            temp.definitions, 
            temp.examples,
            temp.synonyms,
            temp.antonyms,
        );
    }
};

export type Niveau = 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';

export type Tags = {
    regular: boolean,
    separable: boolean,
    accusative: boolean,
    dative: boolean,
    genitive: boolean,
    reflexive: boolean,
    ppiiWithSein: boolean
}

export type Article = 'DAS' | 'DIE' | 'DER' | 'DER_DIE' | 'DER_DAS' | 'DAS_DIE';

export type WordType = 'VERB' | 'NOUN' | 'ADJECTIVE' | 'ADVERB';