package com.sequenceiq.mock.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * 
 */
@ApiModel(description = "")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2021-12-10T21:24:30.629+01:00")




public class ApiRoleConfigGroupRef   {
  @JsonProperty("roleConfigGroupName")
  private String roleConfigGroupName = null;

  public ApiRoleConfigGroupRef roleConfigGroupName(String roleConfigGroupName) {
    this.roleConfigGroupName = roleConfigGroupName;
    return this;
  }

  /**
   * The name of the role config group, which uniquely identifies it in a CM installation.
   * @return roleConfigGroupName
  **/
  @ApiModelProperty(value = "The name of the role config group, which uniquely identifies it in a CM installation.")


  public String getRoleConfigGroupName() {
    return roleConfigGroupName;
  }

  public void setRoleConfigGroupName(String roleConfigGroupName) {
    this.roleConfigGroupName = roleConfigGroupName;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApiRoleConfigGroupRef apiRoleConfigGroupRef = (ApiRoleConfigGroupRef) o;
    return Objects.equals(this.roleConfigGroupName, apiRoleConfigGroupRef.roleConfigGroupName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roleConfigGroupName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiRoleConfigGroupRef {\n");
    
    sb.append("    roleConfigGroupName: ").append(toIndentedString(roleConfigGroupName)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

