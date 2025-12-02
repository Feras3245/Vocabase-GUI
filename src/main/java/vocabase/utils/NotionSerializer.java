package vocabase.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import vocabase.model.*;
import java.util.ArrayList;

/**
 * Utility class responsible for serializing {@link Term} objects (including their subclasses:
 * {@link Noun}, {@link Verb}, {@link Adjective}, and {@link Adverb}) into the JSON format expected
 * by the Notion API for creating new records in Notion databases.
 * <p>
 * Each public {@code serialize} method accepts a {@code Term} object and the corresponding Notion
 * database ID, and produces a structured {@link ObjectNode} payload representing the properties of
 * the term in a format compliant with the Notion API.
 * <p>
 * This class is leveraged by {@link vocabase.service.NotionService} when inserting vocabulary data
 * into Notion. It uses Jackson's {@link ObjectMapper} to construct the JSON tree programmatically,
 * ensuring that fields like title, select, multi-select, and rich text are formatted precisely to
 * Notion's schema.
 *
 *   <p>Each term is wrapped in a parent node indicating the destination Notion database.
 *   Term fields are serialized using helper methods to handle different Notion property types:</p>
 *   <ul>
 *     <li>{@code textProperty} – for "title" and "rich_text" types</li>
 *     <li>{@code selectProperty} – for single selection values (e.g., grammatical article or level)</li>
 *     <li>{@code multiSelectProperty} – for lists of tags or prepositions</li>
 *   </ul>
 *
 * <h2>Supported Term Types</h2>
 * <ul>
 *   <li>{@link Noun} – includes article, plural, definitions, synonyms, antonyms, and examples</li>
 *   <li>{@link Verb} – includes conjugations, tags, definitions, prepositions, synonyms, antonyms, examples</li>
 *   <li>{@link Adjective} – includes comparative, superlative, definitions, synonyms, antonyms, examples</li>
 *   <li>{@link Adverb} – includes definitions, synonyms, antonyms, examples</li>
 * </ul>
 *
 * @see vocabase.model.Term
 * @see vocabase.service.NotionService
 * @see <a href="https://developers.notion.com/reference/property-object">Notion Property Object Reference</a>
 */

public class NotionSerializer {


    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    /**
     * Serializes a {@link Noun} object into a JSON payload compatible with the Notion API for creating
     * a new record in a Notion database.
     *
     * <p>This method constructs a structured JSON object containing the properties of the provided
     * {@code Noun}, formatted according to Notion's API schema. The resulting payload includes the
     * parent database ID and a set of properties such as the noun's term, level, article, plural form,
     * definitions, synonyms, antonyms, and examples. Each property is formatted using helper methods
     * to ensure compliance with Notion's property types (e.g., "title", "select", "rich_text").</p>
     *
     * <p>Example output structure:
     * <pre>
     * {
     *   "parent": { "database_id": "<databaseId>" },
     *   "properties": {
     *     "Noun": { "title": [{ "text": { "content": "<term>" } }] },
     *     "Niveau": { "select": { "name": "<niveau>" } },
     *     "Article": { "select": { "name": "<article>" } },
     *     "Plural": { "rich_text": [{ "text": { "content": "<plural>" } }] },
     *     "Definitions": { "rich_text": [{ "text": { "content": "<definitions>" } }] },
     *     "Synonyms": { "rich_text": [{ "text": { "content": "<synonyms>" } }] },
     *     "Antonyms": { "rich_text": [{ "text": { "content": "<antonyms>" } }] },
     *     "Examples": { "rich_text": [{ "text": { "content": "<examples>" } }] }
     *   }
     * }
     * </pre>
     * </p>
     *
     * @param noun       the {@link Noun} object to serialize
     * @param databaseId the ID of the Notion database where the record will be created
     * @return an {@link ObjectNode} representing the formatted JSON payload for the Notion API
     * @see vocabase.model.Noun
     * @see <a href="https://developers.notion.com/reference/property-object">Notion Property Object Reference</a>
     */
    public static ObjectNode serialize(Noun noun, String databaseId) {
        var payload = objectMapper.createObjectNode();
        payload.set("parent", objectMapper.createObjectNode().put("database_id", databaseId));
        var properties = objectMapper.createObjectNode();;
        properties.set("Noun", textProperty("title", noun.getTerm()));
        properties.set("Niveau", selectProperty((noun.getNiveau() == null) ? "N/A" : noun.getNiveau().toString()));
        properties.set("Article", selectProperty(formatArticle(noun.getArticle())));
        properties.set("Plural", textProperty("rich_text", noun.getPlural()));
        properties.set("Definitions", textProperty("rich_text", formatList(noun.getDefinitions())));
        properties.set("Synonyms", textProperty("rich_text", formatList(noun.getSynonyms())));
        properties.set("Antonyms", textProperty("rich_text", formatList(noun.getAntonyms())));
        properties.set("Examples", textProperty("rich_text", formatList(noun.getExamples())));
        payload.set("properties", properties);
        return payload;
    }

