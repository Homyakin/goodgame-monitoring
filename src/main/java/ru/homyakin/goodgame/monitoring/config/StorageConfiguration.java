package ru.homyakin.goodgame.monitoring.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ru.homyakin.goodgame.storage")
public class StorageConfiguration {
    private Integer newsTtlInHours = 24;
    private Integer tournamentTtlInHours = 12;

    public void setNewsTtlInHours(Integer newsTtlInHours) {
        this.newsTtlInHours = newsTtlInHours;
    }

    public Integer getNewsTtlInHours() {
        return newsTtlInHours;
    }

    public void setTournamentTtlInHours(Integer tournamentTtlInHours) {
        this.tournamentTtlInHours = tournamentTtlInHours;
    }

    public Integer getTournamentTtlInHours() {
        return tournamentTtlInHours;
    }
}
