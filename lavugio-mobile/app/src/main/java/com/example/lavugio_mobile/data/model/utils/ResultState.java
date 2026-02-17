package com.example.lavugio_mobile.data.model.utils;

public abstract class ResultState {

    public static class Success extends ResultState { }

    public static class Error extends ResultState {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