    /**
     * Serializes a {@link Verb} object into a JSON payload compatible with the Notion API for creating
     * a new record in a Notion database.
     *
     * <p>This method constructs a structured JSON object containing the properties of the provided
     * {@code Verb}, formatted according to Notion's API schema. The resulting payload includes the
     * parent database ID and a set of properties such as the verb's term, level, definitions, past
     * participle (PPII), preterite, tags, examples, synonyms, antonyms, and prepositions. Each property
     * is formatted using helper methods to ensure compliance with Notion's property types (e.g., "title",
     * "select", "rich_text", "multi_select").</p>
     *
     * <p>Example output structure:
     * <pre>
     * {
     *   "parent": { "database_id": "<databaseId>" },
     *   "properties": {
     *     "Verb": { "title": [{ "text": { "content": "<term>" } }] },
     *     "Niveau": { "select": { "name": "<niveau>" } },
     *     "Definitions": { "rich_text": [{ "text": { "content": "<definitions>" } }] },
     *     "PPII": { "rich_text": [{ "text": { "content": "<ppii>" } }] },
     *     "Preterite": { "rich_text": [{ "text": { "content": "<preterite>" } }] },
     *     "Tags": { "multi_select": [{ "name": "<tag1>" }, { "name": "<tag2>" }, ...] },
     *     "Examples": { "rich_text": [{ "text": { "content": "<examples>" } }] },
     *     "Synonyms": { "rich_text": [{ "text": { "content": "<synonyms>" } }] },
     *     "Antonyms": { "rich_text": [{ "text": { "content": "<antonyms>" } }] },
     *     "Prepositions": { "multi_select": [{ "name": "<prep1>" }, { "name": "<prep2>" }, ...] }
     *   }
     * }
     * </pre>
     * </p>
     *
     * @param verb       the {@link Verb} object to serialize
     * @param databaseId the ID of the Notion database where the record will be created
     * @return an {@link ObjectNode} representing the formatted JSON payload for the Notion API
     * @see vocabase.model.Verb
     * @see <a href="https://developers.notion.com/reference/property-object">Notion Property Object Reference</a>
     */
    public static ObjectNode serialize(Verb verb, String databaseId) {
        var payload = objectMapper.createObjectNode();
        payload.set("parent", objectMapper.createObjectNode().put("database_id", databaseId));
        var properties = objectMapper.createObjectNode();
        properties.set("Verb", textProperty("title", verb.getTerm()));
        properties.set("Niveau", selectProperty((verb.getNiveau() == null) ? "N/A" : verb.getNiveau().toString()));
        properties.set("Definitions", textProperty("rich_text", formatList( verb.getDefinitions())));
        properties.set("PPII", textProperty("rich_text", verb.getPpii()));
        properties.set("Preterite", textProperty("rich_text", verb.getPreterite()));
        properties.set("Tags", multiSelectProperty(formatVerbTags(verb.getTags())));
        properties.set("Examples", textProperty("rich_text", formatList(verb.getExamples())));
        properties.set("Synonyms", textProperty("rich_text", formatList(verb.getSynonyms())));
        properties.set("Antonyms", textProperty("rich_text", formatList(verb.getAntonyms())));
        properties.set("Prepositions", multiSelectProperty(verb.getPrepositions()));
        payload.set("properties", properties);
        return payload;
    }

