package vocabase.model;

/**
 * Represents a set of grammatical tags associated with a German verb.
 *
 * <p>This {@code record} encapsulates properties that describe key grammatical characteristics
 * of verbs in German. It is used in conjunction with the {@link Verb} class as part of to provide detailed metadata.</p>
 *
 * @param regular       indicates if the verb is regular (true) or irregular (false)
 * @param separable     indicates if the verb is separable (trennbar)
 * @param accusative    indicates if the verb takes an accusative object
 * @param dative        indicates if the verb takes a dative object
 * @param genitive      indicates if the verb takes a genitive object (rare)
 * @param reflexive     indicates if the verb is reflexive
 * @param ppiiWithSein  indicates if the past participle (Partizip II) uses "sein" as the auxiliary verb
 *
 * @see Verb
 */
public record Tags(boolean regular,
                   boolean separable,
                   boolean accusative,
                   boolean dative,
                   boolean genitive,
                   boolean reflexive,
                   boolean ppiiWithSein) {}
