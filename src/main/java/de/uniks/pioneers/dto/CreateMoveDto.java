package de.uniks.pioneers.dto;

import de.uniks.pioneers.model.Resources;

 public record CreateMoveDto(
        String action,
        CreateBuildingDto building,
        Resources resources,
        String partner
) {

  public CreateMoveDto(String action, Resources resources, String partner) {
   this(action,null,resources,partner);
  }

  public CreateMoveDto(String action, CreateBuildingDto building) {
   this(action, building, null, null);
  }

 }
