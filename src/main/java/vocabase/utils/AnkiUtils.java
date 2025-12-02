package vocabase.utils;

import org.jsoup.nodes.Element;
import vocabase.model.*;

public class AnkiUtils {

    public static String format(Verb verb) {
        StringBuilder output = new StringBuilder();
        String hr = new Element("hr").html();
        output.append(format(verb.getTerm()));
        output.append(";");
        output.append(format("Definitions", verb.getDefinitions()));
        output.append(hr);
        output.append(format("PPII", verb.getPpii()));
        output.append(hr);
        output.append(format("Preterite", verb.getPreterite()));
        output.append(hr);
        output.append(format("Prepositions", verb.getPrepositions()));
        output.append(hr);
        output.append(format("Synonyms", verb.getSynonyms()));
        output.append(hr);
        output.append(format("Antonyms", verb.getAntonyms()));
        output.append(hr);
        output.append(format("Tags", verb.getTags()));
        output.append(hr);
        output.append(format("Examples", verb.getExamples()));
        return output.toString();
    }

    public static String format(Noun noun) {
        StringBuilder output = new StringBuilder();
        String hr = new Element("hr").html();
        output.append(format(noun.getTerm()));
        output.append(";");
        output.append(format("Definitions", noun.getDefinitions()));
        output.append(hr);
        output.append(format("Article", noun.getArticle()));
        output.append(hr);
        output.append(format("Plural", noun.getPlural()));
        output.append(hr);
        output.append(format("Synonyms", noun.getSynonyms()));
        output.append(hr);
        output.append(format("Antonyms", noun.getAntonyms()));
        output.append(hr);
        output.append(format("Examples", noun.getExamples()));
        return output.toString();
    }

    public static String format(Adjective noun) {
        StringBuilder output = new StringBuilder();
        String hr = new Element("hr").html();
        output.append(format(noun.getTerm()));
        output.append(";");
        output.append(format("Definitions", noun.getDefinitions()));
        output.append(hr);
        output.append(format("Comparative", noun.getComparative()));
        output.append(hr);
        output.append(format("Superlative", noun.getSuperlative()));
        output.append(hr);
        output.append(format("Synonyms", noun.getSynonyms()));
        output.append(hr);
        output.append(format("Antonyms", noun.getAntonyms()));
        output.append(hr);
        output.append(format("Examples", noun.getExamples()));
        return output.toString();
    }

    public static String format(Adverb noun) {
        StringBuilder output = new StringBuilder();
        String hr = new Element("hr").html();
        output.append(format(noun.getTerm()));
        output.append(";");
        output.append(format("Definitions", noun.getDefinitions()));
        output.append(hr);
        output.append(format("Synonyms", noun.getSynonyms()));
        output.append(hr);
        output.append(format("Antonyms", noun.getAntonyms()));
        output.append(hr);
        output.append(format("Examples", noun.getExamples()));
        return output.toString();
    }

    private static String format(String content) {
        return new Element("h1").html(content).toString().replaceAll("\n", "");
    }
    
    private static String format(String title, String[] content) {
        Element parent = new Element("div");
        Element h2 = new Element("h2").html(title);
        Element span = new Element("span");
        StringBuilder combined = new StringBuilder();
        for (int i = 0; i < content.length; i++)
            if (i == (content.length-1))
                combined.append(content[i]);
            else
                combined.append(content[i]).append(" / ");
        span.html(combined.toString());
        parent.appendChild(h2);
        parent.appendChild(span);
        return parent.toString().replaceAll("\n", "");
    }

    private static String format(String title, String content) {
        Element parent = new Element("div");
        Element h2 = new Element("h2").html(title);
        Element span = new Element("span").html(content);
        parent.appendChild(h2);
        parent.appendChild(span);
        return parent.toString().replaceAll("\n", "");
    }

    private static String format(String title, Article content) {
        Element parent = new Element("div");
        Element h2 = new Element("h2").html(title);
        String article = switch (content) {
            case DAS -> "Das";
            case DER -> "Der";
            case DIE -> "Die";
            case DAS_DIE -> "Das/Die";
            case DER_DAS -> "Der/Das";
            case DER_DIE -> "Der/Die";
            case null -> "Unknown";
        };
        Element span = new Element("span").html(article);
        parent.appendChild(h2);
        parent.appendChild(span);
        return parent.toString().replaceAll("\n", "");
    }

    private static String format(String title, Tags content) {
        Element parent = new Element("div");
        Element h2 = new Element("h2").html(title);
        Element span = new Element("span");
        String combined = "";
        if (content.regular()) {
            combined = "Regular / ";
        } else {
            combined = "Irregular / ";
        }
        if (content.accusative()) {
            combined += "Accusative / ";
        }
        if (content.dative()) {
            combined += "Dative / ";
        }
        if (content.genitive()) {
            combined += "Genitive / ";
        }
        if (content.reflexive()) {
            combined += "Reflexive / ";
        }
        if (content.ppiiWithSein()) {
            combined += "PPII Mit Sein / ";
        }
        if (content.separable()) {
            combined += "Separable";
        } else {
            combined += "Inseparable";
        }
        span.html(combined);
        parent.appendChild(h2);
        parent.appendChild(span);
        return parent.toString().replaceAll("\n", "");
    }
}
