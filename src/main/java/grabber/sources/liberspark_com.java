package grabber.sources;

import grabber.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import system.init;

public class liberspark_com implements Source {
    private final Novel novel;
    private Document toc;

    public liberspark_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            toc = Jsoup.connect(novel.novelLink).get();
            Elements chapterLinks = toc.select("#novel-chapters-list a.text-links");
            for(Element chapterLink: chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR]Could not connect to webpage. (" + e.getMessage() + ")");
            }
        }
        Collections.reverse(chapterList);
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL).get();
            chapterBody = doc.select("#reader-content").first();
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

        metadata.setTitle(toc.select("h1[style=text-align:left]").first().text());
        metadata.setAuthor(toc.select(".novel-author-info a h4").first().text());
        metadata.setDescription(toc.select(".novel-synopsis").first().text());
        metadata.setBufferedCover(toc.select("img#uploaded-cover-image").attr("abs:src"));

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        blacklistedTags.add("div.ad-wrapper");
        return blacklistedTags;
    }

    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
