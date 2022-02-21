package com.gabriel.promocoes.service;

import com.gabriel.promocoes.model.SocialMetaTag;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SocialMetaTagService {

    private static Logger log = LoggerFactory.getLogger(SocialMetaTagService.class);

    public SocialMetaTag getSocialMetaTagByUrl(String url) {
        SocialMetaTag twitter = getTwitterCardByUrl(url);
        if (twitter.getTitle() != null && !twitter.getTitle().isEmpty()) {
            System.out.println(twitter.toString());
            return twitter;
        }

        SocialMetaTag op = getOpenGraphByUrl(url);
        if (op.getTitle() != null && !op.getTitle().isEmpty()) {
            System.out.println(op.toString());
            return op;
        }

        return null;
    }


    private SocialMetaTag getOpenGraphByUrl(String url) {
        SocialMetaTag socialMetaTag = new SocialMetaTag();
        try {
            Document document = Jsoup.connect(url).get();
            socialMetaTag.setTitle(document.head().select("meta[property=og:title]").attr("content"));
            socialMetaTag.setSite(document.head().select("meta[property=og:site_name]").attr("content"));
            socialMetaTag.setImage(document.head().select("meta[property=og:image]").attr("content"));
            socialMetaTag.setUrl(document.head().select("meta[property=og:url]").attr("content"));
        } catch (IOException e) {
           log.error(e.getMessage(), e.getCause());
        }
        return socialMetaTag;
    }

    private SocialMetaTag getTwitterCardByUrl(String url) {
        SocialMetaTag socialMetaTag = new SocialMetaTag();
        try {
            Document document = Jsoup.connect(url).get();
            socialMetaTag.setTitle(document.head().select("meta[name=twitter:title]").attr("content"));
            socialMetaTag.setSite(document.head().select("meta[name=twitter:site]").attr("content"));
            socialMetaTag.setImage(document.head().select("meta[name=twitter:image]").attr("content"));
            socialMetaTag.setUrl(document.head().select("meta[name=twitter:url]").attr("content"));
        } catch (IOException e) {
            log.error(e.getMessage(), e.getCause());
        }
        return socialMetaTag;
    }
}
