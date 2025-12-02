package vocabase.model;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type" // this field must exist in the JSON request/response body
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Verb.class, name = "VERB"),
        @JsonSubTypes.Type(value = Noun.class, name = "NOUN"),
        @JsonSubTypes.Type(value = Adjective.class, name = "ADJECTIVE"),
        @JsonSubTypes.Type(value = Adverb.class, name = "ADVERB")
})

/**
 * Represents a generic dictionary term in the German language, used as the parent class
 * for more specific word types such as {@link Verb}, {@link Noun}, {@link Adjective},
 * and {@link Adverb}.
 *
 * <p>This class includes core linguistic properties common to all word types such as definitions, usage examples,
 * synonyms, antonyms, and grammatical metadata.</p>
 *
 * <p>Supports polymorphic JSON deserialization through Jackson annotations based on the {@code type} property.</p>
 *
 * @see Verb
 * @see Noun
 * @see Adjective
 * @see Adverb
 */
public class Term {
    /**
     * The word or phrase being defined.
     */
    private final String term;

    /**
     * An array of definitions associated with the term.
     */
    private final String[] definitions;

    /**
     * An array of example sentences or phrases using the term.
     */
    private final String[] examples;

    /**
     * An array of synonyms for the term.
     */
    private final String[] synonyms;

    /**
     * An array of antonyms for the term.
     */
    private final String[] antonyms;

    /**
     * The CEFR (Common European Framework of Reference) level of the term.
     */
    private final Niveau niveau;

    /**
     * The grammatical type of the term (e.g., VERB, NOUN, ADJECTIVE, ADVERB).
     */
    private final WordType type;


    /**
     * Constructs a new {@code Term} with the given attributes.
     *
     * @param term        the word or phrase
     * @param type        the grammatical type of the term
     * @param niveau      the CEFR proficiency level of the term
     * @param definitions the array of definitions
     * @param examples    the array of usage examples
     * @param synonyms    the array of synonyms
     * @param antonyms    the array of antonyms
     */
    public Term(String term, WordType type, Niveau niveau, String[] definitions, String[] examples, String[] synonyms, String[] antonyms) {
        this.term = term;
        this.type = type;
        this.niveau = niveau;
        this.definitions = definitions;
        this.examples = examples;
        this.synonyms = synonyms;
        this.antonyms = antonyms;
    }

    /**
     * Returns the term (word or phrase).
     *
     * @return the term
     */
    public String getTerm() {
        return term;
    }

    /**
     * Returns the grammatical type of the term.
     *
     * @return the word type
     */
    public WordType getType() {
        return this.type;
    }

    /**
     * Returns an array of definitions for the term.
     *
     * @return the definitions
     */
    public String[] getDefinitions() {
        return definitions;
    }

    /**
     * Returns an array of usage examples for the term.
     *
     * @return the examples
     */
    public String[] getExamples() {
        return examples;
    }

    /**
     * Returns an array of synonyms for the term.
     *
     * @return the synonyms
     */
    public String[] getSynonyms() {
        return synonyms;
    }

    /**
     * Returns an array of antonyms for the term.
     *
     * @return the antonyms
     */
    public String[] getAntonyms() {
        return antonyms;
    }

    /**
     * Returns the CEFR proficiency level of the term.
     *
     * @return the niveau
     */
    public Niveau getNiveau() {
        return niveau;
    }
}