    /**
     * Serializes an {@link Adjective} object into a JSON payload compatible with the Notion API for creating
     * a new record in a Notion database.
     *
     * <p>This method constructs a structured JSON object containing the properties of the provided
     * {@code Adjective}, formatted according to Notion's API schema. The resulting payload includes the
     * parent database ID and a set of properties such as the adjective's term, level, definitions,
     * comparative form, superlative form, examples, synonyms, and antonyms. Each property is formatted
     * using helper methods to ensure compliance with Notion's property types (e.g., "title", "select",
     * "rich_text").</p>
     *
     * <p>Example output structure:
     * <pre>
     * {
     *   "parent": { "database_id": "<databaseId>" },
     *   "properties": {
     *     "Adjective": { "title": [{ "text": { "content": "<term>" } }] },
     *     "Niveau": { "select": { "name": "<niveau>" } },
     *     "Definitions": { "rich_text": [{ "text": { "content": "<definitions>" } }] },
     *     "Comparative": { "rich_text": [{ "text": { "content": "<comparative>" } }] },
     *     "Superlative": { "rich_text": [{ "text": { "content": "<superlative>" } }] },
     *     "Examples": { "rich_text": [{ "text": { "content": "<examples>" } }] },
     *     "Synonyms": { "rich_text": [{ "text": { "content": "<synonyms>" } }] },
     *     "Antonyms": { "rich_text": [{ "text": { "content": "<antonyms>" } }] }
     *   }
     * }
     * </pre>
     * </p>
     *
     * @param adjective  the {@link Adjective} object to serialize
     * @param databaseId the ID of the Notion database where the record will be created
     * @return an {@link ObjectNode} representing the formatted JSON payload for the Notion API
     * @see vocabase.model.Adjective
     * @see <a href="https://developers.notion.com/reference/property-object">Notion Property Object Reference</a>
     */
    public static ObjectNode serialize(Adjective adjective, String databaseId){
        var payload = objectMapper.createObjectNode();
        payload.set("parent", objectMapper.createObjectNode().put("database_id", databaseId));
        var properties = objectMapper.createObjectNode();
        properties.set("Adjective", textProperty("title", adjective.getTerm()));
        properties.set("Niveau", selectProperty((adjective.getNiveau() == null) ? "N/A" : adjective.getNiveau().toString()));
        properties.set("Definitions", textProperty("rich_text", formatList(adjective.getDefinitions())));
        properties.set("Comparative", textProperty("rich_text", adjective.getComparative()));
        properties.set("Superlative", textProperty("rich_text", adjective.getSuperlative()));
        properties.set("Examples", textProperty("rich_text", formatList(adjective.getExamples())));
        properties.set("Synonyms", textProperty("rich_text", formatList(adjective.getSynonyms())));
        properties.set("Antonyms", textProperty("rich_text", formatList(adjective.getAntonyms())));
        payload.set("properties", properties);
        return payload;
    }

