package vocabase.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import org.springframework.stereotype.Service;
import vocabase.model.*;
import vocabase.utils.NotionDeserializer;
import vocabase.utils.NotionSerializer;

import java.util.*;


/**
 * Service class responsible for handling all interactions with the Notion API for CRUD operations
 * on vocabulary databases stored in a Notion workspace.
 *
 * <p>The service uses Spring's dependency injection to obtain configuration values and manages
 * REST communication with Notion using the {@link RestClient} API.
 *
 * <p>Two external properties must be defined in {@code application.properties}:
 * <ul>
 *   <li><b>notion.secret.key</b>: the secret key for a Notion API integration. To obtain this,
 *   create an internal integration for your Notion workspace. See:
 *   <a href="https://developers.notion.com/docs/create-a-notion-integration#create-your-integration-in-notion">Notion Integration Guide</a>.</li>
 *   <li><b>vocabase.root.id</b>: the Notion page ID that acts as the root container for all
 * Vocabase collections and groups. Your Notion integration must be authorized to at least "read", "update", and "insert" content from and to this page. For a guide on how to give your integration page permissions, please refer to this guide:
 * <a href="https://developers.notion.com/docs/create-a-notion-integration#give-your-integration-page-permissions">Notion Integration Page Permissions</a>.</li>
 * </ul>
 */
@Service
public class NotionService {
    private final RestClient client;
    private final ObjectMapper objectMapper;
    private final String vocabaseRootId;
    private HashMap<WordType, String> db;

    /**
     * Constructs a {@code NotionService} instance that handles all communication with the Notion API.
     * <p>
     * This constructor is automatically called by Spring via dependency injection. It initializes
     * the internal {@link RestClient} and {@link ObjectMapper}, and sets up required authorization headers
     * for interacting with Notion's REST API.
     *
     * @param secret          the Notion integration secret key, injected from the {@code application.properties} file.
     *                        This key is obtained by creating an internal integration in your Notion workspace.
     *                        See: <a href="https://developers.notion.com/docs/create-a-notion-integration">Notion Integration Guide</a>
     * @param vocabaseRootId  the ID of the Notion page that serves as the root container for all Vocabase
     *                        collections and groups. Your Notion integration must be authorized to at least
     *                        "read", "update", and "insert" content from and to this page.
     *                        For a guide on how to grant these permissions, see:
     *                        <a href="https://developers.notion.com/docs/create-a-notion-integration#give-your-integration-page-permissions">
     *                        Notion Integration Page Permissions</a>.
     */
    @Autowired
    public NotionService(@Value("${notion.secret.key}") String secret, @Value("${vocabase.root.id}") String vocabaseRootId) {
        this.objectMapper = new ObjectMapper();
        this.vocabaseRootId = vocabaseRootId;
        this.client = RestClient.builder().baseUrl("https://api.notion.com")
                .defaultHeader("Authorization", secret)
                .defaultHeader("Notion-Version", "2022-06-28")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    }

    /**
     * Fetches the immediate child blocks (pages or databases) of a specified Notion page.
     * <p>
     * This method sends a GET request to the Notion API to retrieve up to 100 child blocks (maximum number per request) of the given page.
     * It filters for blocks of type {@code child_page} or {@code child_database}, extracts their titles and IDs,
     * and returns them as an unmodifiable {@link Map} where the key is the block title and the value is the block ID.
     *
     * @param pageId the ID of the Notion page whose child blocks should be retrieved.
     * @return an unmodifiable map containing the titles and corresponding IDs of all child pages/databases.
     * @throws RuntimeException if there is an error parsing the JSON response from the Notion API.
     */
    private Map<String, String> fetchBlockChildren(String pageId) {
        try {
            var response = this.client.get().uri("/v1/blocks/" + pageId + "/children?page_size=100").retrieve();
            var json = this.objectMapper.readTree(response.body(String.class));
            var children = new TreeMap<String, String>();
            for (var result : json.get("results")) {
                var childType = result.get("type").asText();
                if (childType.equals("child_page") || childType.equals("child_database")) {
                    var childTitle = result.get(childType).get("title").asText();
                    var childId = result.get("id").asText();
                    children.put(childTitle, childId);
                }
            }
            return Collections.unmodifiableMap(children);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing notion api json response", e);
        }
    }

