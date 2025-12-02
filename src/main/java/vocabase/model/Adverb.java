package vocabase.model;

/**
 * Represents a German adverb term.
 *
 * <p>This class extends {@link Term} but does not introduce any additional properties
 * specific to adverbs. It serves as a type distinction for polymorphic deserialization
 * and logical separation.</p>
 *
 * @see Term
 */
public class Adverb extends Term {

    /**
     * Constructs a new {@code Adverb} instance with the given properties.
     *
     * @param term        the adverb in its base form
     * @param niveau      the CEFR proficiency level of the adverb
     * @param definitions an array of definitions
     * @param examples    an array of usage examples
     * @param synonyms    an array of synonyms
     * @param antonyms    an array of antonyms
     */
    public Adverb(String term, Niveau niveau, String[] definitions, String[] examples, String[] synonyms, String[] antonyms) {
        super(term, WordType.ADVERB, niveau, definitions, examples, synonyms, antonyms);
    }
}