    /**
     * Serializes an {@link Adverb} object into a JSON payload compatible with the Notion API for creating
     * a new record in a Notion database.
     *
     * <p>This method constructs a structured JSON object containing the properties of the provided
     * {@code Adverb}, formatted according to Notion's API schema. The resulting payload includes the
     * parent database ID and a set of properties such as the adverb's term, level, definitions,
     * examples, synonyms, and antonyms. Each property is formatted using helper methods to ensure
     * compliance with Notion's property types (e.g., "title", "select", "rich_text").</p>
     *
     * <p>Example output structure:
     * <pre>
     * {
     *   "parent": { "database_id": "<databaseId>" },
     *   "properties": {
     *     "Adverb": { "title": [{ "text": { "content": "<term>" } }] },
     *     "Niveau": { "select": { "name": "<niveau>" } },
     *     "Definitions": { "rich_text": [{ "text": { "content": "<definitions>" } }] },
     *     "Examples": { "rich_text": [{ "text": { "content": "<examples>" } }] },
     *     "Synonyms": { "rich_text": [{ "text": { "content": "<synonyms>" } }] },
     *     "Antonyms": { "rich_text": [{ "text": { "content": "<antonyms>" } }] }
     *   }
     * }
     * </pre>
     * </p>
     *
     * @param adverb     the {@link Adverb} object to serialize
     * @param databaseId the ID of the Notion database where the record will be created
     * @return an {@link ObjectNode} representing the formatted JSON payload for the Notion API
     * @see vocabase.model.Adverb
     * @see <a href="https://developers.notion.com/reference/property-object">Notion Property Object Reference</a>
     */
    public static ObjectNode serialize(Adverb adverb, String databaseId){
        var payload = objectMapper.createObjectNode();
        payload.set("parent", objectMapper.createObjectNode().put("database_id", databaseId));
        var properties = objectMapper.createObjectNode();
        properties.set("Adverb", textProperty("title", adverb.getTerm()));
        properties.set("Niveau", selectProperty((adverb.getNiveau() == null) ? "N/A" : adverb.getNiveau().toString()));
        properties.set("Definitions", textProperty("rich_text", formatList(adverb.getDefinitions())));
        properties.set("Examples", textProperty("rich_text", formatList(adverb.getExamples())));
        properties.set("Synonyms", textProperty("rich_text", formatList(adverb.getSynonyms())));
        properties.set("Antonyms", textProperty("rich_text", formatList(adverb.getAntonyms())));
        payload.set("properties", properties);
        return payload;
    }

    /**
     * Formats an {@link Article} enum value into its corresponding string representation
     * as expected by the Notion database's "select" property.
     *
     * <p>This method ensures that compound articles like {@code DER_DIE} or {@code DAS_DIE}
     * are correctly converted to a readable format ("Der/Die", "Das/Die", etc.), which
     * aligns with how these values are stored and displayed in Notion's UI.</p>
     *
     * @param article the {@link Article} enum to format
     * @return the human-readable string representation of the article
     */
    private static String formatArticle(Article article) {
        return switch (article) {
            case Article.DER -> "Der";
            case Article.DIE -> "Die";
            case Article.DAS -> "Das";
            case Article.DER_DIE -> "Der/Die";
            case Article.DER_DAS -> "Der/Das";
            case Article.DAS_DIE -> "Das/Die";
        };
    }

    /**
     * Converts a {@link Tags} object into a list of string labels used as multi-select
     * values in the Notion database for verbs.
     *
     * <p>This method encodes various grammatical and semantic attributes of a verb—such as
     * whether it is regular, reflexive, accusative, etc.—into a format suitable for the
     * Notion API's <code>multi_select</code> property.</p>
     *
     * <ul>
     *   <li>Adds "Regular" or "Irregular" based on {@code tags.regular()}</li>
     *   <li>Conditionally adds grammatical roles: "Reflexive", "Accusative", "Genitive", "Dative"</li>
     *   <li>Adds "Separable" or "Inseparable" based on {@code tags.separable()}</li>
     *   <li>Adds "PPII Mit Sein" if the past participle is used with "sein"</li>
     * </ul>
     *
     * @param tags the {@link Tags} object representing verb characteristics
     * @return a string array of labels to be used in a Notion multi-select field
     */
    private static String[] formatVerbTags(Tags tags) {
        ArrayList<String> output = new ArrayList<>();
        output.addLast((tags.regular()) ? "Regular" : "Irregular");
        if (tags.reflexive()) output.addLast("Reflexive");
        if (tags.accusative()) output.addLast("Accusative");
        if (tags.genitive()) output.addLast("Genitive");
        if (tags.dative()) output.addLast("Dative");
        output.addLast((tags.separable()) ? "Separable" : "Inseparable");
        if (tags.ppiiWithSein()) output.addLast("PPII Mit Sein");
        return output.toArray(new String[0]);
    }

