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
        List<PageEntry> allWord2 = new ArrayList<>();

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
        String[] wordMassive = word.toLowerCase().split("\\P{IsAlphabetic}+");
        List<String> list = new ArrayList<>();
        for (String wordSearch : wordMassive) {
            if (!stopWord.contains(wordSearch)) {
                list.add(wordSearch);
            }
        }

        for (String list1 : list) {
            allWord2.addAll(pageEntryAll.get(list1));
            break;
        }

        int value = -1;
        for (String list1 : list) {
            if (value == -1) {
                allWord = pageEntryAll.get(list1);
                allWord.clear();
                value++;
                continue;
            }
            allWord = pageEntryAll.get(list1);
            for (PageEntry o : allWord) {
                value = 0;
                for (PageEntry a : allWord2) {
                    if (o.getPdfName().equals(a.getPdfName()) && o.getPage() == a.getPage()) {
                        allWord2.remove(a);
                        allWord2.add(new PageEntry(a.getPdfName(), a.getPage(), (o.getCount() + a.getCount())));
                        value++;
                        break;
                    }
                }
                if (value == 0) {
                    allWord2.add(o);
                }
            }
        }

        allWord2.sort(PageEntry::compareTo);
        if (allWord2 != null) {
            return allWord2 ;
        } else {
            return Collections.emptyList();
        }
    }

}