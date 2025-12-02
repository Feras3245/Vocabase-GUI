package vocabase.model;

/**
 * Represents the CEFR (Common European Framework of Reference for Languages) proficiency levels
 * used to classify the difficulty or fluency required to understand a term in the German language.
 *
 * <p>This enum is used to tag dictionary terms based on their linguistic complexity, helping learners
 * identify which words are appropriate for their level.</p>
 *
 * <ul>
 *   <li>{@code A1}, {@code A2} – Beginner levels</li>
 *   <li>{@code B1}, {@code B2} – Intermediate levels</li>
 *   <li>{@code C1}, {@code C2} – Advanced levels</li>
 * </ul>
 *
 * @see Term
 */
public enum Niveau {
    A1,
    A2,
    B1,
    B2,
    C1,
    C2,
}
