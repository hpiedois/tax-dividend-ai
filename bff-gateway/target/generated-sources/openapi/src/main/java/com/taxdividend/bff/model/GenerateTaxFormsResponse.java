package com.taxdividend.bff.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GenerateTaxFormsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-01T09:58:25.268465+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class GenerateTaxFormsResponse {

  private @Nullable String formUrl;

  public GenerateTaxFormsResponse formUrl(@Nullable String formUrl) {
    this.formUrl = formUrl;
    return this;
  }

  /**
   * Get formUrl
   * @return formUrl
   */
  
  @Schema(name = "formUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("formUrl")
  public @Nullable String getFormUrl() {
    return formUrl;
  }

  public void setFormUrl(@Nullable String formUrl) {
    this.formUrl = formUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GenerateTaxFormsResponse generateTaxFormsResponse = (GenerateTaxFormsResponse) o;
    return Objects.equals(this.formUrl, generateTaxFormsResponse.formUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GenerateTaxFormsResponse {\n");
    sb.append("    formUrl: ").append(toIndentedString(formUrl)).append("\n");
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

