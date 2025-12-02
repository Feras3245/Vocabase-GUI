package vocabase.model;

/**
 * Represents the grammatical article (gender) of a German noun.
 *
 * <p>This enum is used to specify the gender and form of a noun's article, including
 * cases where a noun may have more than one acceptable article.</p>
 *
 * <ul>
 *   <li>{@code DER} – Masculine</li>
 *   <li>{@code DIE} – Feminine</li>
 *   <li>{@code DAS} – Neuter</li>
 *   <li>{@code DER_DIE} – Masculine or Feminine</li>
 *   <li>{@code DER_DAS} – Masculine or Neuter</li>
 *   <li>{@code DAS_DIE} – Neuter or Feminine</li>
 * </ul>
 *
 * @see Noun
 */
public enum Article {
    DER,
    DIE,
    DAS,
    DER_DIE,
    DER_DAS,
    DAS_DIE
}
