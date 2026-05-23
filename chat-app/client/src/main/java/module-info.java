module com.chat.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.chat.shared;
    requires java.logging;
    requires org.slf4j;
    requires ch.qos.logback.classic;

    opens com.chat.client.ui to javafx.fxml;
    exports com.chat.client.ui;
}