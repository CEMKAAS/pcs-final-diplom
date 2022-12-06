import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

    private Map<String, List<PageEntry>> pageEntryAll = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        if (pdfsDir.isDirectory()) {
            for (File item : pdfsDir.listFiles()) {
                var doc = new PdfDocument(new PdfReader(item));
                for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                    var text = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                    var words = text.split("\\P{IsAlphabetic}+");
                    Map<String, Integer> freqs = new HashMap<>();
                    for (var word : words) {
                        if (word.isEmpty()) {
                            continue;
                        }
                        word = word.toLowerCase();
                        freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                    }
                    for (Map.Entry<String, Integer> freqsWord : freqs.entrySet()) {
                        List<PageEntry> pageEntries = new ArrayList<>();
                        if (pageEntryAll.get(freqsWord.getKey()) == null) {
                            pageEntries.add(new PageEntry(item.getName(), i, freqsWord.getValue()));
                            pageEntryAll.put(freqsWord.getKey(), pageEntries);
                        } else {
                            pageEntries = pageEntryAll.get(freqsWord.getKey());
                            pageEntries.add(new PageEntry(item.getName(), i, freqsWord.getValue()));
                            pageEntryAll.put(freqsWord.getKey(), pageEntries);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        Collections.sort(pageEntryAll.get(word), PageEntry::compareTo);
        return pageEntryAll.get(word);
    }
}
