module com.chat.shared {
    requires java.logging;
    exports com.chat.shared.model;
    exports com.chat.shared.router;
    exports com.chat.shared.command;
    exports com.chat.shared.util;
}