package com.taxdividend.bff.model;

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
 * GenerateTaxForms200Response
 */

@JsonTypeName("generateTaxForms_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-26T22:26:13.283645+01:00[Europe/Zurich]")
public class GenerateTaxForms200Response {

  private String formUrl;

  public GenerateTaxForms200Response formUrl(String formUrl) {
    this.formUrl = formUrl;
    return this;
  }

  /**
   * Get formUrl
   * @return formUrl
  */
  
  @Schema(name = "formUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("formUrl")
  public String getFormUrl() {
    return formUrl;
  }

  public void setFormUrl(String formUrl) {
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
    GenerateTaxForms200Response generateTaxForms200Response = (GenerateTaxForms200Response) o;
    return Objects.equals(this.formUrl, generateTaxForms200Response.formUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GenerateTaxForms200Response {\n");
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

