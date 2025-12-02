package vocabase.model;

/**
 * Enum representing the type of a dictionary word.
 *
 * <p>This is primarily used to disambiguate between different word categories (e.g., verb, noun)
 * when sending or receiving data between the Java backend and the TypeScript frontend.</p>
 *
 * <p>Each enum constant corresponds to a distinct {@link Term} subclass for polymorphic
 * (de)serialization and processing:</p>
 *
 * <ul>
 *   <li>{@code VERB} – Corresponds to {@link Verb}</li>
 *   <li>{@code NOUN} – Corresponds to {@link Noun}</li>
 *   <li>{@code ADJECTIVE} – Corresponds to {@link Adjective}</li>
 *   <li>{@code ADVERB} – Corresponds to {@link Adverb}</li>
 * </ul>
 *
 * @see Term
 * @see Verb
 * @see Noun
 * @see Adjective
 * @see Adverb
 */
public enum WordType {
    VERB,
    NOUN,
    ADJECTIVE,
    ADVERB
}
