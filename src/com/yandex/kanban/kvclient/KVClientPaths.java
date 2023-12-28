package com.yandex.kanban.kvclient;

public enum KVClientPaths {
    REGISTER("/register"),
    SAVE("/save"),
    LOAD("/load");
    private final String result;

    KVClientPaths(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
