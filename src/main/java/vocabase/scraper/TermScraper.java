package vocabase.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.CombiningEvaluator;
import org.jsoup.select.Evaluator;
import org.jsoup.select.QueryParser;
import vocabase.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;


/**
 * {@code TermScraper} is responsible for scraping linguistic data from {@code www.verben.de}
 * to construct a {@link Term} object representing a German dictionary entry.
 *
 * <p>This class encapsulates the scraping logic for properties common to all terms such as:
 * <ul>
 *   <li>Term text</li>
 *   <li>Word type (e.g., verb, noun)</li>
 *   <li>Definitions (English)</li>
 *   <li>CEFR language level ({@link Niveau})</li>
 *   <li>Example usages</li>
 *   <li>Synonyms</li>
 *   <li>Antonyms</li>
 * </ul>
 *
 * <p>It also delegates scraping of word-type-specific properties to specialized scraper classes:
 * <ul>
 *   <li>{@link VerbScraper} for {@link Verb}</li>
 *   <li>{@link NounScraper} for {@link Noun}</li>
 *   <li>{@link AdjectiveScraper} for {@link Adjective}</li>
 * </ul>
 *
 * <p>The only public method, {@link #scrape(String)}, takes a search query string,
 * fetches and parses the term's details, and returns a specific subclass of {@link Term}
 * (either {@link Verb}, {@link Noun}, {@link Adjective}, or {@link Adverb}) based on the identified word type.
 *
 * <p>If the term or any critical page elements are missing, the method returns {@code null}.
 *
 * @see Term
 * @see VerbScraper
 * @see NounScraper
 * @see AdjectiveScraper
 */
public class TermScraper {

    private final VerbScraper verbScraper;
    private final NounScraper nounScraper;
    private final AdjectiveScraper adjectiveScraper;

    /**
     * Constructs a {@code TermScraper} with specialized sub-scrapers for verbs, nouns, and adjectives.
     */
    public TermScraper() {
        this.verbScraper = new VerbScraper();
        this.nounScraper = new NounScraper();
        this.adjectiveScraper = new AdjectiveScraper();
    }

    /**
     * Extracts and returns the primary term word from the given lemma element.
     *
     * <p>The {@code lemma} parameter refers to the main container {@code <div>} element on the
     * {@code www.verben.de} results page, which contains most of the term's metadata. This method
     * specifically targets the element with ID {@code "wStckKrz"}, extracts its first child node's
     * text, and performs cleaning to strip unwanted characters and trailing phrases.
     *
     * <p>Text cleaning includes:
     * <ul>
     *   <li>Removing anything after a comma (including across lines)</li>
     *   <li>Stripping non-letter characters (excluding German diacritics, spaces, and hyphens)</li>
     * </ul>
     *
     * @param lemma the root element containing the term metadata block
     * @return the cleaned term word as a {@link String}
     * @throws NullPointerException if the expected element with ID {@code "wStckKrz"} is missing
     */
    String scrapeTerm(Element lemma) {
        return Objects.requireNonNull(lemma.getElementById("wStckKrz")).child(0).wholeText().strip().replaceAll(",(.|\\n)*$", "").replaceAll("[^a-zA-ZäöüÄÖÜßé\\s\\-]", "");
    }

    /**
     * Determines and returns the {@link WordType} of the term based on metadata extracted from the provided HTML element.
     *
     * <p>The {@code meta} parameter is a {@code <span>} element within the lemma container that holds
     * various pieces of metadata about the term, including its grammatical classification (e.g., verb, noun, adjective, adverb).
     * This method inspects the presence of specific {@code <span>} children with {@code title} attributes
     * indicating the word type.
     *
     * <p>Detection is based on the following titles:
     * <ul>
     *   <li>{@code title="Verb"} → {@link WordType#VERB}</li>
     *   <li>{@code title="Substantiv"} → {@link WordType#NOUN}</li>
     *   <li>{@code title="Adjektiv"} → {@link WordType#ADJECTIVE}</li>
     *   <li>{@code title="Adverb"} → {@link WordType#ADVERB}</li>
     * </ul>
     *
     * @param meta the {@code <span>} element containing metadata about the term
     * @return the corresponding {@link WordType}, or {@code null} if no known type is detected
     */
    WordType scrapeWordType(Element meta) {
        if (!Objects.isNull(meta.selectFirst("span[title=\"Verb\"]"))) return WordType.VERB;
        if (!Objects.isNull(meta.selectFirst("span[title=\"Substantiv\"]"))) return WordType.NOUN;
        if (!Objects.isNull(meta.selectFirst("span[title=\"Adjektiv\"]"))) return WordType.ADJECTIVE;
        if (!Objects.isNull(meta.selectFirst("span[title=\"Adverb\"]"))) return WordType.ADVERB;
        else return null;
    }

