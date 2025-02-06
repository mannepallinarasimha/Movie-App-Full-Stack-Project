package com.moviemix.MovieApi.entity;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String textBody) {
}
