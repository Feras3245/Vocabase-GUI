package vocabase.scraper;

import org.jsoup.nodes.Element;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for scraping grammatical properties specific to German adjectives
 * from their HTML representation on {@code www.verben.de}.
 *
 * <p>Used internally by {@link vocabase.scraper.TermScraper} when handling terms of type ADJECTIVE.
 */
class AdjectiveScraper {

    /**
     * Extracts the comparative form of the adjective from the flexion summary.
     * Typically located as the second part of a dot-separated string under the lemma.
     *
     * @param lemma the HTML element representing the term's lemma container
     * @return the comparative form as a string
     * @throws RuntimeException if no valid comparative form can be extracted
     */
    String scrapeComparative(Element lemma) {
        String comparative = Objects.requireNonNull(lemma.selectFirst("div#wStckInf>div#wStckKrz>div+p")).wholeText().split("·")[1].replaceAll("(^[^a-zA-ZäöüÄÖÜß-]+)|([^a-zA-ZäöüÄÖÜß-]+$)", "");
        Matcher comparativeMatcher = Pattern.compile("^[a-zA-ZäöüÄÖÜßé-]+").matcher(comparative);
        if (comparativeMatcher.find()) {
            return comparativeMatcher.group();
        } else {
            throw new RuntimeException("unable to extract plural");
        }
    }

    /**
     * Extracts the superlative form of the adjective from the flexion summary.
     * Typically located as the third part of a dot-separated string under the lemma.
     *
     * @param lemma the HTML element representing the term's lemma container
     * @return the superlative form as a string (e.g., {@code "am schönsten"})
     * @throws RuntimeException if no valid superlative form can be extracted
     */
    String scrapeSuperlative(Element lemma) {
        String superlative = Objects.requireNonNull(lemma.selectFirst("div#wStckInf>div#wStckKrz>div+p")).wholeText().split("·")[2].replaceAll("(^[^a-zA-ZäöüÄÖÜß-]+)|([^a-zA-ZäöüÄÖÜß-]+$)", "");
        Matcher superlativeMatcher = Pattern.compile("^(am )?[a-zA-ZäöüÄÖÜßé-]+").matcher(superlative);
        if (superlativeMatcher.find()) {
            return superlativeMatcher.group();
        } else {
            throw new RuntimeException("unable to extract plural");
        }
    }
}
