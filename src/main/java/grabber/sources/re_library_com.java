package grabber.sources;

import grabber.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import system.init;

public class re_library_com implements Source {
    private final Novel novel;
    private Document toc;

    public re_library_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            toc = Jsoup.connect(novel.novelLink).get();
            Elements chapterLinks = toc.select(".su-accordion a");
            for(Element chapterLink: chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR]Could not connect to webpage. (" + e.getMessage() + ")");
            }
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL).get();
            chapterBody = doc.select(".entry-content").first();
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR]Could not connect to webpage. (" + e.getMessage() + ")");
            }
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        metadata.setTitle(toc.select(".entry-title").first().text());
        metadata.setBufferedCover(toc.select("img.rounded").attr("abs:src"));

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        blacklistedTags.add(".sharedaddy");
        blacklistedTags.add(".code-block");
        blacklistedTags.add(".ezoic-adpicker-ad");
        blacklistedTags.add(".ezoic-ad");
        blacklistedTags.add(".su-button");
        blacklistedTags.add("table:has(a[href])");
        blacklistedTags.add("div[style=margin:0 auto;width:100px]");
        blacklistedTags.add(".prevPageLink");
        blacklistedTags.add(".nextPageLink");
        blacklistedTags.add("a:contains(Next)");
        blacklistedTags.add("a:contains(Previous)");
        blacklistedTags.add("table:has(span[style=font-size:8pt;color:#999999])");
        blacklistedTags.add("h2:contains(References)");
        blacklistedTags.add("table#fixed");
        blacklistedTags.add("a:contains(Index)");
        return blacklistedTags;
    }

    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
