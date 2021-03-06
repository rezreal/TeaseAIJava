package me.goddragon.teaseai.utils.libraries.ripme.ripper.rippers.video;

import me.goddragon.teaseai.utils.libraries.ripme.ripper.VideoRipper;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Http;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PornhubRipper extends VideoRipper {

    private static final String HOST = "pornhub";

    public PornhubRipper(URL url) throws IOException {
        super(url);
    }

    @Override
    public String getHost() {
        return HOST;
    }

    @Override
    public boolean canRip(URL url) {
        Pattern p = Pattern.compile("^https?://[w|m|.]*pornhub\\.com/view_video\\.php\\?viewkey=[a-z0-9]+$");
        Matcher m = p.matcher(url.toExternalForm());
        return m.matches();
    }

    @Override
    public URL sanitizeURL(URL url) throws MalformedURLException {
        return url;
    }

    @Override
    public String getGID(URL url) throws MalformedURLException {
        Pattern p = Pattern.compile("^https?://[wm.]*pornhub\\.com/view_video.php\\?viewkey=([a-z0-9]+)$");
        Matcher m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return m.group(1);
        }

        throw new MalformedURLException(
                "Expected pornhub format:"
                        + "pornhub.com/view_video.php?viewkey=####"
                        + " Got: " + url);
    }

    @Override
    public void rip() throws IOException {
        LOGGER.log(Level.INFO, "    Retrieving " + this.url.toExternalForm());
        Document doc = Http.url(this.url).get();
        String html = doc.body().html();
        Pattern p = Pattern.compile("^.*flashvars_[0-9]+ = (.+});.*$", Pattern.DOTALL);
        Matcher m = p.matcher(html);
        //TeaseLogger.getLogger().log(Level.INFO, html);
        if (m.matches()) {
            String title = null, vidUrl = null;
            try {
                JSONObject json = new JSONObject(m.group(1));

                title = json.getString("video_title");
                title = title.replaceAll("\\+", " ");

                vidUrl = null;
                for (String quality : new String[]{"1080", "720", "480", "240"}) {
                    Pattern pv = Pattern.compile("\"format\":\"(mp4|upshell)\",\"quality\":\"" + quality + "\",\"videoUrl\":\"(.*?)\"");
                    Matcher mv = pv.matcher(html);
                    if (mv.find()) {
                        vidUrl = mv.group(2).replace("\\/", "/");
                        break;
                    }
                }
                if (vidUrl == null) {
                    throw new IOException("Unable to find encrypted video URL at " + this.url);
                }
                addURLToDownload(new URL(vidUrl), HOST + "_" + getGID(this.url));
            } catch (JSONException e) {
                LOGGER.log(Level.SEVERE, "Error while parsing JSON at " + url, e);
                throw e;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error while retrieving video URL at " + url, e);
                throw new IOException(e);
            }
        } else {
            throw new IOException("Failed to download " + this.url + " : could not find 'flashvars'");
        }
        waitForThreads();
    }
}