    /**
     * Joins the elements of a string array into a single string, separating each element
     * with " / ", except for the last element which is appended without a trailing separator.
     *
     * <p>This is primarily used to format lists such as definitions, examples, synonyms,
     * or antonyms into a single Notion-compatible rich text field.</p>
     *
     * <p>For example, the array {@code {"run", "jog", "sprint"}} would be formatted as
     * {@code "run / jog / sprint"}.</p>
     *
     * @param list an array of strings to format
     * @return a single string with elements joined by " / "
     */
    private static String formatList(String[] list){
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < list.length; i++)
            if (i == (list.length-1))
                output.append(list[i]);
            else
                output.append(list[i]).append(" / ");
        return output.toString();
    }

    /**
     * Constructs a Notion-compatible JSON property of a specified text type
     * (e.g., {@code "title"} or {@code "rich_text"}) with the provided content.
     *
     * <p>This method is used to populate string fields in a Notion database entry,
     * such as term names, example usages, or definitions.</p>
     *
     * <p>The resulting structure matches Notion's API expectations for text-based
     * fields, wrapping the content inside a typed JSON array.</p>
     *
     * <p>Example output format for type {@code "title"} and content {@code "Example"}:
     * <pre>
     * {
     *   "type": "title",
     *   "title": [
     *     {
     *       "type": "text",
     *       "text": { "content": "Example" }
     *     }
     *   ]
     * }
     * </pre>
     * </p>
     *
     * @param type    the Notion property type, such as {@code "title"} or {@code "rich_text"}
     * @param content the string content to embed
     * @return an {@link ObjectNode} representing the formatted property
     */
    private static ObjectNode textProperty(String type, String content) {
        var root = objectMapper.createObjectNode();
        root.put("type", type);
        var array = objectMapper.createArrayNode();
        var object = objectMapper.createObjectNode();
        object.put("type", "text");
        object.set("text", objectMapper.createObjectNode().put("content", content));
        array.add(object);
        root.set(type, array);
        return root;
    }

    /**
     * Constructs a Notion-compatible "select" property with the given option name.
     *
     * <p>This method is used for single-select fields in Notion databases, such as
     * grammatical attributes like "Article" or "Niveau". It wraps the provided content
     * in the JSON structure required by Notion's API for select-type properties.</p>
     *
     * <p>Example output for content {@code "Der"}:
     * <pre>
     * {
     *   "type": "select",
     *   "select": {
     *     "name": "Der"
     *   }
     * }
     * </pre>
     * </p>
     *
     * @param content the name of the option to be selected
     * @return an {@link ObjectNode} representing the formatted "select" property
     */
    private static ObjectNode selectProperty(String content) {
        var root = objectMapper.createObjectNode();
        root.put("type", "select");
        root.set("select", objectMapper.createObjectNode().put("name", content));
        return root;
    }

    /**
     * Constructs a Notion-compatible "multi_select" property with the given array of option names.
     *
     * <p>This method is used for multi-select fields in Notion databases, such as verb tags or prepositions.
     * It wraps the provided array of strings in the JSON structure required by Notion's API for multi-select
     * properties, creating an array of select options where each option is represented by a name.</p>
     *
     * <p>Example output for input {@code ["Regular", "Reflexive"]}:
     * <pre>
     * {
     *   "type": "multi_select",
     *   "multi_select": [
     *     { "name": "Regular" },
     *     { "name": "Reflexive" }
     *   ]
     * }
     * </pre>
     * </p>
     *
     * @param selects an array of strings representing the names of the options to be included
     * @return an {@link ObjectNode} representing the formatted "multi_select" property
     */
    private static ObjectNode multiSelectProperty(String[] selects) {
        var root = objectMapper.createObjectNode();
        root = root.put("type", "multi_select");
        var array = objectMapper.createArrayNode();
        for(String select : selects) {
            array.add(objectMapper.createObjectNode().put("name", select));
        }
        root.set("multi_select", array);
        return root;
    }

}
