public class PageEntryTwo {
    private final String pdfName;
    private final String word;
    private final int page;
    private final int count;

    public PageEntryTwo(String pdfName, String word, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
        this.word = word;
    }

    public String getPdfName() {
        return pdfName;
    }

    public String getWord() {
        return word;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }
}

