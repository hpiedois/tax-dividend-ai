package com.taxdividend.bff.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.taxdividend.bff.model.DividendData;
import com.taxdividend.bff.model.ParseStatementResponseMetadata;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ParseStatementResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-01T09:58:25.268465+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class ParseStatementResponse {

  @Valid
  private List<@Valid DividendData> dividends = new ArrayList<>();

  private @Nullable ParseStatementResponseMetadata metadata;

  public ParseStatementResponse dividends(List<@Valid DividendData> dividends) {
    this.dividends = dividends;
    return this;
  }

  public ParseStatementResponse addDividendsItem(DividendData dividendsItem) {
    if (this.dividends == null) {
      this.dividends = new ArrayList<>();
    }
    this.dividends.add(dividendsItem);
    return this;
  }

  /**
   * Get dividends
   * @return dividends
   */
  @Valid 
  @Schema(name = "dividends", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dividends")
  public List<@Valid DividendData> getDividends() {
    return dividends;
  }

  public void setDividends(List<@Valid DividendData> dividends) {
    this.dividends = dividends;
  }

  public ParseStatementResponse metadata(@Nullable ParseStatementResponseMetadata metadata) {
    this.metadata = metadata;
    return this;
  }

  /**
   * Get metadata
   * @return metadata
   */
  @Valid 
  @Schema(name = "metadata", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("metadata")
  public @Nullable ParseStatementResponseMetadata getMetadata() {
    return metadata;
  }

  public void setMetadata(@Nullable ParseStatementResponseMetadata metadata) {
    this.metadata = metadata;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParseStatementResponse parseStatementResponse = (ParseStatementResponse) o;
    return Objects.equals(this.dividends, parseStatementResponse.dividends) &&
        Objects.equals(this.metadata, parseStatementResponse.metadata);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dividends, metadata);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ParseStatementResponse {\n");
    sb.append("    dividends: ").append(toIndentedString(dividends)).append("\n");
    sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

