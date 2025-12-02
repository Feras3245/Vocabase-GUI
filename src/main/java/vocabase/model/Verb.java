package vocabase.model;

/**
 * Represents a German verb term with additional verb-specific linguistic properties.
 *
 * <p>This class extends {@link Term} and adds fields specific to verbs, such as tense forms,
 * associated prepositions, and grammatical tags.</p>
 *
 * @see Term
 */
public class Verb extends Term {

    /**
     * Grammatical tags associated with the verb (e.g., regular, reflexive, separable).
     */
    private final Tags tags;

    /**
     * The past participle form of the verb (Partizip II).
     */
    private final String ppii;

    /**
     * The preterite (simple past) form of the verb.
     */
    private final String preterite;

    /**
     * Common prepositions that are used with the verb (e.g., ZU+DAT, AN+AKK).
     */
    private final String[] prepositions;


    /**
     * Constructs a new {@code Verb} instance with the given properties.
     *
     * @param term         the verb in its base form (infinitive)
     * @param niveau       the CEFR proficiency level of the verb
     * @param definitions  an array of definitions
     * @param examples     an array of usage examples
     * @param synonyms     an array of synonyms
     * @param antonyms     an array of antonyms
     * @param prepositions common prepositions associated with the verb
     * @param tags         grammatical tags for the verb
     * @param ppii         past participle (Partizip II) form of the verb
     * @param preterite    preterite (simple past) form of the verb
     */
    public Verb(String term, Niveau niveau, String[] definitions, String[] examples, String[] synonyms, String[] antonyms, String[] prepositions, Tags tags, String ppii, String preterite) {
        super(term, WordType.VERB, niveau, definitions, examples, synonyms, antonyms);
        this.tags = tags;
        this.ppii = ppii;
        this.preterite = preterite;
        this.prepositions = prepositions;
    }

    /**
     * Returns the grammatical tags associated with the verb.
     *
     * @return the tags
     */
    public Tags getTags() {
        return tags;
    }

    /**
     * Returns the past participle (Partizip II) form of the verb.
     *
     * @return the past participle form
     */
    public String getPpii() {
        return ppii;
    }

    /**
     * Returns the preterite (simple past) form of the verb.
     *
     * @return the preterite form
     */
    public String getPreterite() {
        return preterite;
    }

    /**
     * Returns an array of common prepositions associated with the verb.
     *
     * @return the prepositions
     */
    public String[] getPrepositions() {
        return prepositions;
    }
}
