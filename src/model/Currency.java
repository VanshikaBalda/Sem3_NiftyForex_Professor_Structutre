package forex.model;
public record Currency(String code,String name) { public Currency { code=code.toUpperCase(); } }
