package vocabase.scraper;

import org.jsoup.nodes.Element;
import vocabase.model.Tags;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class responsible for extracting verb-specific grammatical and lexical features
 * from a lemma HTML element scraped from {@code www.verben.de}.
 *
 * <p>This class is only used internally by the {@link vocabase.scraper.TermScraper} when scraping terms
 * identified as verbs, and provides extraction of the following properties:
 * <ul>
 *     <li>Preterite form</li>
 *     <li>Past participle (Partizip II) with auxiliary verb</li>
 *     <li>Prepositional bindings</li>
 *     <li>Additional grammatical tags (e.g. separability, case requirements)</li>
 * </ul>
 */
class VerbScraper {

    /**
     * Extracts the Präteritum (simple past tense) form of the verb from the lemma.
     *
     * @param lemma the HTML element representing the term's lemma container
     * @return the preterite form as a string
     * @throws RuntimeException if the preterite form cannot be reliably extracted
     */
    String scrapePreterite(Element lemma) {
        String preterite = Objects.requireNonNull(lemma.selectFirst("div#wStckInf>div#wStckKrz>div+p")).wholeText().split("·")[1].replaceAll("(^[^a-zA-ZäöüÄÖÜß]+)|([^a-zA-ZäöüÄÖÜß]+$)", "");
        Matcher preteriteMatcher = Pattern.compile("^[a-zA-ZäöüÄÖÜßé-]+ *[a-zA-ZäöüÄÖÜßé-]+").matcher(preterite);
        if (preteriteMatcher.find()) {
            return preteriteMatcher.group();
        } else {
            throw new RuntimeException("unable to extract preterite");
        }
    }

    /**
     * Extracts the Partizip II (past participle) form along with its appropriate auxiliary verb
     * ("hat", "ist", or "hat/ist") depending on the metadata in the lemma.
     *
     * @param lemma the HTML element representing the term's lemma container
     * @return the full PPII form with auxiliary verb, e.g., {@code "hat gemacht"}
     * @throws RuntimeException if the auxiliary verb or verb stem cannot be determined
     */
    String scrapePPII(Element lemma) {
        String metaText = Objects.requireNonNull(lemma.selectFirst("span.rInf")).wholeText();
        boolean hasHaben = Pattern.compile("\\Whaben\\W").matcher(metaText).find();
        boolean hasSein = Pattern.compile("\\Wsein\\W").matcher(metaText).find();
        String hilfsverb;
        if (hasHaben && hasSein)
            hilfsverb = "hat/ist ";
        else if (hasHaben)
            hilfsverb = "hat ";
        else if (hasSein)
            hilfsverb = "ist ";
        else
            throw new RuntimeException("error while scraping PPII: could not determine hilfsverb");
        String ppii = Objects.requireNonNull(lemma.selectFirst("div#wStckInf>div#wStckKrz>div+p")).wholeText().split("·")[2].replaceAll("(^[^a-zA-ZäöüÄÖÜß]+)|([^a-zA-ZäöüÄÖÜß]+$)", "");
        Matcher stemMatcher = Pattern.compile("([a-zA-ZäöüÄÖÜßé-]+)([^a-zA-ZäöüÄÖÜßé-]*)$").matcher(ppii);
        if (stemMatcher.find()) {
            return hilfsverb + stemMatcher.group(1);
        } else throw  new RuntimeException("error while scraping PPII: could not scrape verb stem");
    }

    /**
     * Extracts a list of prepositions that commonly co-occur with the verb.
     * These are found as HTML tags with title attributes representing prepositional collocations.
     *
     * @param lemma the HTML element representing the term's lemma container
     * @return an array of uppercase prepositions, or an empty array if none found
     */
    String[] scrapePrepositions(Element lemma) {
        try {
            var tagsRaw = Objects.requireNonNull(lemma.getElementById("wStckInf")).getElementsByAttributeValueMatching("title", "^\\'[a-zA-ZäöüÄÖÜß]+\\'.*$");
            LinkedHashSet<String> prepositions = new LinkedHashSet<>();
            for (var tag : tagsRaw) {
                prepositions.addLast(tag.wholeText().replaceAll("(^[^a-zA-ZäöüÄÖÜß]+)|([^a-zA-ZäöüÄÖÜß]+$)", "").toUpperCase());
            }
            return prepositions.toArray(new String[0]);
        } catch (RuntimeException e) {
            return new String[]{};
        }
    }

    /**
     * Extracts additional grammatical metadata (tags) associated with the verb,
     * such as whether the verb is regular, separable, reflexive, or which case objects it takes.
     *
     * <p>Also identifies whether the past participle (PPII) is used with "sein".
     *
     * @param lemma the HTML element representing the term's lemma container
     * @return a {@link Tags} instance populated with the extracted grammatical flags
     */
    Tags scrapeTags(Element lemma) {
        boolean accusative = false;
        boolean genitive = false;
        boolean dative = false;
        boolean regular = false;
        boolean separable = false;
        boolean reflexive = false;
        boolean ppiiWithSein = false;
        if (!Objects.isNull(lemma.selectFirst("span[title=\"mit Akkusativobjekt\"]")))
            accusative = true;
        if (!Objects.isNull(lemma.selectFirst("span[title=\"mit Genitivobjekt\"]")))
            genitive = true;
        if (!Objects.isNull(lemma.selectFirst("span[title=\"mit Dativobjekt\"]")))
            dative = true;
        regular = Objects.isNull(lemma.selectFirst("span[title=\"unregelmäßiges Verb\"]"));
        separable = !Objects.isNull(lemma.selectFirst("span[title=\"trennbares Verb\"]"));
        if (!Objects.isNull(lemma.selectFirst("span[title=\"mit Reflexivpronomen (sich)\"]")))
            reflexive = true;
        if (!Objects.isNull(lemma.selectFirst("span[title=\"Hilfsverb 'sein'\"]")))
            ppiiWithSein = true;
        return new Tags(regular, separable, accusative, dative, genitive, reflexive, ppiiWithSein);
    }
}
