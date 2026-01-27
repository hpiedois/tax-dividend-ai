package com.taxdividend.backend.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ParseMakePdf200Response
 */

@JsonTypeName("parseMakePdf_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-26T22:31:55.344143+01:00[Europe/Zurich]")
public class ParseMakePdf200Response {

  private String rawText;

  public ParseMakePdf200Response rawText(String rawText) {
    this.rawText = rawText;
    return this;
  }

  /**
   * Get rawText
   * @return rawText
  */
  
  @Schema(name = "rawText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rawText")
  public String getRawText() {
    return rawText;
  }

  public void setRawText(String rawText) {
    this.rawText = rawText;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParseMakePdf200Response parseMakePdf200Response = (ParseMakePdf200Response) o;
    return Objects.equals(this.rawText, parseMakePdf200Response.rawText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rawText);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ParseMakePdf200Response {\n");
    sb.append("    rawText: ").append(toIndentedString(rawText)).append("\n");
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

