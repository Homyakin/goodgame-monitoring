package ru.homyakin.goodgame.monitoring.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiComments(
    CommentInfo info
) {
}

record CommentInfo(
    @JsonProperty("cqty")
    Long count
) {
}
