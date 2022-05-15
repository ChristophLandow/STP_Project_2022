package de.uniks.pioneers.dto;

public record CreateMemberDto(
        boolean ready,
        String password
) {}
