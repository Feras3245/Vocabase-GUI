package vocabase.model;

/**
 * Represents a German noun term with additional noun-specific linguistic properties.
 *
 * <p>This class extends {@link Term} and adds properties specific to nouns, such as grammatical
 * gender (article) and the plural form.</p>
 *
 * @see Term
 */
public class Noun extends Term {

    /**
     * The grammatical article (gender) of the noun (e.g., DER, DIE, DAS).
     */
    private final Article article;

    /**
     * The plural form of the noun.
     */
    private final String plural;

    /**
     * Constructs a new {@code Noun} instance with the given properties.
     *
     * @param term        the noun in its base (singular) form
     * @param niveau      the CEFR proficiency level of the noun
     * @param definitions an array of definitions
     * @param examples    an array of usage examples
     * @param synonyms    an array of synonyms
     * @param antonyms    an array of antonyms
     * @param article     the grammatical article (gender) of the noun
     * @param plural      the plural form of the noun
     */
    public Noun(String term, Niveau niveau, String[] definitions, String[] examples, String[] synonyms, String[] antonyms, Article article, String plural) {
        super(term, WordType.NOUN, niveau, definitions, examples, synonyms, antonyms);
        this.article = article;
        this.plural = plural;
    }

    /**
     * Returns the grammatical article (gender) of the noun.
     *
     * @return the article
     */
    public Article getArticle() {
        return article;
    }

    /**
     * Returns the plural form of the noun.
     *
     * @return the plural form
     */
    public String getPlural() {
        return plural;
    }
}