    /**
     * Extracts and returns the list of English definitions for a term from the provided lemma element.
     *
     * <p>The {@code lemma} parameter is a container {@code <div>} element from the term's result page on
     * {@code www.verben.de}, which holds the core data about the term. This method specifically targets
     * a paragraph element with the classes {@code r1Zeile rU6px rO0px} containing a {@code <span lang="en">}
     * to retrieve the English definitions.
     *
     * <p>The definitions are:
     * <ul>
     *   <li>Extracted as comma-separated values from the span's text content.</li>
     *   <li>Trimmed of leading/trailing non-letter characters.</li>
     *   <li>Filtered to exclude blank or invalid entries.</li>
     *   <li>Stored uniquely in insertion order using a {@link LinkedHashSet}.</li>
     * </ul>
     *
     * @param lemma the HTML element containing the lemma's data
     * @return an array of cleaned English definitions, or an empty array if none are found
     * @throws NullPointerException if the expected definition element is missing
     */
    String[] scrapeDefinitions(Element lemma) {
        String[] definitionsRaw = Objects.requireNonNull(lemma.selectFirst("p.r1Zeile.rU6px.rO0px>span[lang=\"en\"]")).wholeOwnText().strip().split(",");
        var definitions = new LinkedHashSet<String>();
        for (String definition : definitionsRaw){
            definition = definition.replaceAll("(^[^a-zA-ZäöüÄÖÜß)(]+)|([^a-zA-ZäöüÄÖÜß)(]+$)", "");
            if (!definition.isBlank()) definitions.addLast(definition);
        }
        return definitions.toArray(new String[0]);
    }

    /**
     * Extracts the CEFR (Common European Framework of Reference for Languages) level of the term from the given metadata element.
     *
     * <p>The {@code meta} parameter is a {@code <span>} inside the lemma element on the {@code www.verben.de} results page,
     * which contains metadata about the term, such as part of speech and language level. This method looks for a nested
     * {@code <span>} with class {@code bZrt}, which holds the CEFR level as text (e.g., "A1", "B2").
     *
     * <p>The extracted text is stripped and converted to uppercase before being mapped to the corresponding {@link Niveau} enum constant.
     *
     * @param meta the HTML element containing metadata about the term
     * @return the corresponding {@link Niveau} value if found, or {@code null} if the level is missing or unrecognized
     */
    Niveau scrapeNiveau(Element meta) {
        try {
            return switch (Objects.requireNonNull(meta.selectFirst("span.bZrt")).wholeText().strip().toUpperCase()) {
                case "A1" -> Niveau.A1;
                case "A2" -> Niveau.A2;
                case "B1" -> Niveau.B1;
                case "B2" -> Niveau.B2;
                case "C1" -> Niveau.C1;
                case "C2" -> Niveau.C2;
                default -> null;
            };
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * Extracts example sentences that illustrate the usage of a term from the HTML body of the {@code www.verben.de} results page.
     *
     * <p>This method searches for a section titled {@code "Beispielsätze"} (example sentences), then locates the corresponding
     * list element containing sentence items. It cleans up the HTML structure and normalizes the text content of each example
     * by removing line breaks, non-breaking spaces, stray punctuation, and spacing inconsistencies.
     *
     * <p>Example sentences are returned in insertion order with duplicates removed.
     *
     * @param body the root HTML {@link Element} of the fetched web page
     * @return an array of cleaned example sentences, or an empty array if none are found or an error occurs
     */
    private String[] scrapeExamples(Element body) {
        try {
            ArrayList<Evaluator> evaluators = new ArrayList<>();
            evaluators.addLast(new Evaluator.Tag("h2"));
            evaluators.addLast(new Evaluator.ContainsWholeOwnText("Beispielsätze"));
            Element examplesList = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(body.selectFirst(new CombiningEvaluator.And(evaluators))).parent()).parent()).selectFirst("div.rAufZu>ul.rLst.rLstGt");
            assert examplesList != null;
            examplesList.select("li>br+span").remove();
            var examples = new LinkedHashSet<String>();
            for (var exampleItem : examplesList.select("li")) {
                String example = exampleItem.wholeText().replaceAll("\\n", " ").replaceAll("\\u00A0", " ").strip();
                String ending = "" + example.charAt(example.length() - 1);
                example = example.replaceAll(" [.?!]$", ending);
                example = example.replaceAll(" ,", ",");
                examples.addLast(example);
            }
            return examples.toArray(new String[0]);
        } catch (RuntimeException e) {
            return new String[]{};
        }
    }

