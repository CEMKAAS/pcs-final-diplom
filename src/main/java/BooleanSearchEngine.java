import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.*;
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
                            Collections.sort(pageEntryAll.get(freqsWord.getKey()), PageEntry::compareTo);
                        }
                    }
                }
            }
        }

    }

    @Override
    public List<PageEntry> search(String word) {
        List<String> stopWord = new ArrayList<>();
        List<PageEntry> allWord = new ArrayList<>();
        List<PageEntry> endWord = new ArrayList<>();
        try (BufferedReader textFile = new BufferedReader(new FileReader("stop-ru.txt"))) {

            String s;
            while ((s = textFile.readLine()) != null) {
                stopWord.add(s);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String> list = new ArrayList<>();
        String[] wordMassive = word.split(" ");
        for (String wordSearch : wordMassive) {
            for (String wordStopList : stopWord) {
                if (!wordSearch.equals(wordStopList)) {
                    continue;
                }
                list.add(wordSearch);
            }
        }

        for (Map.Entry<String, List<PageEntry>> freqsWord1 : pageEntryAll.entrySet()) {
            for (String list1 : list) {
                allWord = pageEntryAll.get(list1);
            }
        }

        for (PageEntry o : allWord) {
            for (PageEntry a : endWord) {
                if (o.getPdfName().equals(a.getPdfName()) && o.getCount() == a.getCount()) {
                    endWord.add(new PageEntry(o.getPdfName(), o.getPage(), (o.getCount() + a.getCount())));
                    break;
                }
            }
        }
        if (endWord != null) {
            return endWord;
        } else {
            return Collections.emptyList();
        }
    }

}