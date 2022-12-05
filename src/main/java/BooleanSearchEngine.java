import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

    private List<PageEntry> pageEntries = new ArrayList<>();
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
                    List<PageEntry> listPage = new ArrayList<>();

                    for (Map.Entry<String, Integer> freqsWord : freqs.entrySet()) {
                        if (freqsWord.getKey().isEmpty()) {
                            continue;
                        }
                        listPage.add(new PageEntry(item.getName(), i, freqsWord.getValue()));
                        freqs1.put(freqsWord.getKey(),listPage);

                    }
//                    for (Map.Entry<String, Integer> freqsWord : freqs.entrySet()) {
//
//                    }

                }
            }

            // прочтите тут все pdf и сохраните нужные данные,
            // тк во время поиска сервер не должен уже читать файлы
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        return Collections.emptyList();
    }
}