    /**
     * Extracts and cleans a list of synonyms for a given term from the synonyms page HTML.
     *
     * <p>This method searches for the {@code "Synonyme"} section within the provided {@code synonymsPage} and retrieves
     * synonym entries listed in a definition list ({@code <dl>}). Each synonym is extracted from within {@code <dd>} elements,
     * cleaned of punctuation and indexing tags, and formatted with a numeric prefix (e.g., {@code "1. synonym"}).
     *
     * <p>Multiple synonyms within the same {@code <dd>} element, separated by {@code "≡ "}, are split and handled individually.
     * Any unwanted formatting characters or placeholder ellipses are removed. Blank or invalid entries are skipped.
     *
     * @param synonymsPage the HTML {@link Element} representing the body of the synonyms page
     * @return a numbered array of cleaned synonym strings, or an empty array if none are found or an error occurs
     */
    private String[] scrapeSynonyms(Element synonymsPage) {
        try {
            ArrayList<Evaluator> evaluators = new ArrayList<>();
            evaluators.addLast(QueryParser.parse("section.rBox.rBoxWht>div.rAufZu>h2"));
            evaluators.addLast(new Evaluator.ContainsWholeOwnText("Synonyme"));
            var synonymsList = Objects.requireNonNull(synonymsPage.selectFirst(new CombiningEvaluator.And(evaluators))).nextElementSibling();
            assert synonymsList != null;
            if (!synonymsList.tagName().equals("dl"))
                return new String[]{};
            LinkedHashSet<String> synonyms = new LinkedHashSet<>();
            int count = 1;
            for (var item : synonymsList.select("dd")) {
                if (!Objects.isNull(item.selectFirst("span.wIdx")))
                    Objects.requireNonNull(item.selectFirst("span.wIdx")).remove();
                String[] subItems = Objects.requireNonNull(item.selectFirst("span")).wholeText().replaceAll(", \\.\\.\\.", "").split("≡\\u00A0");
                for (var subItem : subItems) {
                    if (subItem.isBlank()) continue;
                    synonyms.add(count++ + ". " + subItem.replaceAll("(^[^a-zA-ZäöüÄÖÜß]+)|([^a-zA-ZäöüÄÖÜß]+$)", ""));
                }
            }
            if (synonyms.isEmpty()) return new String[]{};
            return synonyms.toArray(new String[0]);
        } catch (RuntimeException e) {
            return new String[]{};
        }
    }

    /**
     * Extracts and formats a list of antonyms (Gegenteile) for a given term from the synonyms page HTML.
     *
     * <p>This method searches for the {@code "Antonyme (Gegenteil)"} section within the provided {@code synonymsPage}
     * and locates the corresponding definition list ({@code <dl>}) containing antonym entries inside {@code <dd>} elements.
     * Each antonym is cleaned of indexing spans, extraneous characters, and placeholder ellipses, then formatted with a numeric prefix
     * (e.g., {@code "1. antonym"}).
     *
     * <p>Multiple antonyms within a single entry, separated by {@code "≡ "}, are treated individually. If the section is not found,
     * malformed, or an exception occurs during parsing, an empty array is returned.
     *
     * @param synonymsPage the HTML {@link Element} representing the body of the synonyms page which may contain antonyms
     * @return a numbered array of cleaned antonym strings, or an empty array if none are found or an error occurs
     */
    private String[] scrapeAntonyms(Element synonymsPage) {
        try {
            ArrayList<Evaluator> evaluators = new ArrayList<>();
            evaluators.addLast(QueryParser.parse("section.rBox.rBoxWht>div.rAufZu>h2"));
            evaluators.addLast(new Evaluator.ContainsWholeOwnText("Antonyme (Gegenteil)"));
            var antonymsList = Objects.requireNonNull(synonymsPage.selectFirst(new CombiningEvaluator.And(evaluators))).nextElementSibling();
            assert antonymsList != null;
            if (!antonymsList.tagName().equals("dl"))
                return new String[]{};
            LinkedHashSet<String> antonyms = new LinkedHashSet<>();
            int count = 1;
            for (var item : antonymsList.select("dd")) {
                if (!Objects.isNull(item.selectFirst("span.wIdx")))
                    Objects.requireNonNull(item.selectFirst("span.wIdx")).remove();
                String[] subItems = Objects.requireNonNull(item.selectFirst("span")).wholeText().replaceAll(", \\.\\.\\.", "").split("≡\\u00A0");
                for (var subItem : subItems) {
                    if (subItem.isBlank()) continue;
                    antonyms.add(count++ + ". " + subItem.replaceAll("(^[^a-zA-ZäöüÄÖÜß]+)|([^a-zA-ZäöüÄÖÜß]+$)", ""));
                }
            }
            if (antonyms.isEmpty()) return new String[]{};
            return antonyms.toArray(new String[0]);
        } catch (RuntimeException e) {
            return new String[]{};
        }
    }

