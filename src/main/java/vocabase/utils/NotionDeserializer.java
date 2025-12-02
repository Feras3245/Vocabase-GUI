package vocabase.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import vocabase.model.*;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Utility class responsible for deserializing JSON payloads from the Notion API into {@link Term} objects
 * (including their subclasses: {@link Noun}, {@link Verb}, {@link Adjective}, and {@link Adverb}).
 * <p>
 * The {@code deserialize(JsonNode properties, WordType wordType)} public method processes properties of
 * results from the Notion API to reconstruct vocabulary term objects with their
 * full grammatical attributes. The class handles conversion of Notion property types (select, multi-select,
 * rich text, etc.) into Java model fields.
 * <p>
 * This class is used by {@link vocabase.service.NotionService} when retrieving vocabulary data from Notion.
 * It uses Jackson's {@link JsonNode} to navigate the JSON structure and extract values according to the
 * expected Notion database schema.
 *
 * <h2>Property Conversion</h2>
 * <p>Helper methods transform Notion properties into Java types:</p>
 * <ul>
 *   <li>{@code textProperty} – extracts content from "rich_text" or "title" fields</li>
 *   <li>{@code selectProperty} – retrieves single-select values (e.g., article or level)</li>
 *   <li>{@code multiSelectProperty} – converts multi-select options to arrays (e.g., tags or prepositions)</li>
 *   <li>{@code listProperty} – splits concatenated lists (definitions, examples) using " / " delimiter</li>
 * </ul>
 *
 * <h2>Supported Term Types</h2>
 * <ul>
 *   <li>{@link Noun} – reconstructs article, plural form, definitions, and examples</li>
 *   <li>{@link Verb} – processes conjugations, tags, prepositions, and grammatical attributes</li>
 *   <li>{@link Adjective} – handles comparative/superlative forms and related fields</li>
 *   <li>{@link Adverb} – extracts standard term properties</li>
 * </ul>
 *
 * @see vocabase.model.Term
 * @see vocabase.service.NotionService
 * @see NotionSerializer
 * @see <a href="https://developers.notion.com/reference/property-object">Notion Property Object Reference</a>
 */
public class NotionDeserializer {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    /**
     * Deserializes a Notion API JSON payload into a specific {@link Term} subclass based on the provided
     * {@link WordType}.
     *
     * <p>This method acts as the main entry point, examining the properties node from a Notion API response's result
     * and delegating to type-specific logic to construct the appropriate term object. It handles common
     * properties (definitions, examples, synonyms, antonyms) before processing type-specific fields.</p>
     *
     * <p>Example input structure (simplified):
     * <pre>
     * {
     *   "properties": {
     *     "Noun": { "title": [{ "plain_text": "Haus" }] },
     *     "Niveau": { "select": { "name": "A1" } },
     *     "Article": { "select": { "name": "Das" } },
     *     "Plural": { "rich_text": [{ "plain_text": "Häuser" }] },
     *     "Definitions": { "rich_text": [{ "plain_text": "Building / Residence" }] },
     *     "Examples": { "rich_text": [{ "plain_text": "Ich gehe nach Hause." }] }
     *   }
     * }
     * </pre>
     * </p>
     *
     * @param properties the {@link JsonNode} containing Notion property data from an API response's result
     * @param wordType   the {@link WordType} enum indicating which term subclass to construct
     * @return a reconstructed {@link Term} object of the appropriate subclass
     * @throws NullPointerException if required properties are missing in the JSON input
     */
    public static Term deserialize(JsonNode properties, WordType wordType) {
        String[] definitions = listProperty(properties.get("Definitions"));
        String[] synonyms = listProperty(properties.get("Synonyms"));
        String[] antonyms = listProperty(properties.get("Antonyms"));
        String[] examples = listProperty(properties.get("Examples"));
        Niveau niveau = formatNiveau(selectProperty(properties.get("Niveau")));
        return switch (wordType) {
            case WordType.VERB: {
                String term = titleProperty(properties.get("Verb"));
                String[] prepositions = multiSelectProperty(properties.get("Prepositions"));
                Tags tags = formatTags(multiSelectProperty(properties.get("Tags")));
                String preterite = textProperty(properties.get("Preterite"));
                String ppii = textProperty(properties.get("PPII"));
                yield new Verb(term, niveau, definitions, examples, synonyms, antonyms, prepositions, tags, ppii, preterite);
            }
            case WordType.NOUN: {
                String term = titleProperty(properties.get("Noun"));
                Article article = formatArticle(selectProperty(properties.get("Article")));
                String plural = textProperty(properties.get("Plural"));
                yield new Noun(term, niveau, definitions, examples, synonyms, antonyms, article, plural);
            }
            case WordType.ADJECTIVE: {
                String term = titleProperty(properties.get("Adjective"));
                String comparative = textProperty(properties.get("Comparative"));
                String superlative = textProperty(properties.get("Superlative"));
                yield new Adjective(term, niveau, definitions, examples, synonyms, antonyms, comparative, superlative);
            }
            case WordType.ADVERB: {
                String term = titleProperty(properties.get("Adverb"));
                yield new Adverb(term, niveau, definitions, examples, synonyms, antonyms);
            }
        };
    }

