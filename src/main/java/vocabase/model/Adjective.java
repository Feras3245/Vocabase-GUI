package vocabase.model;

/**
 * Represents a German adjective term with additional adjective-specific linguistic properties.
 *
 * <p>This class extends {@link Term} and includes the comparative and superlative forms of
 * the adjective.</p>
 *
 * @see Term
 */
public class Adjective extends Term {

    /**
     * The comparative form of the adjective (e.g., "größer" for "groß").
     */
    private final String comparative;

    /**
     * The superlative form of the adjective (e.g., "am größten" for "groß").
     */
    private final String superlative;

    /**
     * Constructs a new {@code Adjective} instance with the given properties.
     *
     * @param term         the adjective in its base (positive) form
     * @param niveau       the CEFR proficiency level of the adjective
     * @param definitions  an array of definitions
     * @param examples     an array of usage examples
     * @param synonyms     an array of synonyms
     * @param antonyms     an array of antonyms
     * @param comparative  the comparative form of the adjective
     * @param superlative  the superlative form of the adjective
     */
    public Adjective(String term, Niveau niveau, String[] definitions, String[] examples, String[] synonyms, String[] antonyms, String comparative, String superlative) {
        super(term, WordType.ADJECTIVE, niveau, definitions, examples, synonyms, antonyms);
        this.comparative = comparative;
        this.superlative = superlative;
    }

    /**
     * Returns the comparative form of the adjective.
     *
     * @return the comparative form
     */
    public String getComparative() {
        return comparative;
    }

    /**
     * Returns the superlative form of the adjective.
     *
     * @return the superlative form
     */
    public String getSuperlative() {
        return superlative;
    }
}