    /**
     * Fetches all collections from the root Vocabase page.
     * <p>
     * A collection is defined as a direct child page of the root Notion page identified by {@code vocabase.root.id}.
     * Each collection may contain one or more groups, which in turn contain language databases (e.g., Verbs, Nouns).
     *
     * @return a map where each key is the title of a collection and the value is its corresponding Notion page ID.
     * @throws RuntimeException if the request to the Notion API fails or the response is malformed.
     */
    public Map<String, String> fetchCollections() {
        return fetchBlockChildren(this.vocabaseRootId);
    }

    /**
     * Fetches all groups within a specified collection.
     * <p>
     * A group is defined as a child page of a collection page. This method first looks up the given collection
     * within the root Vocabase page ({@code vocabase.root.id}), and then retrieves its direct children.
     * Each group must contain exactly four Notion database pages named {@code Verbs}, {@code Nouns},
     * {@code Adjectives}, and {@code Adverbs}.
     *
     * @param collection the name of the collection to fetch groups from.
     * @return a map where each key is the title of a group and the value is its corresponding Notion page ID.
     * @throws RuntimeException if the specified collection does not exist or if the request to the Notion API fails.
     */
    public Map<String, String> fetchGroups(String collection) {
        var collections = fetchBlockChildren(this.vocabaseRootId);
        if (!collections.containsKey(collection)) throw new RuntimeException("Collection doesn't exist!");
        return fetchBlockChildren(collections.get(collection));
    }

    /**
     * Selects the desired group from a given collection to populate with terms.
     * <p>
     * This method retrieves the child pages (databases) of the specified group and extracts
     * the IDs of the four required databases: "Verbs", "Nouns", "Adjectives", and "Adverbs".
     * These database IDs are stored in an internal {@link HashMap}, with each {@link WordType}
     * acting as the key and its corresponding Notion database ID as the value.
     * <p>
     * All four databases must be present in the group; otherwise, a {@link RuntimeException}
     * is thrown.
     *
     * @param collection the name of the collection containing the group
     * @param group      the name of the group to select
     * @throws RuntimeException if any of the required databases are missing or if an error occurs during selection
     */
    public void selectDB(String collection, String group) {
        try {
            var groups = fetchGroups(collection);
            var children = fetchBlockChildren(groups.get(group));
            this.db = new HashMap<>();
            children.forEach((child, id) -> {
                if (Objects.equals(child, "Verbs"))
                    this.db.put(WordType.VERB, id);
                else if (Objects.equals(child, "Nouns"))
                    this.db.put(WordType.NOUN, id);
                else if (Objects.equals(child, "Adjectives"))
                    this.db.put(WordType.ADJECTIVE, id);
                else if (Objects.equals(child, "Adverbs"))
                    this.db.put(WordType.ADVERB, id);
            });
            if ((!this.db.containsKey(WordType.VERB)) || (!this.db.containsKey(WordType.NOUN)) || (!this.db.containsKey(WordType.ADJECTIVE)) || (!this.db.containsKey(WordType.ADVERB)))
                throw new RuntimeException("A Database is missing from the group");
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while selecting DB group!");
        }
    }

