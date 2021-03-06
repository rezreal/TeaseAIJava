package me.goddragon.teaseai.utils.libraries.ripme.ripper.rippers.video;

import me.goddragon.teaseai.utils.libraries.ripme.ripper.VideoRipper;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Http;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StickyXXXRipper extends VideoRipper {

    private static final String HOST = "stickyxxx";

    public StickyXXXRipper(URL url) throws IOException {
        super(url);
    }

    @Override
    public String getHost() {
        return HOST;
    }

    @Override
    public boolean canRip(URL url) {
        Pattern p = Pattern.compile("^https?://.*stickyxxx\\.com(/)(.*)/$");
        Matcher m = p.matcher(url.toExternalForm());
        return m.matches();
    }

    @Override
    public URL sanitizeURL(URL url) throws MalformedURLException {
        return url;
    }

    @Override
    public String getGID(URL url) throws MalformedURLException {
        Pattern p = Pattern.compile("^https?://.*stickyxxx\\.com(/)(.*)/$");
        Matcher m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return m.group(2);
        }

        throw new MalformedURLException(
                "Expected stickyxxx format:"
                        + "stickyxxx.com/####"
                        + " Got: " + url);
    }

    @Override
    public void rip() throws IOException {
        LOGGER.log(Level.INFO, "Retrieving " + this.url);
        Document doc = Http.url(url).get();
        Elements videos = doc.select(".wp-video > video > source");
        if (videos.isEmpty()) {
            throw new IOException("Could not find Embed code at " + url);
        }
        String vidUrl = videos.attr("src");
        addURLToDownload(new URL(vidUrl), HOST + "_" + getGID(this.url));
        waitForThreads();
    }
}