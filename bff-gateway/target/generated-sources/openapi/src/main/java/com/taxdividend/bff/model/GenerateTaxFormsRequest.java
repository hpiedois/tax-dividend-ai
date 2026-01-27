package com.taxdividend.bff.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GenerateTaxFormsRequest
 */

@JsonTypeName("generateTaxForms_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-26T22:26:13.283645+01:00[Europe/Zurich]")
public class GenerateTaxFormsRequest {

  @Valid
  private List<String> dividendIds;

  public GenerateTaxFormsRequest dividendIds(List<String> dividendIds) {
    this.dividendIds = dividendIds;
    return this;
  }

  public GenerateTaxFormsRequest addDividendIdsItem(String dividendIdsItem) {
    if (this.dividendIds == null) {
      this.dividendIds = new ArrayList<>();
    }
    this.dividendIds.add(dividendIdsItem);
    return this;
  }

  /**
   * Get dividendIds
   * @return dividendIds
  */
  
  @Schema(name = "dividendIds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dividendIds")
  public List<String> getDividendIds() {
    return dividendIds;
  }

  public void setDividendIds(List<String> dividendIds) {
    this.dividendIds = dividendIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GenerateTaxFormsRequest generateTaxFormsRequest = (GenerateTaxFormsRequest) o;
    return Objects.equals(this.dividendIds, generateTaxFormsRequest.dividendIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dividendIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GenerateTaxFormsRequest {\n");
    sb.append("    dividendIds: ").append(toIndentedString(dividendIds)).append("\n");
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

