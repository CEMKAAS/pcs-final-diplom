import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

    private List<PageEntryTwo> listPage = new ArrayList<>();
    private Map<String, List<PageEntry>> freqs1 = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
//         если объект представляет каталог
        if (pdfsDir.isDirectory()) {
//         получаем все вложенные объекты в каталоге
            for (File item : pdfsDir.listFiles()) {

                var doc = new PdfDocument(new PdfReader(item));

                for (int i = 1; i <= doc.getNumberOfPages(); i++) {

                    var text = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                    var words = text.split("\\P{IsAlphabetic}+");
                    Map<String, Integer> freqs = new HashMap<>();// мапа, где ключом будет слово, а значением - частота

                    for (var word : words) { // перебираем слова
                        if (word.isEmpty()) {
                            continue;
                        }
                        word = word.toLowerCase();
                        freqs.put(word, freqs.getOrDefault(word, 0) + 1);

                    }

                    for (Map.Entry<String, Integer> freqsWord : freqs.entrySet()) {
                        List<PageEntry> pageEntries = new ArrayList<>();
                        pageEntries.add(new PageEntry(item.getName(), i, freqsWord.getValue()));
                        freqs1.put(freqsWord.getKey(),pageEntries);
                    }

                }


            }
        }
        System.out.println(freqs1.toString());
    }

    @Override
    public List<PageEntry> search(String word) {
        freqs1.get(word);
        return Collections.emptyList();
    }
}
