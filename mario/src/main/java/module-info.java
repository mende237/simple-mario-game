module com.game.mario {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.google.protobuf;
    requires io.grpc;
    requires io.grpc.stub;
    requires java.annotation;
    requires com.google.common;
    requires io.grpc.protobuf;
    requires org.json;

    opens com.game.mario to javafx.fxml;
    opens com.game.mario.character to javafx.fxml;
    opens com.game.mario.game to javafx.fxml;
    opens com.game.mario.item to javafx.fxml;
    opens com.game.mario.util to javafx.fxml;

    exports com.game.mario;
    exports com.game.mario.character;
    exports com.game.mario.game;
    exports com.game.mario.item;
    exports com.game.mario.util;
    exports proto;
}