    /**
     * Scrapes and constructs a {@link Term} object based on the provided German word query by extracting
     * structured linguistic information from the website {@code www.verben.de}.
     *
     * <p>This is the primary entry point for the scraping logic. It retrieves and parses multiple components from the
     * queried term’s result page, including:
     * <ul>
     *   <li>The base word (lemma)</li>
     *   <li>The word type ({@code Verb}, {@code Noun}, {@code Adjective}, or {@code Adverb})</li>
     *   <li>Language proficiency level (A1–C2)</li>
     *   <li>Definitions in English</li>
     *   <li>Example sentences</li>
     *   <li>Synonyms and antonyms from a related synonyms subpage (if available)</li>
     * </ul>
     *
     * <p>Depending on the identified word type, the method delegates to specialized scrapers for
     * additional type-specific data (e.g., prepositions and conjugations for verbs, plural form for nouns).
     * Each specialized scraper fills its respective subclass of {@link Term}:
     * {@link Verb}, {@link Noun}, {@link Adjective}, or {@link Adverb}.
     *
     * <p>If the query cannot be parsed, the page structure is unexpected, or a network failure occurs,
     * the method safely returns {@code null}.
     *
     * @param query the German term to scrape from {@code www.verben.de}
     * @return a fully populated {@link Term} instance corresponding to the query,
     *         or {@code null} if scraping fails or the structure is not recognized
     */
    public Term scrape(String query){
        try {
            Element body = Jsoup.connect("https://www.verben.de/?w=" + query).get().body();
            var lemma = Objects.requireNonNull(body.getElementById("wStckInf")).parent();
            assert lemma != null;
            var meta = lemma.selectFirst("span.rInf");
            assert meta != null;
            String term = scrapeTerm(lemma);
            WordType wordType = scrapeWordType(meta);
            Niveau niveau = scrapeNiveau(meta);
            String[] definitions = scrapeDefinitions(lemma);
            String[] examples = scrapeExamples(body);

            String[] synonyms;
            String[] antonyms;
            try {
                assert wordType != null;
                String path = switch (wordType) {
                    case WordType.VERB -> "verben";
                    case WordType.NOUN -> "substantive";
                    case WordType.ADJECTIVE -> "adjektive";
                    case WordType.ADVERB -> "adverbien";
                };
                var synonymsPage = Jsoup.connect("https://www.verben.de/"+ path +"/synonyme/" + term + ".htm").get().body();
                synonyms = scrapeSynonyms(synonymsPage);
                antonyms = scrapeAntonyms(synonymsPage);
            } catch (IOException e){
                synonyms = new String[]{};
                antonyms = new String[]{};
            }

            return switch (wordType) {
                case VERB -> {
                    String[] prepositions = this.verbScraper.scrapePrepositions(lemma);
                    String preterite = this.verbScraper.scrapePreterite(lemma);
                    String ppii = this.verbScraper.scrapePPII(lemma);
                    Tags tags = this.verbScraper.scrapeTags(lemma);
                    yield new Verb(term, niveau, definitions, examples, synonyms, antonyms, prepositions, tags, ppii, preterite);
                }
                case NOUN -> {
                    String plural = this.nounScraper.scrapePlural(lemma);
                    Article article = this.nounScraper.scrapeArticle(meta);
                    yield new Noun(term, niveau, definitions, examples, synonyms, antonyms, article, plural);
                }
                case ADJECTIVE -> {
                    String comparative = this.adjectiveScraper.scrapeComparative(lemma);
                    String superlative = this.adjectiveScraper.scrapeSuperlative(lemma);
                    yield new Adjective(term, niveau, definitions, examples, synonyms, antonyms, comparative, superlative);
                }
                case ADVERB -> new Adverb(term, niveau, definitions, examples, synonyms, antonyms);
            };

        } catch (IOException|RuntimeException e) {
            return null;
        }
    }
}