    /**
     * Retrieves all vocabulary records from the selected Notion databases for each {@link WordType}.
     * <p>
     * This method calls {@link #fetchDbRecords(WordType)} for each of the four word types:
     * VERB, NOUN, ADJECTIVE, and ADVERB, and returns the results as a map where each
     * {@link WordType} is associated with an array of its corresponding {@link Term} objects.
     * <p>
     * Before calling this method, {@link #selectDB(String, String)} must be called to initialize
     * the database IDs for the current working group.
     *
     * @return a map associating each {@link WordType} with an array of its fetched {@link Term} records
     * @throws RuntimeException if any database is not properly selected or if fetching records fails
     */
    public Map<WordType, Term[]> fetchAllRecords() {
        return Map.of(WordType.VERB, this.fetchDbRecords(WordType.VERB),
                WordType.NOUN, this.fetchDbRecords(WordType.NOUN),
                WordType.ADJECTIVE, this.fetchDbRecords(WordType.ADJECTIVE),
                WordType.ADVERB, this.fetchDbRecords(WordType.ADVERB));
    }

    /**
     * Fetches all records of a specific {@link WordType} from the corresponding Notion database
     * selected via {@link #selectDB(String, String)}. A record is defined as an entry in a Notion Database page.
     * <p>
     * This method sends paginated POST requests to the Notion API's database query endpoint,
     * retrieving up to 100 records per page. Each record in the response is deserialized into
     * a {@link Term} object using {@link NotionDeserializer}.
     * <p>
     * If there are additional pages of results (indicated by the {@code has_more} flag),
     * the request continues using the provided {@code next_cursor} until all records are retrieved.
     *
     * @param wordType the type of word whose records should be fetched (e.g., VERB, NOUN)
     * @return an array of {@link Term} objects representing all records in the corresponding database
     * @throws RuntimeException if an unexpected response is received from the Notion API
     */
    private Term[] fetchDbRecords(WordType wordType) {
        ArrayList<Term> terms = new ArrayList<>();
        var requestBody = this.objectMapper.createObjectNode().put("page_size", 100);
        boolean hasMore = true;
        do {
            var response = client.post().uri("/v1/databases/" + this.db.get(wordType) + "/query").body(requestBody).retrieve().toEntity(ObjectNode.class).getBody();
            assert response != null;
            for (var result : response.get("results")) {
                terms.addLast(NotionDeserializer.deserialize(result.get("properties"), wordType));
            }
            hasMore = response.get("has_more").asBoolean();
            if (hasMore) {
                requestBody = this.objectMapper.createObjectNode().put("start_cursor", response.get("next_cursor").asText());
            }
        } while (hasMore);
        return terms.toArray(new Term[0]);
    }

    /**
     * Inserts a new {@link Term} record into the appropriate Notion database based on its {@link WordType}.
     * <p>
     * This method serializes the given {@link Term} instance into a JSON payload using the {@link NotionSerializer}
     * and posts it to the corresponding Notion database page determined by the currently selected group.
     * The correct database ID is resolved from the internal {@code db} map populated via {@link #selectDB(String, String)}.
     * <p>
     * Supported term types include:
     * <ul>
     *   <li>{@link Verb}</li>
     *   <li>{@link Noun}</li>
     *   <li>{@link Adjective}</li>
     *   <li>{@link Adverb}</li>
     * </ul>
     *
     * @param term the vocabulary term to insert into Notion
     * @throws RuntimeException if the insertion fails or the Notion API returns a non-success response
     */
    public void insert(Term term) {
        var payload = switch (term.getType()) {
            case WordType.VERB -> NotionSerializer.serialize((Verb) term, this.db.get(WordType.VERB));
            case WordType.NOUN -> NotionSerializer.serialize((Noun) term, this.db.get(WordType.NOUN));
            case WordType.ADJECTIVE -> NotionSerializer.serialize((Adjective) term, this.db.get(WordType.ADJECTIVE));
            case WordType.ADVERB -> NotionSerializer.serialize((Adverb) term, this.db.get(WordType.ADVERB));
        };
        var response = this.client.post().uri("/v1/pages").body(payload.toString()).retrieve().toEntity(String.class);
        if (!response.getStatusCode().is2xxSuccessful())
            throw new RuntimeException("unable to insert row into notion database!");
    }
}
