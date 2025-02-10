package com.ftcs.rateandreview.projection;

import java.time.LocalDateTime;

public interface DriverReviewProjection {
    String getFullName();
    Integer getRating();
    String getReviewText();
    LocalDateTime getCreateAt();
    LocalDateTime getUpdateAt();
}
