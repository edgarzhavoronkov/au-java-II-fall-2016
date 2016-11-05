package ru.spbau.mit.util;

/**
 * Created by Эдгар on 05.11.2016.
 * small enum for request types
 */
public enum RequestType {
    LIST {
        @Override
        public String toString() {
            return "list";
        }
    },
    GET {
        @Override
        public String toString() {
            return "get";
        }
    }
}
