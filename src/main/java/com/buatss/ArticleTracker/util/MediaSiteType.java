package com.buatss.ArticleTracker.util;

import com.buatss.ArticleTracker.model.MediaSite;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum MediaSiteType {
    WP(new MediaSite(1, "Wirtualna Polska", "https://www.wp.pl/")),
    ONET(new MediaSite(2, "Onet", "https://www.onet.pl/")),
    MONEY(new MediaSite(3, "Money", "https://www.money.pl/")),
    WYBORCZA(new MediaSite(4, "Wyborcza", "https://wyborcza.pl/")),
    SE(new MediaSite(5, "SuperExpress", "https://www.se.pl/")),
    BI(new MediaSite(6, "BusinessInsider", "https://businessinsider.com.pl/")),
    PULS_HR(new MediaSite(7, "PulsHR", "https://www.pulshr.pl/")),
    BANKIER(new MediaSite(8, "Bankier", "https://www.bankier.pl/")),
    WNP(new MediaSite(9, "WNP", "https://www.wnp.pl/")),
    NETTG(new MediaSite(10, "netTG", "https://nettg.pl/")),
    BIZNESALERT(new MediaSite(11, "BiznesAlert", "https://biznesalert.pl/"));


    private final MediaSite mediaSite;

    MediaSiteType(MediaSite mediaSite) {
        this.mediaSite = mediaSite;
    }

    public static List<MediaSite> getAllMedias() {
        return Arrays.stream(MediaSiteType.values())
                .map(MediaSiteType::getMediaSite)
                .collect(Collectors.toList());
    }
}