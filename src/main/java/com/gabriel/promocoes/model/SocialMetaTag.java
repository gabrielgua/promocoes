package com.gabriel.promocoes.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter @Setter @ToString
public class SocialMetaTag implements Serializable {

    private String site;
    private String title;
    private String url;
    private String image;
}
