package vocabase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import vocabase.model.*;
import vocabase.scraper.TermScraper;
import vocabase.service.NotionService;
import vocabase.utils.AnkiUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
public class DataController {
    private final vocabase.service.NotionService notion;
    private final TermScraper scraper;

    @Autowired
    public DataController(NotionService notion) {
        this.notion = notion;
        this.scraper = new TermScraper();
    }

    @GetMapping("/search")
    public Term search(@RequestParam String query){
        return this.scraper.scrape(query);
    }

    @GetMapping("/notion/collections")
    public String[] getCollections() {
        return this.notion.fetchCollections().keySet().toArray(new String[0]);
    }

    @GetMapping("/notion/groups/{collection}")
    public String[] getGroups(@PathVariable String collection) {
        return this.notion.fetchGroups(collection).keySet().toArray(new String[0]);
    }

    @PostMapping("/notion/db")
    public void selectDB(@RequestBody Map<String, String> request) {
        this.notion.selectDB(request.get("collection"), request.get("group"));
    }

    @PostMapping("/notion/save")
    public Map<String, Boolean> saveToNotion(@RequestBody Term term) {
        try {
            this.notion.insert(term);
            return Map.of("success", Boolean.TRUE);
        } catch (RuntimeException e) {
            System.out.println(e);
            return Map.of("success", Boolean.FALSE);
        }
    }

    @GetMapping("/notion/download")
    public ResponseEntity<byte[]> download() {
        var records = this.notion.fetchAllRecords();
        StringBuilder combined = new StringBuilder();
        for (var verb : records.get(WordType.VERB))
            combined.append(AnkiUtils.format((Verb) verb)).append("\n");
        for (var noun : records.get(WordType.NOUN))
            combined.append(AnkiUtils.format((Noun) noun)).append("\n");
        for (var adjective : records.get(WordType.ADJECTIVE))
            combined.append(AnkiUtils.format((Adjective) adjective)).append("\n");
        for (var adverb : records.get(WordType.ADVERB))
            combined.append(AnkiUtils.format((Adverb) adverb)).append("\n");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDisposition(ContentDisposition.attachment().filename("anki.txt").build());
        return new ResponseEntity<>(combined.toString().getBytes(StandardCharsets.UTF_8), headers, HttpStatus.OK);
    }
}
