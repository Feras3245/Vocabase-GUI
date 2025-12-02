package vocabase.scraper;

import org.jsoup.nodes.Element;
import vocabase.model.Article;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for scraping grammatical properties specific to German nouns
 * from their HTML representation on {@code www.verben.de}.
 *
 * <p>Used internally by {@link vocabase.scraper.TermScraper} when handling terms of type NOUN.
 */
class NounScraper {

    /**
     * Extracts the grammatical gender (article) of the noun based on span elements in the metadata.
     * Combines multiple spans when multiple genders are present (e.g., for gender-neutral/plural entries).
     *
     * @param meta the metadata element (usually from {@code span.rInf})
     * @return the {@link Article} enum representing the noun's gender
     * @throws RuntimeException if no recognizable article could be extracted
     */
    Article scrapeArticle(Element meta) {
        boolean isFeminine = !Objects.isNull(meta.selectFirst("span[title=\"Genus feminin\"]"));
        boolean isNeutral = !Objects.isNull(meta.selectFirst("span[title=\"Genus neutral\"]"));
        boolean isMasculine = !Objects.isNull(meta.selectFirst("span[title=\"Genus maskulin\"]"));
        if (isFeminine && (!isNeutral) && (!isMasculine)) return Article.DIE;
        if (isNeutral && (!isFeminine) && (!isMasculine)) return Article.DAS;
        if (isMasculine && (!isFeminine) && (!isNeutral)) return Article.DER;
        if (isMasculine && isFeminine && (!isNeutral)) return Article.DER_DIE;
        if (isMasculine && isNeutral && (!isFeminine)) return Article.DER_DAS;
        if (isNeutral && isFeminine && (!isMasculine)) return Article.DAS_DIE;
        else throw new RuntimeException("unable to scrape article");
    }

    /**
     * Extracts the plural form of the noun from the lemma's flexion summary.
     * This is usually found as the second part in a dot-separated flexion string.
     *
     * @param lemma the HTML element representing the term's lemma container
     * @return the plural form as a string
     * @throws RuntimeException if no valid plural form can be extracted
     */
    String scrapePlural(Element lemma) {
        String plural = Objects.requireNonNull(lemma.selectFirst("div#wStckInf>div#wStckKrz>div+p")).wholeText().split("·")[1].replaceAll("(^[^a-zA-ZäöüÄÖÜß-]+)|([^a-zA-ZäöüÄÖÜß-]+$)", "");
        Matcher pluralMatcher = Pattern.compile("^[a-zA-ZäöüÄÖÜßé-]+").matcher(plural);
        if (pluralMatcher.find()) {
            return pluralMatcher.group();
        } else {
            throw new RuntimeException("unable to extract plural");
        }
    }
}