    /**
     * Converts an array of Notion multi-select tags into a {@link Tags} object representing
     * verb grammatical attributes.
     *
     * <p>This method parses the string labels from Notion's multi-select field and sets
     * corresponding boolean flags in the {@code Tags} object. It handles all supported
     * verb characteristics including regularity, separability, grammatical cases, and
     * auxiliary verb usage.</p>
     *
     * @param tags an array of string labels from Notion's multi-select field
     * @return a {@link Tags} object with boolean attributes set according to the input tags
     */
    private static Tags formatTags(String[] tags) {
        boolean regular = false;
        boolean separable = false;
        boolean accusative = false;
        boolean dative = false;
        boolean genitive = false;
        boolean reflexive = false;
        boolean ppiiWithSein = false;
        for (String tag : tags) {
            if (Objects.equals(tag, "Regular"))
                regular = true;
            else if (Objects.equals(tag, "Separable"))
                separable = true;
            else if (Objects.equals(tag, "Accusative"))
                accusative = true;
            else if (Objects.equals(tag, "Dative"))
                dative = true;
            else if (Objects.equals(tag, "Genitive"))
                genitive = true;
            else if (Objects.equals(tag, "Reflexive"))
                reflexive = true;
            else if (Objects.equals(tag, "PPII Mit Sein"))
                ppiiWithSein = true;
        }
        return new Tags(regular, separable, accusative, dative, genitive, reflexive, ppiiWithSein);
    }

    /**
     * Converts a Notion select property value into an {@link Article} enum constant.
     *
     * <p>This method maps human-readable article strings ("Der", "Die", "Das", etc.) back to their
     * corresponding enum values. Returns null for unrecognized input.</p>
     *
     * @param article the string value from Notion's select property
     * @return the corresponding {@link Article} enum constant, or null if no match found
     */
    private static Article formatArticle(String article) {
        return switch (article) {
            case "Der" -> Article.DER;
            case "Die" -> Article.DIE;
            case "Das" -> Article.DAS;
            case "Der/Die" -> Article.DER_DIE;
            case "Der/Das" -> Article.DER_DAS;
            case "Das/Die" -> Article.DAS_DIE;
            default -> null;
        };
    }

    /**
     * Converts a Notion select property value into a {@link Niveau} enum constant.
     *
     * <p>Attempts to match the input string against {@link Niveau} enum values. Returns null
     * if the input doesn't correspond to any known niveau or is empty.</p>
     *
     * @param niveau the string value from Notion's select property
     * @return the corresponding {@link Niveau} enum constant, or null if no match found
     */
    private static Niveau formatNiveau(String niveau) {
        try {
            return Niveau.valueOf(niveau);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Extracts and splits a concatenated list from a Notion rich text property.
     *
     * <p>This method processes fields like definitions or examples where multiple values
     * are stored as a single string separated by " / ". Returns an empty array if the
     * input property contains no text.</p>
     *
     * @param property the {@link JsonNode} containing a Notion rich_text property
     * @return an array of strings split from the concatenated content
     */
    private static String[] listProperty(JsonNode property) {
        String plainText = property.get("rich_text").get(0).get("plain_text").asText();
        if (plainText.isBlank())
            return new String[]{};
        else
            return plainText.split(" / ");
    }

    /**
     * Extracts text content from a Notion rich text property.
     *
     * @param property the {@link JsonNode} containing a Notion rich_text property
     * @return the plain text content as a string
     */
    private static String textProperty(JsonNode property) {
        return property.get("rich_text").get(0).get("plain_text").asText();
    }

    /**
     * Extracts text content from a Notion title property.
     *
     * @param property the {@link JsonNode} containing a Notion title property
     * @return the plain text content as a string
     */
    private static String titleProperty(JsonNode property) {
        return property.get("title").get(0).get("plain_text").asText();
    }

    /**
     * Extracts the selected value from a Notion select property.
     *
     * @param property the {@link JsonNode} containing a Notion select property
     * @return the name of the selected option as a string
     */
    private static String selectProperty(JsonNode property) {
        return property.get("select").get("name").asText();
    }

    /**
     * Extracts all selected values from a Notion multi-select property.
     *
     * @param property the {@link JsonNode} containing a Notion multi_select property
     * @return an array of strings representing all selected options
     */
    private static String[] multiSelectProperty(JsonNode property) {
        ArrayList<String> options = new ArrayList<>();
        var multiSelect = property.get("multi_select");
        for (JsonNode option : property.get("multi_select")) {
            options.addLast(option.get("name").asText());
        }
        return options.toArray(new String[0]);
    }
}
